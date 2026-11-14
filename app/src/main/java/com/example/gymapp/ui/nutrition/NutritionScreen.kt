package com.example.gymapp.ui.nutrition

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymapp.domain.ActivityLevel
import com.example.gymapp.domain.AnalysisConfidence
import com.example.gymapp.domain.BodyProfile
import com.example.gymapp.domain.EnergyBreakdown
import com.example.gymapp.domain.FoodItem
import com.example.gymapp.domain.Macros
import com.example.gymapp.domain.Meal
import com.example.gymapp.domain.MealSource
import com.example.gymapp.domain.NutritionGoal
import com.example.gymapp.domain.Sex
import com.example.gymapp.domain.VietFoodDb
import com.example.gymapp.domain.kcalRange
import com.example.gymapp.domain.totalMacros
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.BurntSecondary
import com.example.gymapp.ui.theme.CoralError
import com.example.gymapp.ui.theme.EmeraldTertiary
import com.example.gymapp.ui.theme.IronOnSurface
import com.example.gymapp.ui.theme.IronOnSurfaceVariant
import com.example.gymapp.ui.theme.IronOutline
import com.example.gymapp.ui.theme.IronSurface
import com.example.gymapp.ui.theme.IronSurfaceContainer
import com.example.gymapp.ui.theme.IronSurfaceHigh
import com.example.gymapp.ui.theme.IronSurfaceHighest
import com.example.gymapp.ui.theme.IronSurfaceLow
import com.example.gymapp.ui.theme.NeonPrimaryContainer
import com.example.gymapp.ui.theme.OnNeonPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val analysis by viewModel.analysis.collectAsStateWithLifecycle()
    val energy by viewModel.energy.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val recentMeals by viewModel.recentMeals.collectAsStateWithLifecycle()
    var showDescribe by remember { mutableStateOf(false) }
    var showRecent by remember { mutableStateOf(false) }
    var showAddFood by remember { mutableStateOf(false) }
    var showAgeSex by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<Meal?>(null) }
    var launchError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.refreshTargets() }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
        if (saved) viewModel.analyzePhoto(captureUri(context))
    }
    val pickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(viewModel::analyzePhoto)
    }
    val busy = analysis is AnalysisState.Analyzing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IronSurface)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = IronOnSurface)
            }
            Text(
                text = "Dinh dưỡng",
                style = MaterialTheme.typography.titleLarge,
                color = IronOnSurface,
                fontWeight = FontWeight.Bold,
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                DaySelector(
                    day = state.day,
                    onPrev = { viewModel.shiftDay(-1) },
                    onNext = { viewModel.shiftDay(1) },
                )
            }

            item {
                SummaryCard(
                    eaten = state.eaten,
                    targets = state.targets,
                    goal = state.goal,
                    onGoal = viewModel::setGoal,
                )
            }

            item {
                EnergyCard(
                    energy = energy,
                    profile = profile,
                    onActivity = viewModel::setActivity,
                    onEditAgeSex = { showAgeSex = true },
                )
            }

            item {
                if (state.aiConfigured) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CaptureButton(
                            icon = Icons.Filled.PhotoCamera,
                            label = "Chụp ảnh",
                            enabled = !busy,
                            primary = true,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                try {
                                    cameraLauncher.launch(captureUri(context))
                                } catch (_: ActivityNotFoundException) {
                                    launchError = "Máy không có ứng dụng camera"
                                }
                            },
                        )
                        CaptureButton(
                            icon = Icons.Filled.PhotoLibrary,
                            label = "Thư viện",
                            enabled = !busy,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                pickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                                )
                            },
                        )
                        CaptureButton(
                            icon = Icons.Filled.Edit,
                            label = "Mô tả",
                            enabled = !busy,
                            modifier = Modifier.weight(1f),
                            onClick = { showDescribe = true },
                        )
                    }
                } else {
                    NoticeCard(
                        text = "Chưa cấu hình AI. Thêm gemini.apiKey vào local.properties rồi build lại.",
                        color = BurntSecondary,
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SecondaryAction(
                        icon = Icons.Filled.Add,
                        label = "Nhập tay",
                        enabled = !busy,
                        modifier = Modifier.weight(1f),
                        onClick = viewModel::startManual,
                    )
                    SecondaryAction(
                        icon = Icons.Filled.History,
                        label = "Ăn lại bữa cũ",
                        enabled = !busy && recentMeals.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        onClick = { showRecent = true },
                    )
                }
            }

            launchError?.let { message ->
                item { NoticeCard(text = message, color = CoralError, onClose = { launchError = null }) }
            }

            when (val current = analysis) {
                is AnalysisState.Analyzing -> item {
                    AnalyzingCard(
                        text = if (current.source == MealSource.PHOTO) "AI đang phân tích ảnh…" else "AI đang phân tích mô tả…",
                        onCancel = viewModel::dismissAnalysis,
                    )
                }
                is AnalysisState.Failed -> item {
                    NoticeCard(text = current.message, color = CoralError, onClose = viewModel::dismissAnalysis)
                }
                else -> Unit
            }

            item {
                Text(
                    text = "BỮA ĂN (${state.meals.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = IronOnSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (state.meals.isEmpty()) {
                item {
                    Text(
                        text = "Chưa ghi bữa nào. Chụp ảnh từ trên xuống, thấy rõ cả bát đĩa để AI ước lượng khẩu phần tốt hơn.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = IronOnSurfaceVariant,
                    )
                }
            } else {
                items(state.meals, key = { it.id }) { meal ->
                    MealCard(
                        meal = meal,
                        photo = viewModel.photoFile(meal.photoName),
                        onOpen = { viewModel.editMeal(meal) },
                        onRepeat = { viewModel.repeatMeal(meal) },
                        onDelete = { pendingDelete = meal },
                    )
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = IronOutline, modifier = Modifier.size(14.dp))
                    Text(
                        text = "Ước tính từ ảnh thường lệch 20–35% và hay thiếu đạm, chất béo. Sửa số gram cho đúng trước khi lưu. Món khớp bảng món Việt dùng số liệu tham khảo của bảng thay cho ước tính của AI. Chạm vào bữa đã lưu để sửa.",
                        style = MaterialTheme.typography.labelSmall,
                        color = IronOutline,
                    )
                }
            }
        }
    }

    (analysis as? AnalysisState.Review)?.let { review ->
        ReviewDialog(
            draft = review.draft,
            photo = viewModel.photoFile(review.draft.photoName),
            onTitle = viewModel::setDraftTitle,
            onItem = viewModel::updateItem,
            onRemove = viewModel::removeItem,
            onAddFood = { showAddFood = true },
            onSave = viewModel::saveDraft,
            onDismiss = {
                showAddFood = false
                viewModel.dismissAnalysis()
            },
        )
        if (showAddFood) {
            AddFoodDialog(
                onDismiss = { showAddFood = false },
                onAdd = { item ->
                    viewModel.addItem(item)
                    showAddFood = false
                },
            )
        }
    }

    if (showRecent) {
        RecentMealsDialog(
            meals = recentMeals,
            onDismiss = { showRecent = false },
            onPick = { meal ->
                showRecent = false
                viewModel.repeatMeal(meal)
            },
        )
    }

    if (showAgeSex) {
        AgeSexDialog(
            profile = profile,
            onDismiss = { showAgeSex = false },
            onSave = { age, sex ->
                viewModel.setAgeAndSex(age, sex)
                showAgeSex = false
            },
        )
    }

    if (showDescribe) {
        DescribeDialog(
            onDismiss = { showDescribe = false },
            onSubmit = { text ->
                showDescribe = false
                viewModel.analyzeText(text)
            },
        )
    }

    pendingDelete?.let { meal ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Xóa bữa ăn?") },
            text = { Text("Xóa \"${meal.title}\" khỏi nhật ký.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMeal(meal)
                        pendingDelete = null
                    },
                ) { Text("Xóa", color = CoralError, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Hủy") } },
        )
    }
}

