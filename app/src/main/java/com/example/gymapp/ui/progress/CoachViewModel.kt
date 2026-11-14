package com.example.gymapp.ui.progress

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.GymApp
import com.example.gymapp.data.CoachStore
import com.example.gymapp.data.MealAnalysisException
import com.example.gymapp.domain.ActivityLevel
import com.example.gymapp.domain.CoachReport
import com.example.gymapp.domain.NutritionGoal
import com.example.gymapp.domain.WeeklyDigest
import com.example.gymapp.domain.energyBreakdown
import com.example.gymapp.domain.macrosFor
import com.example.gymapp.service.RestTimerStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

data class CoachUiState(
    val report: CoachReport? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val configured: Boolean = false,
)

class CoachViewModel(private val app: GymApp) : AndroidViewModel(app) {
    private val _state = MutableStateFlow(
        CoachUiState(report = CoachStore.load(app), configured = app.coach.isConfigured),
    )
    val state: StateFlow<CoachUiState> = _state

    fun generate() {
        if (_state.value.loading) return
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            _state.value = try {
                val digest = buildDigest()
                if (!digest.hasTraining && digest.mealDaysLogged == 0) {
                    _state.value.copy(loading = false, error = "Chưa có buổi tập hay bữa ăn nào trong 7 ngày qua để tổng kết.")
                } else {
                    val report = app.coach.review(digest)
                    CoachStore.save(app, report)
                    _state.value.copy(loading = false, report = report)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: MealAnalysisException) {
                _state.value.copy(loading = false, error = e.message)
            } catch (e: Exception) {
                _state.value.copy(loading = false, error = "Không tạo được tổng kết: ${e.message ?: e.javaClass.simpleName}")
            }
        }
    }

    fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }

    private suspend fun buildDigest(): WeeklyDigest {
        val now = System.currentTimeMillis()
        val zone = ZoneId.systemDefault()
        val snapshot = app.repository.observeGym().first()
        val meals = app.repository.getMeals(now - 14 * DAY_MS, now + 1)
        val profile = RestTimerStore.readBodyProfile(app)
        val goal = NutritionGoal.fromKey(RestTimerStore.readNutritionGoal(app))
        val activity = ActivityLevel.fromKey(RestTimerStore.readActivityLevel(app))
        val targets = macrosFor(energyBreakdown(profile, activity, goal).targetKcal, profile.weightKg, goal)
        return WeeklyDigest.build(
            exercises = snapshot.exercises,
            sessions = snapshot.sessions,
            sets = snapshot.sets,
            meals = meals,
            profile = profile,
            goal = goal,
            targets = targets,
            nowMs = now,
            dayKey = { ms -> Instant.ofEpochMilli(ms).atZone(zone).toLocalDate().toEpochDay() },
        )
    }

    private companion object {
        const val DAY_MS = 24L * 60 * 60 * 1000
    }
}
