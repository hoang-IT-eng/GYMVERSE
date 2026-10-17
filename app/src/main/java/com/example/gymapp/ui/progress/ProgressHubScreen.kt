package com.example.gymapp.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.gymapp.GymApp
import com.example.gymapp.data.GymSnapshot
import com.example.gymapp.domain.IronLogCatalog
import com.example.gymapp.domain.MuscleVolumeLandmark
import com.example.gymapp.domain.formatDuration
import com.example.gymapp.domain.formatEstimate
import com.example.gymapp.domain.formatLoad
import com.example.gymapp.domain.formatSessionWhen
import com.example.gymapp.domain.pointsForExercise
import com.example.gymapp.ui.components.ProgressLineChart
import com.example.gymapp.ui.home.HomeUiState
import com.example.gymapp.ui.home.RecentSessionUi
import com.example.gymapp.ui.theme.BurntSecondary
import com.example.gymapp.ui.theme.BurntSecondaryContainer
import com.example.gymapp.ui.theme.EmeraldTertiary
import com.example.gymapp.ui.theme.IronOnSurface
import com.example.gymapp.ui.theme.IronOnSurfaceVariant
import com.example.gymapp.ui.theme.IronOutline
import com.example.gymapp.ui.theme.IronSurface
import com.example.gymapp.ui.theme.IronSurfaceContainer
import com.example.gymapp.ui.theme.IronSurfaceHigh
import com.example.gymapp.ui.theme.IronSurfaceHighest
import com.example.gymapp.ui.theme.IronSurfaceLow
import com.example.gymapp.ui.theme.IronSurfaceLowest
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.NeonPrimaryContainer
import com.example.gymapp.ui.theme.OnNeonPrimary
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Locale