@Composable
fun NutritionTodayCard(
    eaten: Macros,
    targets: Macros,
    onOpen: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = IronSurfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NeonPrimary.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Restaurant, contentDescription = null, tint = IronOnSurface)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Dinh dưỡng hôm nay",
                    style = MaterialTheme.typography.titleMedium,
                    color = IronOnSurface,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${eaten.kcal.roundToInt()} / ${targets.kcal.roundToInt()} kcal • Đạm ${eaten.proteinG.roundToInt()}/${targets.proteinG.roundToInt()} g",
                    style = MaterialTheme.typography.labelMedium,
                    color = IronOnSurfaceVariant,
                )
                ProgressLine(fraction = fraction(eaten.kcal, targets.kcal), color = NeonPrimary)
            }
            Icon(Icons.Filled.PhotoCamera, contentDescription = "Chụp bữa ăn", tint = IronOnSurfaceVariant)
        }
    }
}

@Composable
private fun DaySelector(day: LocalDate, onPrev: () -> Unit, onNext: () -> Unit) {
    val today = LocalDate.now()
    val label = when (day) {
        today -> "Hôm nay"
        today.minusDays(1) -> "Hôm qua"
        else -> day.format(DateTimeFormatter.ofPattern("EEEE, d/M", Locale.forLanguageTag("vi-VN")))
            .replaceFirstChar { it.uppercase() }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPrev) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "Ngày trước", tint = IronOnSurface)
        }
        Text(label, style = MaterialTheme.typography.titleMedium, color = IronOnSurface, fontWeight = FontWeight.Bold)
        IconButton(onClick = onNext, enabled = day.isBefore(today)) {
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Ngày sau",
                tint = if (day.isBefore(today)) IronOnSurface else IronSurfaceHighest,
            )
        }
    }
}

