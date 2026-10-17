package com.example.gymapp.ui.progress

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp.domain.IronLogCatalog
import com.example.gymapp.domain.ProgressPoint
import com.example.gymapp.domain.formatEstimate
import com.example.gymapp.domain.formatLoad
import com.example.gymapp.domain.formatSessionWhen
import com.example.gymapp.domain.formatSignedEstimate
import com.example.gymapp.domain.formatVolume
import com.example.gymapp.service.RestPhase
import com.example.gymapp.service.RestTimerBus
import com.example.gymapp.ui.appViewModelFactory
import com.example.gymapp.ui.components.ActiveRestBanner
import com.example.gymapp.ui.components.ProgressLineChart
import com.example.gymapp.ui.theme.BurntSecondary
import com.example.gymapp.ui.theme.EmeraldTertiary
import com.example.gymapp.ui.theme.IronOnSurface
import com.example.gymapp.ui.theme.IronOnSurfaceVariant
import com.example.gymapp.ui.theme.IronSurface
import com.example.gymapp.ui.theme.IronSurfaceContainer
import com.example.gymapp.ui.theme.IronSurfaceHigh
import com.example.gymapp.ui.theme.IronSurfaceHighest
import com.example.gymapp.ui.theme.IronSurfaceLow
import com.example.gymapp.ui.theme.IronSurfaceLowest
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.NeonPrimaryContainer
import com.example.gymapp.ui.theme.OnNeonPrimary
import kotlin.math.abs

/**
 * Stitch screen: Chi tiết Progressive Overload - Barbell Bench Press
 */