@Composable
fun ProgressHubScreen(
    homeState: HomeUiState,
    onOpenOverloadDetail: (Long) -> Unit,
    onOpenWorkout: (Long) -> Unit,
) {
    val context = LocalContext.current
    val app = context.applicationContext as GymApp
    val snapshot by remember { app.repository.observeGym() }.collectAsState(
        initial = GymSnapshot(emptyList(), emptyList(), emptyList(), emptyList()),
    )

    var categoryTab by rememberSaveable { mutableStateOf("strength") }
    var selectedExerciseId by rememberSaveable {
        mutableLongStateOf(homeState.progress.firstOrNull()?.exerciseId ?: homeState.catalog.firstOrNull()?.id ?: 1L)
    }
    var weekOffset by rememberSaveable { mutableIntStateOf(0) }
    var showDropdown by remember { mutableStateOf(false) }

    val selectedExercise = snapshot.exercises.find { it.id == selectedExerciseId }
        ?: snapshot.exercises.firstOrNull()
    val points = remember(snapshot, selectedExercise?.id) {
        if (selectedExercise != null) {
            pointsForExercise(snapshot.sets, snapshot.sessions, selectedExercise.id)
        } else {
            emptyList()
        }
    }
    val (weekLabel, landmarks) = remember(snapshot, weekOffset) {
        IronLogCatalog.computeWeeklyLandmarks(
            exercises = snapshot.exercises,
            sessions = snapshot.sessions,
            sets = snapshot.sets,
            weekOffset = weekOffset,
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // 1. Header & Navigation Hub
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "DỮ LIỆU HYPERTROPHY",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = if (categoryTab == "history") "Lịch sử buổi tập" else "Tiến bộ & Phân tích",
                            style = MaterialTheme.typography.headlineLarge,
                            color = IronOnSurface,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = IronSurfaceHigh,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(NeonPrimary),
                            )
                            Text(
                                text = "Chu kỳ 12W",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonPrimary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                // Category Tabs
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    HubCategoryChip(
                        icon = Icons.Filled.FitnessCenter,
                        label = "Bài tập & Sức mạnh",
                        selected = categoryTab == "strength",
                        onClick = { categoryTab = "strength" },
                    )
                    HubCategoryChip(
                        icon = Icons.Filled.ViewTimeline,
                        label = "Khối lượng & Nhóm cơ",
                        selected = categoryTab == "volume",
                        onClick = { categoryTab = "volume" },
                    )
                    HubCategoryChip(
                        icon = Icons.Filled.History,
                        label = "Lịch sử buổi tập (${homeState.recent.size})",
                        selected = categoryTab == "history",
                        onClick = { categoryTab = "history" },
                    )
                }
            }
        }

        if (categoryTab == "history") {
            // Stitch Screen: Lịch sử buổi tập - IronLog Workout History
            item {
                HistorySnapshotAndCalendar(
                    sessions = snapshot.sessions,
                    sets = snapshot.sets,
                    weeklyPrCount = homeState.weeklyPrCount,
                )
            }

            if (homeState.recent.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = IronSurfaceContainer,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Chưa có buổi tập nào được lưu. Bấm nút Ghi tập ở giữa thanh dưới để bắt đầu!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = IronOnSurfaceVariant,
                        )
                    }
                }
            } else {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp, 16.dp)
                                    .clip(CircleShape)
                                    .background(NeonPrimary),
                            )
                            Text(
                                text = "NHẬT KÝ BUỔI TẬP GẦN ĐÂY",
                                style = MaterialTheme.typography.labelSmall,
                                color = IronOnSurface,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }
                        Text(
                            text = "${homeState.recent.size} buổi",
                            style = MaterialTheme.typography.labelSmall,
                            color = IronOnSurfaceVariant,
                        )
                    }
                }

                items(homeState.recent, key = { it.sessionId }) { session ->
                    HistoryFeedWorkoutCard(
                        session = session,
                        onClick = { onOpenWorkout(session.sessionId) },
                    )
                }
            }

            // Social Share Story Card
            item {
                SocialShareStoryCard(
                    sessionCount = snapshot.sessions.size,
                    totalVolumeKg = snapshot.sets.sumOf { it.weightKg * it.reps },
                    prCount = homeState.weeklyPrCount,
                    streakDays = homeState.streakDays,
                )
            }
        } else {
            // Strength & Volume Analytics View (Stitch Screen: Tiến bộ & Phân tích)
            if (categoryTab == "strength" && selectedExercise != null) {
                item {
                    Box {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = IronSurfaceHigh,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDropdown = true },
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                                        Text(
                                            text = selectedExercise.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = IronOnSurface,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = IronSurfaceHighest,
                                            ) {
                                                Text(
                                                    text = selectedExercise.muscle.label,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = NeonPrimary,
                                                )
                                            }
                                            Text(
                                                text = "${selectedExercise.repMin}–${selectedExercise.repMax} reps",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = IronOnSurfaceVariant,
                                            )
                                        }
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Đổi bài", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                                    Icon(
                                        imageVector = Icons.Filled.ExpandMore,
                                        contentDescription = null,
                                        tint = IronOnSurfaceVariant,
                                    )
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false },
                        ) {
                            snapshot.exercises.forEach { ex ->
                                DropdownMenuItem(
                                    text = { Text("${ex.name} • ${ex.muscle.label}") },
                                    onClick = {
                                        selectedExerciseId = ex.id
                                        showDropdown = false
                                    },
                                )
                            }
                        }
                    }
                }

                // 3. Line Chart e1RM & PR Tracking
                item {
                    val latest = points.lastOrNull()
                    val first = points.firstOrNull()
                    val est = latest?.bestE1rm
                    val delta = if (est != null && first?.bestE1rm != null && points.size > 1) {
                        est - first.bestE1rm
                    } else {
                        null
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = IronSurfaceHigh,
                        shadowElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top,
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ShowChart,
                                            contentDescription = null,
                                            tint = NeonPrimary,
                                            modifier = Modifier.size(18.dp),
                                        )
                                        Text(
                                            text = "ĐƯỜNG CONG E1RM & KỶ LỤC TẠ (PR)",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = IronOnSurface,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Text(
                                            text = if (est != null) formatEstimate(est) else "${latest?.bestReps ?: selectedExercise.repMin}",
                                            style = MaterialTheme.typography.displaySmall,
                                            color = IronOnSurface,
                                            fontWeight = FontWeight.ExtraBold,
                                        )
                                        Text(
                                            text = if (est != null) "kg" else "reps",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = IronOnSurfaceVariant,
                                        )
                                        if (delta != null) {
                                            Surface(
                                                shape = CircleShape,
                                                color = NeonPrimary.copy(alpha = 0.15f),
                                            ) {
                                                Text(
                                                    text = "${if (delta >= 0) "+" else ""}${formatEstimate(delta)} kg chu kỳ",
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = NeonPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if (latest != null && latest.e1rmWeightKg != null) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = IronSurfaceHighest,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Stars,
                                                contentDescription = null,
                                                tint = NeonPrimary,
                                                modifier = Modifier.size(14.dp),
                                            )
                                            Text(
                                                text = "${formatSessionWhen(latest.startedAt)}: ${est?.let { formatEstimate(it) } ?: ""} kg e1RM",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = NeonPrimary,
                                                fontWeight = FontWeight.ExtraBold,
                                            )
                                        }
                                        Text(
                                            text = "Set tốt nhất: ${formatLoad(latest.e1rmWeightKg, selectedExercise.addedLoad)} × ${latest.e1rmReps} reps @ RIR 2",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = IronOnSurfaceVariant,
                                        )
                                    }
                                }
                            }

                            val series = points.map { it.bestE1rm ?: it.bestReps.toDouble() }
                            if (series.isNotEmpty()) {
                                ProgressLineChart(
                                    values = series,
                                    color = NeonPrimaryContainer,
                                )
                            } else {
                                Text(
                                    text = "Ghi nhận ít nhất 1 buổi tập cho ${selectedExercise.name} để vẽ đường cong e1RM.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = IronOnSurfaceVariant,
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = IronSurfaceContainer,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Verified,
                                            contentDescription = null,
                                            tint = NeonPrimary,
                                            modifier = Modifier.size(18.dp),
                                        )
                                        Text(
                                            text = "Đạt chuẩn Progressive Overload",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = IronOnSurface,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                    Text(
                                        text = "${points.size} buổi ghi nhận",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonPrimary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Hypertrophy Weekly Volume Landmarks (Sets / Week)
            item {
                WeeklyVolumeLandmarksCard(
                    weekLabel = weekLabel,
                    landmarks = landmarks,
                    onPrevWeek = { weekOffset -= 1 },
                    onNextWeek = { if (weekOffset < 0) weekOffset += 1 },
                )
            }

            // 5. AI Hypertrophy Coach Insights
            item {
                val underMev = landmarks.firstOrNull { it.isUnderMev }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = IronSurfaceHigh,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SmartToy,
                                    contentDescription = null,
                                    tint = NeonPrimary,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                            Column {
                                Text(
                                    text = "Khuyến nghị từ AI Coach",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = IronOnSurface,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "Dựa trên RIR & Khối lượng MEV–MAV–MRV",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonPrimary,
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = IronSurfaceContainer,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lightbulb,
                                    contentDescription = null,
                                    tint = BurntSecondary,
                                    modifier = Modifier.size(20.dp),
                                )
                                Column {
                                    Text(
                                        text = if (underMev != null) "Cần bổ sung khối lượng (${underMev.title})" else "Khối lượng tuần đạt ngưỡng tối ưu",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = BurntSecondary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = if (underMev != null) {
                                            "Nhóm ${underMev.title} hiện có ${underMev.setsCompleted}/${underMev.mevSets} sets (Dưới MEV). Hãy thêm ${(underMev.mevSets - underMev.setsCompleted).coerceAtLeast(2)} sets vào buổi tập tới để kích thích tăng trưởng cơ."
                                        } else {
                                            "Tất cả nhóm cơ đều nằm trong vùng MAV 10–20 sets/tuần. Tiếp tục duy trì Double Progression!"
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = IronOnSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Action CTA to open Progressive Overload Detail screen
            if (selectedExercise != null) {
                item {
                    Button(
                        onClick = { onOpenOverloadDetail(selectedExercise.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonPrimary,
                            contentColor = OnNeonPrimary,
                        ),
                    ) {
                        Icon(imageVector = Icons.Filled.Timeline, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Xem chi tiết chu kỳ Progressive Overload bài này",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HubCategoryChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) NeonPrimary else IronSurfaceHigh,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) OnNeonPrimary else IronOnSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) OnNeonPrimary else IronOnSurfaceVariant,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun WeeklyVolumeLandmarksCard(
    weekLabel: String,
    landmarks: List<MuscleVolumeLandmark>,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = IronSurfaceHigh,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Equalizer,
                            contentDescription = null,
                            tint = NeonPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = "Khối lượng tập theo nhóm cơ",
                            style = MaterialTheme.typography.titleMedium,
                            color = IronOnSurface,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = IronSurfaceContainer,
                    ) {
                        Text(
                            text = "Sets/Tuần",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = IronOnSurfaceVariant,
                        )
                    }
                }
                Text(
                    text = "Khoảng Hypertrophy khuyến nghị: 10 – 20 sets/tuần (tối thiểu MEV – tối đa MRV)",
                    style = MaterialTheme.typography.labelSmall,
                    color = IronOnSurfaceVariant,
                )
            }

            // Week Switcher Pill
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = IronSurfaceContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(IronSurfaceHighest)
                            .clickable(onClick = onPrevWeek),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronLeft,
                            contentDescription = "Tuần trước",
                            tint = IronOnSurface,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarToday,
                            contentDescription = null,
                            tint = NeonPrimary,
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = weekLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = IronOnSurface,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(IronSurfaceHighest)
                            .clickable(onClick = onNextWeek),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = "Tuần sau",
                            tint = IronOnSurface,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            // Legend Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                VolumeLegendDot(IronSurfaceHighest, "<10s Duy trì (MEV)")
                VolumeLegendDot(NeonPrimary, "10-20s Tối ưu (MAV)")
                VolumeLegendDot(BurntSecondaryContainer, ">20s Quá tải (MRV)")
            }

            // Volume Bars
            landmarks.forEach { item ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelMedium,
                                color = IronOnSurface,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (item.isUnderMev || item.isOverMrv) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = BurntSecondary,
                                    modifier = Modifier.size(14.dp),
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "${item.setsCompleted}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (item.isUnderMev || item.isOverMrv) BurntSecondary else NeonPrimary,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            Text(
                                text = "/ ${item.mrvSets} sets",
                                style = MaterialTheme.typography.labelSmall,
                                color = IronOnSurfaceVariant,
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (item.isUnderMev || item.isOverMrv) {
                                    BurntSecondaryContainer.copy(alpha = 0.25f)
                                } else {
                                    NeonPrimary.copy(alpha = 0.14f)
                                },
                            ) {
                                Text(
                                    text = item.zoneLabel,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (item.isUnderMev || item.isOverMrv) BurntSecondary else NeonPrimary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(IronSurfaceHighest),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(item.progressFraction.coerceAtLeast(0.05f))
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(
                                    if (item.isUnderMev || item.isOverMrv) BurntSecondaryContainer else NeonPrimary,
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VolumeLegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color),
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
    }
}

@Composable
private fun HistorySnapshotAndCalendar(
    sessions: List<com.example.gymapp.domain.Session>,
    sets: List<com.example.gymapp.domain.SetEntry>,
    weeklyPrCount: Int,
) {
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    val yearMonth = YearMonth.from(today)
    val totalVol = sets.sumOf { it.weightKg * it.reps }
    val totalMinutes = sessions.sumOf { s ->
        val end = s.finishedAt ?: s.startedAt
        ((end - s.startedAt) / 60_000L).coerceAtLeast(15L)
    }
    val trainedDays = sessions.map {
        Instant.ofEpochMilli(it.startedAt).atZone(zone).toLocalDate()
    }.toSet()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 3-Metric Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = IronSurfaceLow,
                modifier = Modifier.weight(1f),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("VOLUME", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                        Icon(Icons.Filled.StackedBarChart, null, tint = NeonPrimary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (totalVol >= 1000) String.format(Locale.US, "%.1fk kg", totalVol / 1000.0) else "${totalVol.toInt()} kg",
                        style = MaterialTheme.typography.titleLarge,
                        color = IronOnSurface,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text("Tích lũy", style = MaterialTheme.typography.labelSmall, color = NeonPrimary)
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = IronSurfaceLow,
                modifier = Modifier.weight(1f),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("THỜI GIAN", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                        Icon(Icons.Filled.Timer, null, tint = BurntSecondary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "${totalMinutes / 60}h ${totalMinutes % 60}p",
                        style = MaterialTheme.typography.titleLarge,
                        color = IronOnSurface,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text("${sessions.size} buổi", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = IronSurfaceLow,
                modifier = Modifier.weight(1f),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("KỶ LỤC PR", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                        Icon(Icons.Filled.Stars, null, tint = NeonPrimary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "$weeklyPrCount PRs",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeonPrimary,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text("Tuần này", style = MaterialTheme.typography.labelSmall, color = NeonPrimary)
                }
            }
        }

        // Neon Dot-indicator Calendar Matrix
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = IronSurfaceLow,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
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
                        Text(
                            text = "Tháng ${yearMonth.monthValue}, ${yearMonth.year}",
                            style = MaterialTheme.typography.titleMedium,
                            color = IronOnSurface,
                            fontWeight = FontWeight.Bold,
                        )
                        Surface(
                            shape = CircleShape,
                            color = NeonPrimary.copy(alpha = 0.14f),
                        ) {
                            Text(
                                text = "${trainedDays.count { YearMonth.from(it) == yearMonth }} Buổi",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonPrimary,
                            )
                        }
                    }
                }

                // Day of week headers
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach { d ->
                        Text(d, style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                    }
                }

                val firstDayOffset = yearMonth.atDay(1).dayOfWeek.value - 1
                val daysInMonth = yearMonth.lengthOfMonth()
                val totalCells = ((firstDayOffset + daysInMonth + 6) / 7) * 7

                for (row in 0 until (totalCells / 7)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        for (col in 0..6) {
                            val cellIndex = row * 7 + col
                            val dayNum = cellIndex - firstDayOffset + 1
                            if (dayNum in 1..daysInMonth) {
                                val date = yearMonth.atDay(dayNum)
                                val isToday = date == today
                                val trained = date in trainedDays
                                Column(
                                    modifier = Modifier.size(34.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isToday) NeonPrimary.copy(alpha = 0.22f) else Color.Transparent,
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "$dayNum",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = when {
                                                isToday -> NeonPrimary
                                                trained -> IronOnSurface
                                                else -> IronOnSurfaceVariant.copy(alpha = 0.45f)
                                            },
                                            fontWeight = if (isToday || trained) FontWeight.Bold else FontWeight.Normal,
                                        )
                                    }
                                    if (trained) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(NeonPrimary),
                                        )
                                    }
                                }
                            } else {
                                Spacer(Modifier.size(34.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryFeedWorkoutCard(
    session: RecentSessionUi,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = IronSurfaceContainer,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = IronOnSurface,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = formatSessionWhen(session.startedAt),
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeonPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("•", color = IronOnSurfaceVariant)
                    Text(
                        text = session.finishedAt?.let { formatDuration(session.startedAt, it) } ?: "Đang mở",
                        style = MaterialTheme.typography.bodyMedium,
                        color = IronOnSurfaceVariant,
                    )
                    Text("•", color = IronOnSurfaceVariant)
                    Text(
                        text = "${session.setCount} hiệp",
                        style = MaterialTheme.typography.bodyMedium,
                        color = IronOnSurfaceVariant,
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = IronSurfaceLowest,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text("TỔNG KHỐI LƯỢNG", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                        Text(
                            text = "${String.format(Locale.US, "%,d", session.volumeKg.toLong())} kg",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonPrimary,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("RIR TRUNG BÌNH", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                        Text(
                            text = "1.8 RIR (Tối ưu)",
                            style = MaterialTheme.typography.titleMedium,
                            color = IronOnSurface,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            if (session.exercisesSummary.isNotEmpty()) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    session.exercisesSummary.forEach { name ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = IronSurfaceHigh,
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(NeonPrimary),
                                )
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = IronOnSurface,
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Xem chi tiết ${session.setCount} hiệp tập",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = NeonPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun SocialShareStoryCard(
    sessionCount: Int,
    totalVolumeKg: Double,
    prCount: Int,
    streakDays: Int,
) {
    var shared by remember { mutableStateOf(false) }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = IronSurfaceHigh,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
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
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonPrimaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "IL",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnNeonPrimary,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Column {
                        Text(
                            text = "Khoe thành tích IronLog",
                            style = MaterialTheme.typography.titleMedium,
                            color = IronOnSurface,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = "Đạt mốc $sessionCount buổi tập hoàn thành!",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonPrimary,
                        )
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = BurntSecondary.copy(alpha = 0.2f),
                ) {
                    Text(
                        text = "🔥 $streakDays Days",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = BurntSecondary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = IronSurface,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Buổi tập", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                        Text("$sessionCount", style = MaterialTheme.typography.titleLarge, color = IronOnSurface, fontWeight = FontWeight.ExtraBold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tổng tạ nâng", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                        Text(
                            text = String.format(Locale.US, "%.1fT", totalVolumeKg / 1000.0),
                            style = MaterialTheme.typography.titleLarge,
                            color = NeonPrimary,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Kỷ lục mới", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                        Text("$prCount PRs", style = MaterialTheme.typography.titleLarge, color = BurntSecondary, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Button(
                onClick = { shared = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (shared) EmeraldTertiary else NeonPrimaryContainer,
                    contentColor = OnNeonPrimary,
                ),
            ) {
                Icon(imageVector = Icons.Filled.IosShare, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (shared) "ĐÃ TẠO ẢNH STORY THÀNH CÔNG!" else "CHIA SẺ THÀNH TÍCH (STORY / ẢNH)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}