@Composable
private fun SummaryCard(
    eaten: Macros,
    targets: Macros,
    goal: NutritionGoal,
    onGoal: (NutritionGoal) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = IronSurfaceContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${eaten.kcal.roundToInt()}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = IronOnSurface,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "/ ${targets.kcal.roundToInt()} kcal",
                    style = MaterialTheme.typography.labelLarge,
                    color = IronOnSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            ProgressLine(fraction = fraction(eaten.kcal, targets.kcal), color = NeonPrimary)
            MacroRow("Đạm", eaten.proteinG, targets.proteinG, EmeraldTertiary)
            MacroRow("Tinh bột", eaten.carbsG, targets.carbsG, NeonPrimaryContainer)
            MacroRow("Chất béo", eaten.fatG, targets.fatG, BurntSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                NutritionGoal.entries.forEach { option ->
                    val active = option == goal
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) NeonPrimaryContainer else IronSurfaceLow)
                            .clickable { onGoal(option) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (active) OnNeonPrimary else IronOnSurface,
                            fontWeight = if (active) FontWeight.ExtraBold else FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroRow(label: String, eaten: Double, target: Double, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = IronOnSurface)
            Text(
                text = "${eaten.roundToInt()} / ${target.roundToInt()} g",
                style = MaterialTheme.typography.labelMedium,
                color = IronOnSurfaceVariant,
            )
        }
        ProgressLine(fraction = fraction(eaten, target), color = color)
    }
}

@Composable
private fun ProgressLine(fraction: Float, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(CircleShape)
            .background(IronSurfaceHighest),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .height(6.dp)
                .clip(CircleShape)
                .background(if (fraction >= 1f) BurntSecondary else color),
        )
    }
}

