package com.example.gymapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymapp.domain.DayCheckItem
import com.example.gymapp.domain.IronLogCatalog
import com.example.gymapp.domain.MuscleRecoveryItem
import com.example.gymapp.domain.formatDuration
import com.example.gymapp.domain.formatElapsed
import com.example.gymapp.domain.formatEstimate
import com.example.gymapp.domain.formatSessionWhen
import com.example.gymapp.domain.formatSignedEstimate
import com.example.gymapp.ui.components.Sparkline
import com.example.gymapp.ui.theme.BurntSecondary
import com.example.gymapp.ui.theme.BurntSecondaryContainer
import com.example.gymapp.ui.theme.EmeraldTertiary
import com.example.gymapp.ui.theme.IronOnSurface
import com.example.gymapp.ui.theme.IronOnSurfaceVariant
import com.example.gymapp.ui.theme.IronOutline
import com.example.gymapp.ui.theme.IronSurfaceContainer
import com.example.gymapp.ui.theme.IronSurfaceHigh
import com.example.gymapp.ui.theme.IronSurfaceHighest
import com.example.gymapp.ui.theme.IronSurfaceLow
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.NeonPrimaryContainer
import com.example.gymapp.ui.theme.OnNeonPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenWorkout: (Long) -> Unit,
    onOpenProgress: (Long) -> Unit,
    onOpenExerciseDetail: (Long) -> Unit,
    onOpenRoutines: () -> Unit,
    onOpenLibrary: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NeonPrimary)
        }
        return
    }

    val dateLine = remember {
        val formatter = DateTimeFormatter.ofPattern("EEEE, d 'Tháng' M", Locale.forLanguageTag("vi-VN"))
        LocalDate.now().format(formatter).replaceFirstChar { it.uppercase() } + " • Chu kỳ Hypertrophy"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 1. Greeting Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Chào ${state.athleteName}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = IronOnSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = dateLine,
                        style = MaterialTheme.typography.labelMedium,
                        color = IronOnSurfaceVariant,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(IronSurfaceContainer)
                        .clickable(onClick = onOpenRoutines)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = state.activeRoutine.name.take(16),
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        // 2. Quick Search & Routine Navigation Jump
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = IronSurfaceHigh,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = IronOutline,
                            modifier = Modifier.size(20.dp),
                        )
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = IronOnSurface),
                            modifier = Modifier.weight(1f),
                            decorationBox = { inner ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Tìm bài tập, giáo án, nhóm cơ...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = IronOutline,
                                    )
                                }
                                inner()
                            },
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(IronSurfaceHighest)
                                .clickable(onClick = onOpenLibrary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = "Thư viện bài tập",
                                tint = NeonPrimary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                if (searchQuery.isNotBlank()) {
                    val matches = state.catalog.filter { ex ->
                        val meta = IronLogCatalog.metaFor(ex)
                        ex.name.contains(searchQuery, ignoreCase = true) ||
                            meta.vietnameseName.contains(searchQuery, ignoreCase = true) ||
                            ex.muscle.label.contains(searchQuery, ignoreCase = true)
                    }.take(5)
                    matches.forEach { ex ->
                        val meta = IronLogCatalog.metaFor(ex)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = IronSurfaceContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenExerciseDetail(ex.id) },
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(ex.name, style = MaterialTheme.typography.titleMedium, color = IronOnSurface)
                                    Text(
                                        "${meta.vietnameseName} • ${ex.muscle.label}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IronOnSurfaceVariant,
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = null,
                                    tint = NeonPrimary,
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Chạm để duyệt toàn bộ giáo án & kho bài tập",
                            style = MaterialTheme.typography.labelSmall,
                            color = IronOnSurfaceVariant,
                        )
                        Row(
                            modifier = Modifier.clickable(onClick = onOpenRoutines),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Xem giáo án",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonPrimary,
                                fontWeight = FontWeight.Bold,
                            )
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = null,
                                tint = NeonPrimary,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                }
            }
        }

        // 3. Today's Workout Hero Card
        item {
            TodayHeroWorkoutCard(
                state = state,
                onStartOrResume = {
                    scope.launch {
                        onOpenWorkout(viewModel.startOrResume())
                    }
                },
                onOpenExercise = { id ->
                    if (id != null) onOpenExerciseDetail(id)
                },
            )
        }

        // 4. Streak & Weekly Consistency Widget
        item {
            StreakAndWeekCard(
                streakDays = state.streakDays,
                weekStrip = state.weekStrip,
            )
        }

        // 5. Muscle Recovery Status Widget
        item {
            MuscleRecoveryCard(recovery = state.recovery)
        }

        // 6. Quick Stats 2-Column Grid (Tổng Volume Tuần & PR Mới Tuần Này)
        item {
            QuickStatsRow(
                weeklyVolumeKg = state.weeklyVolumeKg,
                weeklyVolumeDeltaPct = state.weeklyVolumeDeltaPct,
                weeklyPrCount = state.weeklyPrCount,
                topPrLine = state.topPrLine,
            )
        }

        // 7. Exercise Progress Sparklines (if any trained)
        if (state.progress.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Đường cong 1RM theo bài",
                        style = MaterialTheme.typography.titleLarge,
                        color = IronOnSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Chạm để phân tích",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPrimary,
                    )
                }
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.progress, key = { it.exerciseId }) { card ->
                        ProgressSparkCard(card = card, onClick = { onOpenProgress(card.exerciseId) })
                    }
                }
            }
        }

        // 8. Recent Sessions
        if (state.recent.isNotEmpty()) {
            item {
                Text(
                    text = "Buổi tập gần đây",
                    style = MaterialTheme.typography.titleLarge,
                    color = IronOnSurface,
                    fontWeight = FontWeight.Bold,
                )
            }
            items(state.recent.take(5), key = { it.sessionId }) { session ->
                RecentSessionCard(session = session, onClick = { onOpenWorkout(session.sessionId) })
            }
        }

        // 9. Gym Motivation Micro-banner
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = IronSurfaceHighest.copy(alpha = 0.45f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenLibrary),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = null,
                            tint = NeonPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = "Khởi động kỹ xoay khớp vai trước hiệp Bench Press",
                            style = MaterialTheme.typography.labelMedium,
                            color = IronOnSurfaceVariant,
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = IronOutline,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayHeroWorkoutCard(
    state: HomeUiState,
    onStartOrResume: () -> Unit,
    onOpenExercise: (Long?) -> Unit,
) {
    val active = state.active
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = IronSurfaceContainer,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header Badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = CircleShape,
                    color = NeonPrimaryContainer.copy(alpha = 0.20f),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NeonPrimary),
                        )
                        Text(
                            text = if (active != null) "ĐANG DIỄN RA" else "LỊCH TẬP HÔM NAY",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = IronSurfaceHighest,
                ) {
                    Text(
                        text = "Chu kỳ 12W • Khối lượng MAV",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = IronOnSurface,
                    )
                }
            }

            // Workout Title
            Column {
                Text(
                    text = state.heroTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    color = IronOnSurface,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = state.heroFocus.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }

            // 4-Column Key Targets Row
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = IronSurfaceLow,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    HeroMetricCol("BÀI TẬP", "${state.heroPreviews.size} bài", IronOnSurface)
                    HeroMetricCol("TỔNG HIỆP", "${state.heroPreviews.sumOf { it.targetSets }} hiệp", IronOnSurface)
                    HeroMetricCol("THỜI GIAN", "~55p", IronOnSurface)
                    HeroMetricCol("TARGET RIR", "1 - 2", BurntSecondary)
                }
            }

            // Mini Exercise Preview List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "DANH SÁCH BÀI TẬP TRỌNG TÂM",
                    style = MaterialTheme.typography.labelSmall,
                    color = IronOutline,
                    fontWeight = FontWeight.Bold,
                )
                state.heroPreviews.take(3).forEachIndexed { idx, preview ->
                    val isMain = idx == 0
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isMain) IronSurfaceHigh else IronSurfaceLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenExercise(preview.exerciseId) },
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isMain) NeonPrimaryContainer else IronSurfaceHighest),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "${preview.order}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isMain) OnNeonPrimary else IronOutline,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = preview.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = IronOnSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = preview.targetLine,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isMain) NeonPrimary else IronOnSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                            Surface(
                                shape = CircleShape,
                                color = IronSurfaceHighest,
                            ) {
                                Text(
                                    text = "${preview.targetSets} sets",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isMain) IronOnSurface else IronOutline,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                if (state.heroPreviews.size > 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        state.heroPreviews.drop(3).take(2).forEach { preview ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = IronSurfaceLow,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onOpenExercise(preview.exerciseId) },
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "${preview.order}. ${preview.name}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IronOutline,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f),
                                    )
                                    Text(
                                        text = "${preview.targetSets}s",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IronOnSurface,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // HERO CTA BUTTON
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Button(
                    onClick = onStartOrResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPrimaryContainer,
                        contentColor = OnNeonPrimary,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    if (active == null) {
                        Text(
                            text = "BẮT ĐẦU BUỔI TẬP",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    } else {
                        val elapsed = rememberElapsedSince(active.startedAt)
                        Text(
                            text = "TIẾP TỤC BUỔI TẬP (${active.setCount} SET • ${formatElapsed(elapsed)})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
                Text(
                    text = "Mở thẳng màn hình ghi hiệp & kích hoạt Rest Timer",
                    style = MaterialTheme.typography.labelSmall,
                    color = IronOutline,
                )
            }
        }
    }
}

@Composable
private fun HeroMetricCol(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = IronOutline)
        Text(value, style = MaterialTheme.typography.titleMedium, color = valueColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StreakAndWeekCard(
    streakDays: Int,
    weekStrip: List<DayCheckItem>,
) {
    val trainedCount = weekStrip.count { it.completed }
    val remaining = (4 - trainedCount).coerceAtLeast(0)
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = IronSurfaceLow,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BurntSecondaryContainer.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = null,
                            tint = BurntSecondary,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "$streakDays Ngày",
                                style = MaterialTheme.typography.headlineMedium,
                                color = BurntSecondary,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            Text(
                                text = "liên tục",
                                style = MaterialTheme.typography.labelMedium,
                                color = IronOnSurface,
                            )
                        }
                        Text(
                            text = "Mục tiêu tuần: $trainedCount/4 buổi hoàn tất",
                            style = MaterialTheme.typography.labelSmall,
                            color = IronOutline,
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = IronSurfaceContainer,
                ) {
                    Text(
                        text = "Tuần 4/4 🔥",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // 7-Day Matrix Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                weekStrip.forEach { day ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (day.isToday) NeonPrimaryContainer.copy(alpha = 0.15f) else IronSurfaceContainer,
                            )
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = day.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (day.isToday) NeonPrimary else IronOutline,
                            fontWeight = FontWeight.Bold,
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        day.completed -> NeonPrimaryContainer
                                        day.isToday -> IronSurfaceHighest
                                        else -> IronSurfaceHighest.copy(alpha = 0.6f)
                                    },
                                )
                                .then(
                                    if (day.isToday && !day.completed) {
                                        Modifier.border(1.5.dp, NeonPrimary, CircleShape)
                                    } else {
                                        Modifier
                                    },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            when {
                                day.completed -> Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = OnNeonPrimary,
                                    modifier = Modifier.size(16.dp),
                                )
                                day.isToday -> Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(NeonPrimary),
                                )
                                else -> Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(IronOutline.copy(alpha = 0.45f)),
                                )
                            }
                        }
                    }
                }
            }

            // Motivational Footer
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = IronSurfaceContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = BurntSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = if (remaining == 0) {
                            "Đã hoàn thành mục tiêu 4 buổi/tuần! Duy trì phong độ Hypertrophy."
                        } else {
                            "Chỉ còn $remaining buổi nữa để hoàn thành mục tiêu 4 buổi/tuần!"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = IronOnSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun MuscleRecoveryCard(recovery: List<MuscleRecoveryItem>) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = IronSurfaceContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.HealthAndSafety,
                    contentDescription = null,
                    tint = NeonPrimary,
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    text = "Trạng thái phục hồi cơ bắp",
                    style = MaterialTheme.typography.titleLarge,
                    color = IronOnSurface,
                    fontWeight = FontWeight.Bold,
                )
            }

            recovery.forEach { item ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = IronOnSurface,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (item.isOptimal) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = NeonPrimaryContainer.copy(alpha = 0.2f),
                                ) {
                                    Text(
                                        text = "TỐI ƯU",
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonPrimary,
                                    )
                                }
                            }
                        }
                        Text(
                            text = item.statusText,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (item.isOptimal) NeonPrimary else EmeraldTertiary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(IronSurfaceHighest),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(item.percent / 100f)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(if (item.isOptimal) NeonPrimary else EmeraldTertiary),
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = IronSurfaceLow,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Filled.TipsAndUpdates,
                        contentDescription = null,
                        tint = NeonPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("Nhóm cơ đẩy ")
                            withStyle(SpanStyle(color = NeonPrimary, fontWeight = FontWeight.Bold)) {
                                append("(Push)")
                            }
                            append(" đã hồi phục. Đây là ")
                            withStyle(SpanStyle(color = IronOnSurface, fontWeight = FontWeight.Bold)) {
                                append("thời điểm vàng")
                            }
                            append(" để kích hoạt cơ chế Progressive Overload!")
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = IronOnSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    weeklyVolumeKg: Double,
    weeklyVolumeDeltaPct: Int?,
    weeklyPrCount: Int,
    topPrLine: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Volume Stat Card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = IronSurfaceLow,
            modifier = Modifier.weight(1f),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "TỔNG VOLUME TUẦN",
                        style = MaterialTheme.typography.labelSmall,
                        color = IronOutline,
                        fontWeight = FontWeight.Bold,
                    )
                    Icon(
                        imageVector = Icons.Filled.FitnessCenter,
                        contentDescription = null,
                        tint = NeonPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                }
                val formattedVolume = if (weeklyVolumeKg >= 1000.0) {
                    String.format(Locale.US, "%.1fk", weeklyVolumeKg / 1000.0)
                } else {
                    weeklyVolumeKg.toInt().toString()
                }
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = formattedVolume,
                        style = MaterialTheme.typography.displaySmall,
                        color = IronOnSurface,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "kg",
                        style = MaterialTheme.typography.titleMedium,
                        color = IronOutline,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Filled.ArrowUpward,
                        contentDescription = null,
                        tint = NeonPrimary,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = if (weeklyVolumeDeltaPct != null) {
                            "${if (weeklyVolumeDeltaPct >= 0) "+" else ""}$weeklyVolumeDeltaPct% vs tuần trước"
                        } else {
                            "Chu kỳ tích lũy MAV"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        // PR Record Card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = IronSurfaceLow,
            modifier = Modifier.weight(1f),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "PR MỚI TUẦN NÀY",
                        style = MaterialTheme.typography.labelSmall,
                        color = BurntSecondary,
                        fontWeight = FontWeight.Bold,
                    )
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = BurntSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "$weeklyPrCount",
                        style = MaterialTheme.typography.displaySmall,
                        color = BurntSecondary,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "kỷ lục",
                        style = MaterialTheme.typography.titleMedium,
                        color = IronOnSurface,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = topPrLine,
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ProgressSparkCard(card: ExerciseProgressUi, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .width(178.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = IronSurfaceContainer,
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = card.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = IronOnSurface,
            )
            if (card.latestE1rm != null) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = formatEstimate(card.latestE1rm),
                        style = MaterialTheme.typography.headlineMedium,
                        color = NeonPrimary,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text("kg e1RM", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                }
                val delta = card.deltaE1rm
                if (delta != null && abs(delta) >= 0.05) {
                    Text(
                        text = "${formatSignedEstimate(delta)} kg",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (delta > 0) NeonPrimary else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                    )
                }
            } else {
                Text(
                    text = "${card.bestReps} reps",
                    style = MaterialTheme.typography.headlineMedium,
                    color = NeonPrimary,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Sparkline(
                values = card.sparkline,
                color = NeonPrimaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun RecentSessionCard(session: RecentSessionUi, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = IronSurfaceContainer,
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = IronOnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${session.volumeKg.toInt()} kg",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonPrimary,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = formatSessionWhen(session.startedAt),
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Text("•", color = IronOnSurfaceVariant)
                Text(
                    text = when (val finished = session.finishedAt) {
                        null -> "Đang mở"
                        else -> formatDuration(session.startedAt, finished)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = IronOnSurfaceVariant,
                )
                Text("•", color = IronOnSurfaceVariant)
                Text(
                    text = "${session.setCount} hiệp",
                    style = MaterialTheme.typography.labelMedium,
                    color = IronOnSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun rememberElapsedSince(startedAt: Long): Long {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(startedAt) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1_000)
        }
    }
    return (now - startedAt).coerceAtLeast(0L)
}
