package com.example.gymapp.ui.nutrition

import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.GymApp
import com.example.gymapp.data.MealAnalysisException
import com.example.gymapp.data.MealPhotoLoader
import com.example.gymapp.domain.ActivityLevel
import com.example.gymapp.domain.AnalysisConfidence
import com.example.gymapp.domain.BodyProfile
import com.example.gymapp.domain.EnergyBreakdown
import com.example.gymapp.domain.FoodItem
import com.example.gymapp.domain.Macros
import com.example.gymapp.domain.Meal
import com.example.gymapp.domain.MealAnalysis
import com.example.gymapp.domain.MealSource
import com.example.gymapp.domain.NutritionGoal
import com.example.gymapp.domain.Sex
import com.example.gymapp.domain.VietFoodDb
import com.example.gymapp.domain.energyBreakdown
import com.example.gymapp.domain.macrosFor
import com.example.gymapp.service.RestTimerStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

data class MealDraft(
    val title: String,
    val source: MealSource,
    val confidence: AnalysisConfidence?,
    val items: List<FoodItem>,
    /** Set when editing a saved meal. */
    val mealId: Long? = null,
    val photoName: String? = null,
    /** The photo was taken for this draft and must be deleted if the draft is dropped. */
    val ownsPhoto: Boolean = false,
)

sealed interface AnalysisState {
    data object Idle : AnalysisState
    data class Analyzing(val source: MealSource) : AnalysisState
    data class Review(val draft: MealDraft) : AnalysisState
    data class Failed(val message: String) : AnalysisState
}