@Composable
private fun CaptureButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    onClick: () -> Unit,
) {
    val background = if (primary) NeonPrimaryContainer else IronSurfaceHigh
    val content = if (primary) OnNeonPrimary else IronOnSurface
    Column(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (enabled) background else IronSurfaceLow)
            .clickable(enabled = enabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, contentDescription = null, tint = if (enabled) content else IronOutline)
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (enabled) content else IronOutline,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AnalyzingCard(text: String, onCancel: () -> Unit) {
    Surface(shape = RoundedCornerShape(14.dp), color = IronSurfaceContainer, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CircularProgressIndicator(color = IronOnSurface, strokeWidth = 3.dp, modifier = Modifier.size(22.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium, color = IronOnSurface, modifier = Modifier.weight(1f))
            TextButton(onClick = onCancel) { Text("Hủy") }
        }
    }
}

@Composable
private fun NoticeCard(text: String, color: Color, onClose: (() -> Unit)? = null) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.12f),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(text, style = MaterialTheme.typography.bodyMedium, color = color, modifier = Modifier.weight(1f))
            if (onClose != null) {
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = "Đóng", tint = color, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun MealCard(meal: Meal, photo: File?, onOpen: () -> Unit, onRepeat: () -> Unit, onDelete: () -> Unit) {
    val macros = meal.macros
    val time = remember(meal.eatenAt) { SimpleDateFormat("HH:mm", Locale.US).format(Date(meal.eatenAt)) }
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = IronSurfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (photo != null) {
                MealPhoto(file = photo, modifier = Modifier.size(64.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(time, style = MaterialTheme.typography.labelSmall, color = IronOutline)
                    if (meal.source == MealSource.PHOTO) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = "Từ ảnh", tint = IronOutline, modifier = Modifier.size(12.dp))
                    }
                }
                Text(
                    text = meal.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = IronOnSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = meal.items.joinToString(", ") { "${it.name} ${it.grams.roundToInt()}g" },
                    style = MaterialTheme.typography.labelSmall,
                    color = IronOnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = macroLine(macros),
                    style = MaterialTheme.typography.labelMedium,
                    color = IronOnSurface,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Column {
                IconButton(onClick = onRepeat, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Filled.Replay, contentDescription = "Ăn lại bữa này", tint = IronOnSurfaceVariant, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "Xóa bữa", tint = IronOnSurfaceVariant, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun MealPhoto(file: File, modifier: Modifier = Modifier) {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, file) {
        value = withContext(Dispatchers.IO) { decodeThumbnail(file, 512) }
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(IronSurfaceHigh),
    ) {
        bitmap?.let {
            Image(
                bitmap = it,
                contentDescription = "Ảnh bữa ăn",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private fun decodeThumbnail(file: File, maxSide: Int): ImageBitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(file.path, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
    var sample = 1
    while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxSide) sample *= 2
    return BitmapFactory.decodeFile(file.path, BitmapFactory.Options().apply { inSampleSize = sample })?.asImageBitmap()
}

@Composable
private fun SecondaryAction(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(IronSurfaceContainer)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        Icon(icon, contentDescription = null, tint = if (enabled) IronOnSurface else IronOutline, modifier = Modifier.size(18.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) IronOnSurface else IronOutline,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun EnergyCard(
    energy: EnergyBreakdown,
    profile: BodyProfile,
    onActivity: (ActivityLevel) -> Unit,
    onEditAgeSex: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Surface(shape = RoundedCornerShape(18.dp), color = IronSurfaceContainer, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = BurntSecondary)
                Column(Modifier.weight(1f)) {
                    Text("BMR & TDEE", style = MaterialTheme.typography.titleMedium, color = IronOnSurface, fontWeight = FontWeight.Bold)
                    Text(
                        text = "BMR ${kcal(energy.bmrKatch)} • TDEE ${kcal(energy.tdee)} • Mục tiêu ${kcal(energy.targetKcal)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = IronOnSurfaceVariant,
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Thu gọn" else "Xem cách tính",
                    tint = IronOnSurfaceVariant,
                )
            }
            if (expanded) {
                EnergyLine(
                    label = "BMR (Katch-McArdle)",
                    value = kcal(energy.bmrKatch),
                    note = "370 + 21,6 × khối nạc ${formatOne(profile.leanMassKg)} kg. Năng lượng cơ thể đốt khi nằm nghỉ cả ngày.",
                    highlight = true,
                )
                EnergyLine(
                    label = "BMR (Mifflin-St Jeor)",
                    value = kcal(energy.bmrMifflin),
                    note = "Tham khảo, tính từ cân nặng, chiều cao ${profile.heightCm} cm, tuổi, giới tính. Lệch nhiều so với Katch thường do % mỡ chưa chính xác.",
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(IronSurfaceLow)
                        .clickable(onClick = onEditAgeSex)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${profile.sex.label}, ${profile.ageYears} tuổi • ${formatOne(profile.weightKg)} kg • mỡ ${formatOne(profile.bodyFatPct)}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = IronOnSurface,
                        modifier = Modifier.weight(1f),
                    )
                    Text("Sửa", style = MaterialTheme.typography.labelMedium, color = IronOnSurface, fontWeight = FontWeight.Bold)
                }
                Text("Mức vận động", style = MaterialTheme.typography.labelMedium, color = IronOnSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ActivityLevel.entries.forEach { level ->
                        val active = level == energy.activity
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) NeonPrimaryContainer else IronSurfaceLow)
                                .clickable { onActivity(level) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "×${level.factor}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (active) OnNeonPrimary else IronOnSurface,
                                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.SemiBold,
                            )
                        }
                    }
                }
                Text(
                    text = "${energy.activity.label}: ${energy.activity.detail}",
                    style = MaterialTheme.typography.labelSmall,
                    color = IronOnSurfaceVariant,
                )
                EnergyLine(
                    label = "TDEE",
                    value = kcal(energy.tdee),
                    note = "BMR × ${energy.activity.factor}. Tổng năng lượng tiêu hao mỗi ngày.",
                )
                val delta = energy.goal.kcalDelta
                EnergyLine(
                    label = "Mục tiêu (${energy.goal.label})",
                    value = kcal(energy.targetKcal),
                    note = when {
                        delta > 0 -> "TDEE + $delta kcal để tăng cơ, hạn chế tăng mỡ."
                        delta < 0 -> "TDEE − ${-delta} kcal, khoảng 0,4 kg mỗi tuần."
                        else -> "Ăn bằng TDEE để giữ cân."
                    },
                    highlight = true,
                )
            }
        }
    }
}

@Composable
private fun EnergyLine(label: String, value: String, note: String, highlight: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = IronOnSurface, fontWeight = FontWeight.SemiBold)
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = if (highlight) IronOnSurface else IronOnSurface,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(note, style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
    }
}

@Composable
private fun AgeSexDialog(profile: BodyProfile, onDismiss: () -> Unit, onSave: (Int, Sex) -> Unit) {
    var age by remember { mutableStateOf(profile.ageYears.toString()) }
    var sex by remember { mutableStateOf(profile.sex) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tuổi & giới tính") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Sex.entries.forEach { option ->
                        val active = option == sex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) NeonPrimaryContainer else IronSurfaceLow)
                                .clickable { sex = option },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(option.label, color = if (active) OnNeonPrimary else IronOnSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it.filter(Char::isDigit).take(2) },
                    label = { Text("Tuổi") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    "Cân nặng, % mỡ và chiều cao sửa trong Hồ sơ.",
                    style = MaterialTheme.typography.labelSmall,
                    color = IronOnSurfaceVariant,
                )
            }
        },
        confirmButton = {
            val parsed = age.toIntOrNull()
            TextButton(onClick = { parsed?.let { onSave(it, sex) } }, enabled = parsed != null && parsed in 14..90) {
                Text("Lưu", color = IronOnSurface, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } },
    )
}