@Composable
fun ProgressScreen(
    exerciseId: Long,
    onBack: () -> Unit,
    onOpenWorkout: (Long) -> Unit,
    onApplyToWorkout: (String) -> Unit = {},
) {
    val viewModel: ProgressViewModel = viewModel(
        key = "progress-$exerciseId",
        factory = appViewModelFactory { ProgressViewModel(it.repository, exerciseId) },
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val restPhase by RestTimerBus.phase.collectAsStateWithLifecycle()
    val restVisible = restPhase !is RestPhase.Idle
    val exercise = state.exercise
    var cycleFilter by rememberSaveable { mutableStateOf("8W") }
    var showExerciseMenu by remember { mutableStateOf(false) }
    var expandPrinciple by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IronSurface)
            .statusBarsPadding(),
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = IronOnSurface,
                    )
                }
                Text(
                    text = "Chi Tiết Progressive Overload",
                    style = MaterialTheme.typography.titleLarge,
                    color = IronOnSurface,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonPrimary)
            }
            return
        }
        if (exercise == null) {
            Text("Không tìm thấy bài tập.", modifier = Modifier.padding(20.dp), color = IronOnSurface)
            return
        }

        val meta = remember(exercise) { IronLogCatalog.metaFor(exercise) }
        val chartPoints = remember(state.points, cycleFilter) {
            when (cycleFilter) {
                "4W" -> state.points.takeLast(4)
                "8W" -> state.points.takeLast(8)
                "12W" -> state.points.takeLast(12)
                else -> state.points
            }
        }
        val latest = chartPoints.lastOrNull()
        val first = chartPoints.firstOrNull()
        val suggestion = state.suggestion

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // 1. Top Exercise Selector & Time Filters
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = IronSurfaceContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showExerciseMenu = true },
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
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
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(IronSurfaceHighest),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.FitnessCenter,
                                            contentDescription = null,
                                            tint = NeonPrimary,
                                        )
                                    }
                                    Column(Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = exercise.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = IronOnSurface,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                            Icon(
                                                imageVector = Icons.Filled.ExpandMore,
                                                contentDescription = null,
                                                tint = IronOnSurfaceVariant,
                                            )
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        ) {
                                            Text(
                                                text = meta.vietnameseName,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = IronOnSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = IronSurfaceHighest,
                                            ) {
                                                Text(
                                                    text = "${exercise.muscle.label} (CHÍNH)",
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = NeonPrimary,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = showExerciseMenu,
                            onDismissRequest = { showExerciseMenu = false },
                        ) {
                            state.catalog.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text("${item.name} (${item.muscle.label})") },
                                    onClick = {
                                        viewModel.selectExercise(item.id)
                                        showExerciseMenu = false
                                    },
                                )
                            }
                        }
                    }

                    // Cycle Selector Pills
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = IronSurfaceLowest,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            listOf("4W" to "4 tuần", "8W" to "8 tuần", "12W" to "12 tuần", "ALL" to "Tất cả").forEach { (key, label) ->
                                val active = cycleFilter == key
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) IronSurfaceHighest else Color.Transparent)
                                        .clickable { cycleFilter = key }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (active) NeonPrimary else IronOnSurfaceVariant,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Overload Status Hero Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = IronSurfaceContainer,
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = IronSurfaceLowest,
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = NeonPrimary,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Text(
                                        text = "DOUBLE PROGRESSION + RIR",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonPrimary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                            Surface(
                                shape = CircleShape,
                                color = IronSurfaceHigh,
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.EmojiEvents,
                                        contentDescription = null,
                                        tint = BurntSecondary,
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Text(
                                        text = "PR Vòng lặp",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BurntSecondary,
                                    )
                                }
                            }
                        }

                        // Main Next Target
                        Column {
                            Text(
                                text = "MỤC TIÊU BUỔI TỚI (NEXT TARGET)",
                                style = MaterialTheme.typography.labelMedium,
                                color = IronOnSurfaceVariant,
                            )
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = if (suggestion != null) {
                                        "${formatLoad(suggestion.weightKg, exercise.addedLoad)} × ${suggestion.targetReps} reps"
                                    } else {
                                        "${formatLoad(exercise.startKg, exercise.addedLoad)} × ${exercise.repMin}-${exercise.repMax} reps"
                                    },
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = NeonPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                )
                                Text(
                                    text = "@ RIR 2",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = IronOnSurface,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        // Algorithm Note
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = IronSurfaceLowest,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = NeonPrimary,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = suggestion?.reason ?: "Ghi nhận hiệp tập đầu tiên để hệ thống tính toán lộ trình tăng tạ.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = IronOnSurface,
                                )
                            }
                        }

                        // Current e1RM Stat Footnote
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            Column {
                                Text("e1RM Ước tính hiện tại", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    val est = latest?.bestE1rm
                                    Text(
                                        text = if (est != null) formatEstimate(est) else "${latest?.bestReps ?: exercise.repMin}",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = IronOnSurface,
                                        fontWeight = FontWeight.ExtraBold,
                                    )
                                    Text(
                                        text = if (est != null) "kg" else "reps",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = IronOnSurfaceVariant,
                                    )
                                }
                            }

                            val latestEst = latest?.bestE1rm
                            val firstEst = first?.bestE1rm
                            val delta = if (latestEst != null && firstEst != null && chartPoints.size > 1) {
                                latestEst - firstEst
                            } else {
                                null
                            }
                            if (delta != null && abs(delta) >= 0.05) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = IronSurfaceHigh,
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.NorthEast,
                                                contentDescription = null,
                                                tint = NeonPrimary,
                                                modifier = Modifier.size(14.dp),
                                            )
                                            Text(
                                                text = "${formatSignedEstimate(delta)} kg",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = NeonPrimary,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                    Text(
                                        text = "trong chu kỳ chọn",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IronOnSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Dual Metric Trends Chart Card (e1RM & RIR)
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = IronSurfaceContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Column {
                            Text(
                                text = "Tiến độ e1RM & RIR trung bình",
                                style = MaterialTheme.typography.titleMedium,
                                color = IronOnSurface,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Công thức Epley: 1RM ≈ tạ × (1 + rep ÷ 30)",
                                style = MaterialTheme.typography.labelSmall,
                                color = IronOnSurfaceVariant,
                            )
                        }

                        // Legend
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp, 4.dp)
                                        .clip(CircleShape)
                                        .background(NeonPrimaryContainer),
                                )
                                Text("e1RM (kg)", style = MaterialTheme.typography.labelSmall, color = IronOnSurface)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp, 4.dp)
                                        .clip(CircleShape)
                                        .background(BurntSecondary),
                                )
                                Text("RIR TB (Dự phòng)", style = MaterialTheme.typography.labelSmall, color = IronOnSurface)
                            }
                        }

                        val values = chartPoints.map { it.bestE1rm ?: it.bestReps.toDouble() }
                        val rirSeries = chartPoints.map { 2.0 }
                        if (values.isNotEmpty()) {
                            ProgressLineChart(
                                values = values,
                                color = NeonPrimaryContainer,
                                secondaryValues = rirSeries,
                                modifier = Modifier.semantics {
                                    contentDescription = "Biểu đồ 1RM ước tính"
                                },
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = formatSessionWhen(chartPoints.first().startedAt),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IronOnSurfaceVariant,
                                )
                                if (chartPoints.size > 1) {
                                    Text(
                                        text = formatSessionWhen(chartPoints.last().startedAt),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonPrimary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Chưa có buổi tập cho bài này. Bấm nút bên dưới để áp dụng vào buổi tập hôm nay.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = IronOnSurfaceVariant,
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = IronSurfaceLow,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lightbulb,
                                    contentDescription = null,
                                    tint = NeonPrimary,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = "Xu hướng phân tích: RIR ổn định mức 1.5 - 2.0 giúp mức tạ tăng đều đặn mà không tích tụ quá tải thần kinh (CNS Fatigue).",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = IronOnSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }

            // 4. Overload Readiness & Threshold Indicators (3 mini-cards)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ThresholdMiniCard(
                        title = "Ngưỡng RIR",
                        value = "2.0 / 1.0",
                        sub = "Đạt chuẩn ≥ 1",
                        icon = Icons.Filled.CheckCircle,
                        modifier = Modifier.weight(1f),
                    )
                    ThresholdMiniCard(
                        title = "Rep Mục Tiêu",
                        value = "${latest?.bestReps ?: exercise.repMin} / ${exercise.repMax}",
                        sub = "${exercise.repMin}–${exercise.repMax} reps",
                        icon = Icons.Filled.CheckCircle,
                        modifier = Modifier.weight(1f),
                    )
                    ThresholdMiniCard(
                        title = "Tần suất",
                        value = "2 lần / tuần",
                        sub = "Hypertrophy",
                        icon = Icons.Filled.Bolt,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // 5. Detailed History Table
            if (state.points.isNotEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = IronSurfaceContainer,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.History,
                                    contentDescription = null,
                                    tint = IronOnSurfaceVariant,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = "Lịch sử buổi tập & Dữ liệu RIR",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = IronOnSurface,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            // Table header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(IronSurfaceLowest)
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                            ) {
                                Text("NGÀY", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant, modifier = Modifier.weight(0.9f))
                                Text("SET TỐT NHẤT", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant, modifier = Modifier.weight(1.2f))
                                Text("VOLUME", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant, modifier = Modifier.weight(1f))
                                Text("KẾT QUẢ", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant, textAlign = TextAlign.End, modifier = Modifier.weight(0.9f))
                            }

                            state.points.asReversed().take(8).forEach { pt ->
                                HistoryTableRow(
                                    point = pt,
                                    repMax = exercise.repMax,
                                    addedLoad = exercise.addedLoad,
                                )
                            }
                        }
                    }
                }
            }

            // 6. Educational Principle Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = IronSurfaceLow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandPrinciple = !expandPrinciple },
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.School,
                                    contentDescription = null,
                                    tint = NeonPrimary,
                                    modifier = Modifier.size(20.dp),
                                )
                                Text(
                                    text = "Nguyên lý Double Progression + RIR",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = IronOnSurface,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.ExpandMore,
                                contentDescription = null,
                                tint = IronOnSurfaceVariant,
                            )
                        }
                        if (expandPrinciple) {
                            Text(
                                text = "Trong khoảng lặp mục tiêu ${exercise.repMin} - ${exercise.repMax} reps: bạn giữ nguyên mức tạ và tăng dần số rep qua các buổi cho đến khi đạt trần ${exercise.repMax} reps ở tất cả các set với RIR ≥ 1, sau đó mới tăng +${formatLoad(exercise.incrementKg, false)}.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = IronOnSurfaceVariant,
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = IronSurfaceContainer,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = null,
                                        tint = BurntSecondary,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Text(
                                        text = "Quy tắc này ngăn chặn chấn thương khớp và tránh tình trạng chững tạ non (Premature Plateau).",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IronOnSurface,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (restVisible) {
            ActiveRestBanner(onOpenSession = onOpenWorkout)
        }

        // Sticky Bottom CTA Bar
        Surface(
            color = IronSurface.copy(alpha = 0.95f),
            shadowElevation = 12.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Button(
                    onClick = { onApplyToWorkout(exercise.name) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPrimaryContainer,
                        contentColor = OnNeonPrimary,
                    ),
                ) {
                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "ÁP DỤNG VÀO BUỔI TẬP HÔM NAY",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ThresholdMiniCard(
    title: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = IronSurfaceContainer,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(title, style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeonPrimary,
                    modifier = Modifier.size(15.dp),
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = IronOnSurface,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall,
                color = NeonPrimary,
            )
        }
    }
}

@Composable
private fun HistoryTableRow(
    point: ProgressPoint,
    repMax: Int,
    addedLoad: Boolean,
) {
    val hitTop = point.bestReps >= repMax
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = formatSessionWhen(point.startedAt),
            style = MaterialTheme.typography.labelMedium,
            color = IronOnSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.9f),
        )
        Text(
            text = if (point.e1rmWeightKg != null) {
                "${formatLoad(point.e1rmWeightKg, addedLoad)} × ${point.e1rmReps}"
            } else {
                "${point.bestReps} reps"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = IronOnSurface,
            modifier = Modifier.weight(1.2f),
        )
        Text(
            text = formatVolume(point.volumeKg),
            style = MaterialTheme.typography.bodyMedium,
            color = IronOnSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier.weight(0.9f),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (hitTop) NeonPrimaryContainer else IronSurfaceHighest,
            ) {
                Text(
                    text = if (hitTop) "Tăng tạ" else "Tăng rep",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (hitTop) OnNeonPrimary else EmeraldTertiary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