data class NutritionUiState(
    val day: LocalDate = LocalDate.now(),
    val meals: List<Meal> = emptyList(),
    val eaten: Macros = Macros(),
    val targets: Macros = Macros(),
    val goal: NutritionGoal = NutritionGoal.LEAN_BULK,
    val aiConfigured: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
class NutritionViewModel(private val app: GymApp) : AndroidViewModel(app) {
    private val repository = app.repository
    private val analyzer = app.mealAnalyzer
    private val photos = app.mealPhotos
    private val zone = ZoneId.systemDefault()
    private val day = MutableStateFlow(LocalDate.now())
    private val goal = MutableStateFlow(NutritionGoal.fromKey(RestTimerStore.readNutritionGoal(app)))
    private val activity = MutableStateFlow(ActivityLevel.fromKey(RestTimerStore.readActivityLevel(app)))
    private val _profile = MutableStateFlow(RestTimerStore.readBodyProfile(app))
    val profile: StateFlow<BodyProfile> = _profile
    private val _analysis = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysis: StateFlow<AnalysisState> = _analysis
    private var analysisJob: Job? = null

    val energy: StateFlow<EnergyBreakdown> = combine(_profile, activity, goal) { profile, activity, goal ->
        energyBreakdown(profile, activity, goal)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, energyBreakdown(_profile.value, activity.value, goal.value))

    private val targets: StateFlow<Macros> = energy
        .map { macrosFor(it.targetKcal, _profile.value.weightKg, it.goal) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, macrosFor(energy.value.targetKcal, _profile.value.weightKg, goal.value))

    private val mealsOfDay = day.flatMapLatest { date -> repository.observeMeals(startOf(date), startOf(date.plusDays(1))) }

    val state: StateFlow<NutritionUiState> = combine(day, mealsOfDay, goal, targets) { date, meals, goal, targets ->
        NutritionUiState(
            day = date,
            meals = meals,
            eaten = meals.fold(Macros()) { acc, meal -> acc + meal.macros },
            targets = targets,
            goal = goal,
            aiConfigured = analyzer.isConfigured,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NutritionUiState(aiConfigured = analyzer.isConfigured))

    val todayEaten: StateFlow<Macros> = repository
        .observeMeals(startOf(LocalDate.now()), startOf(LocalDate.now().plusDays(1)))
        .map { meals -> meals.fold(Macros()) { acc, meal -> acc + meal.macros } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Macros())

    val todayTargets: StateFlow<Macros> = targets

    /** Distinct meals of the last 30 days, newest first, for one-tap re-logging. */
    val recentMeals: StateFlow<List<Meal>> = repository
        .observeMeals(startOf(LocalDate.now().minusDays(30)), startOf(LocalDate.now().plusDays(1)))
        .map { meals -> meals.distinctBy { VietFoodDb.normalize(it.title) }.take(20) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Body metrics live in Profile, so re-read them whenever the screen comes back. */
    fun refreshTargets() {
        _profile.value = RestTimerStore.readBodyProfile(app)
    }

    fun setGoal(next: NutritionGoal) {
        goal.value = next
        RestTimerStore.saveNutritionGoal(app, next.key)
    }

    fun setActivity(next: ActivityLevel) {
        activity.value = next
        RestTimerStore.saveActivityLevel(app, next.key)
    }

    fun setAgeAndSex(ageYears: Int, sex: Sex) {
        RestTimerStore.saveAgeAndSex(app, ageYears, sex.key)
        refreshTargets()
    }

    fun shiftDay(days: Long) {
        val next = day.value.plusDays(days)
        if (!next.isAfter(LocalDate.now())) day.value = next
    }

    fun analyzePhoto(uri: Uri) {
        if (_analysis.value is AnalysisState.Analyzing) return
        _analysis.value = AnalysisState.Analyzing(MealSource.PHOTO)
        analysisJob = viewModelScope.launch {
            var photoName: String? = null
            _analysis.value = try {
                val jpeg = MealPhotoLoader.loadForUpload(app, uri)
                photoName = photos.save(jpeg)
                reviewOrFailure(analyzer.analyzePhoto(jpeg), MealSource.PHOTO, photoName)
            } catch (e: CancellationException) {
                photos.delete(photoName)
                throw e
            } catch (e: Exception) {
                failure(e)
            }
            if (_analysis.value !is AnalysisState.Review) photos.delete(photoName)
        }
    }

    fun analyzeText(description: String) {
        if (description.isBlank() || _analysis.value is AnalysisState.Analyzing) return
        _analysis.value = AnalysisState.Analyzing(MealSource.TEXT)
        analysisJob = viewModelScope.launch {
            _analysis.value = try {
                reviewOrFailure(analyzer.analyzeText(description), MealSource.TEXT, null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                failure(e)
            }
        }
    }

    fun startManual() {
        if (_analysis.value is AnalysisState.Analyzing) return
        _analysis.value = AnalysisState.Review(MealDraft("Bữa ăn", MealSource.MANUAL, null, emptyList()))
    }

    fun editMeal(meal: Meal) {
        if (_analysis.value is AnalysisState.Analyzing) return
        _analysis.value = AnalysisState.Review(
            MealDraft(meal.title, meal.source, meal.confidence, meal.items, mealId = meal.id, photoName = meal.photoName),
        )
    }

    fun repeatMeal(meal: Meal) {
        if (_analysis.value is AnalysisState.Analyzing) return
        _analysis.value = AnalysisState.Review(MealDraft(meal.title, MealSource.MANUAL, null, meal.items))
    }

    fun updateItem(index: Int, item: FoodItem) = editDraft { draft ->
        draft.copy(items = draft.items.mapIndexed { i, old -> if (i == index) item else old })
    }

    fun removeItem(index: Int) = editDraft { draft ->
        draft.copy(items = draft.items.filterIndexed { i, _ -> i != index })
    }

    fun addItem(item: FoodItem) = editDraft { draft -> draft.copy(items = draft.items + item) }

    fun setDraftTitle(title: String) = editDraft { it.copy(title = title) }

    fun saveDraft() {
        val draft = (_analysis.value as? AnalysisState.Review)?.draft ?: return
        if (draft.items.isEmpty()) return
        _analysis.value = AnalysisState.Idle
        val date = day.value
        val eatenAt = if (date == LocalDate.now()) {
            System.currentTimeMillis()
        } else {
            date.atTime(LocalTime.NOON).atZone(zone).toInstant().toEpochMilli()
        }
        viewModelScope.launch {
            if (draft.mealId != null) {
                repository.updateMeal(draft.mealId, draft.title, draft.items)
            } else {
                repository.saveMeal(draft.title, draft.source, draft.confidence, draft.items, eatenAt, draft.photoName)
            }
        }
    }

    fun dismissAnalysis() {
        analysisJob?.cancel()
        val draft = (_analysis.value as? AnalysisState.Review)?.draft
        if (draft?.ownsPhoto == true) photos.delete(draft.photoName)
        _analysis.value = AnalysisState.Idle
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            repository.deleteMeal(meal.id)
            photos.delete(meal.photoName)
        }
    }

    fun photoFile(name: String?) = name?.let(photos::file)

    private fun reviewOrFailure(result: MealAnalysis, source: MealSource, photoName: String?): AnalysisState {
        if (!result.isFood || result.items.isEmpty()) {
            return AnalysisState.Failed("Không nhận ra đồ ăn. Thử chụp gần hơn, đủ sáng, hoặc mô tả bằng chữ.")
        }
        return AnalysisState.Review(
            MealDraft(
                title = result.dish.ifBlank { result.items.first().name },
                source = source,
                confidence = result.confidence,
                items = result.items,
                photoName = photoName,
                ownsPhoto = photoName != null,
            ),
        )
    }

    private fun failure(e: Exception): AnalysisState = AnalysisState.Failed(
        if (e is MealAnalysisException) e.message ?: "Phân tích thất bại"
        else "Không đọc được ảnh: ${e.message ?: e.javaClass.simpleName}",
    )

    private fun editDraft(transform: (MealDraft) -> MealDraft) {
        val current = _analysis.value as? AnalysisState.Review ?: return
        _analysis.value = AnalysisState.Review(transform(current.draft))
    }

    private fun startOf(date: LocalDate): Long = date.atStartOfDay(zone).toInstant().toEpochMilli()
}