@Composable
private fun RecentMealsDialog(meals: List<Meal>, onDismiss: () -> Unit, onPick: (Meal) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ăn lại bữa cũ") },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(meals, key = { it.id }) { meal ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(IronSurfaceLow)
                            .clickable { onPick(meal) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                    ) {
                        Text(meal.title, style = MaterialTheme.typography.titleSmall, color = IronOnSurface, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "${meal.macros.kcal.roundToInt()} kcal • Đạm ${meal.macros.proteinG.roundToInt()}g • ${meal.items.size} món",
                            style = MaterialTheme.typography.labelSmall,
                            color = IronOnSurfaceVariant,
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Đóng") } },
    )
}

@Composable
private fun AddFoodDialog(onDismiss: () -> Unit, onAdd: (FoodItem) -> Unit) {
    var query by remember { mutableStateOf("") }
    var custom by remember { mutableStateOf(false) }
    val results = remember(query) { VietFoodDb.search(query, limit = 30) }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = IronSurfaceContainer,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 620.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (custom) "Món tự nhập" else "Thêm món",
                    style = MaterialTheme.typography.titleLarge,
                    color = IronOnSurface,
                    fontWeight = FontWeight.Bold,
                )
                if (custom) {
                    CustomFoodForm(initialName = query, onCancel = { custom = false }, onAdd = onAdd)
                } else {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Tìm: phở, ức gà, cơm…") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(results, key = { it.name }) { food ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(IronSurfaceLow)
                                    .clickable { onAdd(food.toItem()) }
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(food.name, style = MaterialTheme.typography.titleSmall, color = IronOnSurface, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "${food.portionLabel} ≈ ${food.portionGrams.roundToInt()}g • ${(food.kcal * food.portionGrams / 100).roundToInt()} kcal • Đạm ${(food.protein * food.portionGrams / 100).roundToInt()}g",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IronOnSurfaceVariant,
                                    )
                                }
                                Icon(Icons.Filled.Add, contentDescription = "Thêm ${food.name}", tint = IronOnSurface)
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = { custom = true }) {
                            Text("Món khác (tự nhập)", color = IronOnSurface, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = onDismiss) { Text("Đóng") }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomFoodForm(initialName: String, onCancel: () -> Unit, onAdd: (FoodItem) -> Unit) {
    var name by remember { mutableStateOf(initialName) }
    var grams by remember { mutableStateOf("100") }
    var kcal by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    fun number(text: String) = text.replace(',', '.').toDoubleOrNull()
    val valid = name.isNotBlank() && (number(grams) ?: 0.0) > 0.0 && number(kcal) != null

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên món") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumberField("Số gram", grams, { grams = it }, Modifier.weight(1f))
            NumberField("kcal/100g", kcal, { kcal = it }, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumberField("Đạm/100g", protein, { protein = it }, Modifier.weight(1f))
            NumberField("Tinh bột", carbs, { carbs = it }, Modifier.weight(1f))
            NumberField("Béo", fat, { fat = it }, Modifier.weight(1f))
        }
        Text(
            "Lấy số trên nhãn dinh dưỡng (thường ghi theo 100 g). Bỏ trống đạm, tinh bột, béo nếu không biết.",
            style = MaterialTheme.typography.labelSmall,
            color = IronOnSurfaceVariant,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onCancel) { Text("Quay lại") }
            TextButton(
                enabled = valid,
                onClick = {
                    onAdd(
                        FoodItem(
                            name = name.trim(),
                            grams = number(grams) ?: 0.0,
                            kcalPer100g = (number(kcal) ?: 0.0).coerceIn(0.0, 900.0),
                            proteinPer100g = (number(protein) ?: 0.0).coerceIn(0.0, 100.0),
                            carbsPer100g = (number(carbs) ?: 0.0).coerceIn(0.0, 100.0),
                            fatPer100g = (number(fat) ?: 0.0).coerceIn(0.0, 100.0),
                        ),
                    )
                },
            ) { Text("Thêm", color = IronOnSurface, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun NumberField(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = { input -> onChange(input.filter { it.isDigit() || it == '.' || it == ',' }.take(6)) },
        label = { Text(label, maxLines = 1) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier,
    )
}

private fun kcal(value: Double) = "${value.roundToInt()} kcal"

private fun formatOne(value: Double) = String.format(Locale.US, "%.1f", value).removeSuffix(".0")

@Composable
private fun ReviewDialog(
    draft: MealDraft,
    photo: File?,
    onTitle: (String) -> Unit,
    onItem: (Int, FoodItem) -> Unit,
    onRemove: (Int) -> Unit,
    onAddFood: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    val total = draft.items.totalMacros()
    val range = kcalRange(total.kcal)
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = IronSurfaceContainer,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 640.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val heading = when {
                    draft.mealId != null -> "Sửa bữa ăn"
                    draft.source == MealSource.MANUAL -> "Ghi bữa ăn"
                    else -> "Kiểm tra lại khẩu phần"
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = if (draft.source == MealSource.MANUAL || draft.mealId != null) Icons.Filled.Edit else Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = IronOnSurface,
                    )
                    Text(
                        text = heading,
                        style = MaterialTheme.typography.titleLarge,
                        color = IronOnSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    draft.confidence?.let { ConfidenceChip(it) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (photo != null) MealPhoto(file = photo, modifier = Modifier.size(56.dp))
                    OutlinedTextField(
                        value = draft.title,
                        onValueChange = onTitle,
                        label = { Text("Tên bữa") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(draft.items) { index, item ->
                        ReviewItemRow(
                            item = item,
                            onGrams = { grams -> onItem(index, item.copy(grams = grams)) },
                            onRemove = { onRemove(index) },
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(IronSurfaceLow)
                                .clickable(onClick = onAddFood)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, tint = IronOnSurface, modifier = Modifier.size(18.dp))
                            Text(
                                text = if (draft.items.isEmpty()) "Thêm món đầu tiên" else "Thêm món bị thiếu",
                                style = MaterialTheme.typography.labelLarge,
                                color = IronOnSurface,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
                Surface(shape = RoundedCornerShape(12.dp), color = IronSurfaceLow, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "≈ ${total.kcal.roundToInt()} kcal",
                            style = MaterialTheme.typography.titleLarge,
                            color = IronOnSurface,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Khoảng ${range.first}–${range.last} kcal • ${macroLine(total, withKcal = false)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = IronOnSurfaceVariant,
                        )
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Hủy") }
                    TextButton(onClick = onSave, enabled = draft.items.isNotEmpty()) {
                        Text("Lưu bữa ăn", color = IronOnSurface, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewItemRow(item: FoodItem, onGrams: (Double) -> Unit, onRemove: () -> Unit) {
    var text by remember(item.grams) { mutableStateOf(item.grams.roundToInt().toString()) }
    val macros = item.macros
    Surface(shape = RoundedCornerShape(12.dp), color = IronSurfaceLow, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = IronOnSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${macros.kcal.roundToInt()} kcal • Đạm ${macros.proteinG.roundToInt()}g" +
                        if (item.fromDatabase) " • bảng món Việt" else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (item.fromDatabase) EmeraldTertiary else IronOnSurfaceVariant,
                )
            }
            GramStep("−") { onGrams((item.grams - 10.0).coerceAtLeast(0.0)) }
            OutlinedTextField(
                value = text,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }.take(4)
                    text = digits
                    digits.toDoubleOrNull()?.let(onGrams)
                },
                suffix = { Text("g") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(84.dp),
            )
            GramStep("+") { onGrams(item.grams + 10.0) }
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.Close, contentDescription = "Bỏ ${item.name}", tint = IronOnSurfaceVariant, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun GramStep(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(IronSurfaceHigh)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = IronOnSurface, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ConfidenceChip(confidence: AnalysisConfidence) {
    val color = when (confidence) {
        AnalysisConfidence.HIGH -> IronOnSurface
        AnalysisConfidence.MEDIUM -> BurntSecondary
        AnalysisConfidence.LOW -> CoralError
    }
    Surface(shape = CircleShape, color = color.copy(alpha = 0.14f)) {
        Text(
            text = confidence.label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun DescribeDialog(onDismiss: () -> Unit, onSubmit: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mô tả bữa ăn") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("VD: 1 tô phở bò tái, 1 ly trà đá") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(text) }, enabled = text.isNotBlank()) {
                Text("Phân tích", color = IronOnSurface, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } },
    )
}

private fun macroLine(macros: Macros, withKcal: Boolean = true): String {
    val parts = "Đạm ${macros.proteinG.roundToInt()}g • Tinh bột ${macros.carbsG.roundToInt()}g • Béo ${macros.fatG.roundToInt()}g"
    return if (withKcal) "${macros.kcal.roundToInt()} kcal • $parts" else parts
}

private fun fraction(value: Double, target: Double): Float =
    if (target <= 0.0) 0f else (value / target).toFloat().coerceIn(0f, 1f)

private fun captureUri(context: Context): Uri {
    val dir = File(context.cacheDir, "meal_photos").apply { mkdirs() }
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(dir, "capture.jpg"))
}
