package com.example.gymapp.ui.routines

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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymapp.GymApp
import com.example.gymapp.domain.Exercise
import com.example.gymapp.domain.IronLogCatalog
import com.example.gymapp.domain.MuscleGroup
import com.example.gymapp.domain.RoutineDaySpec
import com.example.gymapp.domain.RoutinePlan
import com.example.gymapp.domain.formatEstimate
import com.example.gymapp.domain.formatKg
import com.example.gymapp.service.RestTimerStore
import com.example.gymapp.ui.home.HomeViewModel
import com.example.gymapp.ui.theme.BurntSecondary
import com.example.gymapp.ui.theme.BurntSecondaryContainer
import com.example.gymapp.ui.theme.EmeraldTertiary
import com.example.gymapp.ui.theme.IronOnSurface
import com.example.gymapp.ui.theme.IronOnSurfaceVariant
import com.example.gymapp.ui.theme.IronSurfaceContainer
import com.example.gymapp.ui.theme.IronSurfaceHigh
import com.example.gymapp.ui.theme.IronSurfaceHighest
import com.example.gymapp.ui.theme.IronSurfaceLow
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.NeonPrimaryContainer
import com.example.gymapp.ui.theme.OnBurntSecondaryContainer
import com.example.gymapp.ui.theme.OnNeonPrimary
import kotlinx.coroutines.launch

@Composable
fun RoutinesAndLibraryScreen(
    homeViewModel: HomeViewModel,
    initialTab: String = "routines",
    onOpenWorkout: (Long) -> Unit,
    onOpenExerciseDetail: (Long) -> Unit,
) {
    val state by homeViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var activeTab by rememberSaveable(initialTab) { mutableStateOf(initialTab) }
    var routineFilter by rememberSaveable { mutableStateOf("all") }
    var expandedRoutineId by rememberSaveable { mutableStateOf(state.activeRoutine.id) }
    var activeRoutineId by remember { mutableStateOf(RestTimerStore.readActiveRoutineId(context)) }
    var showCreateExerciseDialog by remember { mutableStateOf(false) }
    var editorPlan by remember { mutableStateOf<RoutinePlan?>(null) }
    var editorOpen by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        // Top Segmented Switcher: Giáo án vs Thư viện bài tập
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = IronSurfaceLow,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    val isRoutines = activeTab == "routines"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isRoutines) IronSurfaceHigh else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { activeTab = "routines" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = "Giáo án Khoa Học",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isRoutines) NeonPrimary else IronOnSurfaceVariant,
                                fontWeight = if (isRoutines) FontWeight.Bold else FontWeight.Medium,
                            )
                            Surface(
                                shape = CircleShape,
                                color = NeonPrimary.copy(alpha = 0.14f),
                            ) {
                                Text(
                                    text = "${IronLogCatalog.routines.size}",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonPrimary,
                                )
                            }
                        }
                    }

                    val isLibrary = activeTab == "library"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isLibrary) IronSurfaceHigh else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { activeTab = "library" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = "Thư viện bài tập",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isLibrary) NeonPrimary else IronOnSurfaceVariant,
                                fontWeight = if (isLibrary) FontWeight.Bold else FontWeight.Medium,
                            )
                            Surface(
                                shape = CircleShape,
                                color = IronSurfaceHighest,
                            ) {
                                Text(
                                    text = "${state.catalog.size}",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IronOnSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }

        if (activeTab == "routines") {
            RoutinesPane(
                routineFilter = routineFilter,
                onSelectFilter = { routineFilter = it },
                expandedRoutineId = expandedRoutineId,
                onToggleExpand = { id ->
                    expandedRoutineId = if (expandedRoutineId == id) "" else id
                },
                activeRoutineId = activeRoutineId,
                onSelectActiveRoutine = { id ->
                    activeRoutineId = id
                    RestTimerStore.saveActiveRoutineId(context, id)
                },
                customPlans = state.customPlans,
                onCompose = {
                    editorPlan = null
                    editorOpen = true
                },
                onEditPlan = { plan ->
                    editorPlan = plan
                    editorOpen = true
                },
                onDeletePlan = { plan ->
                    val rawId = plan.id.removePrefix("custom:").toLongOrNull()
                    if (rawId != null) scope.launch {
                        val app = context.applicationContext as GymApp
                        app.repository.deleteCustomRoutine(rawId)
                        if (activeRoutineId == plan.id) {
                            activeRoutineId = "ppl_6d"
                            RestTimerStore.saveActiveRoutineId(context, "ppl_6d")
                        }
                    }
                },
                onStartRoutineDay = { daySpec ->
                    scope.launch {
                        val sessionId = homeViewModel.startRoutineDay(daySpec)
                        onOpenWorkout(sessionId)
                    }
                },
            )
        } else {
            val e1rmByExercise = remember(state.progress) {
                state.progress.associateBy { it.exerciseId }
            }
            ExerciseLibraryPane(
                catalog = state.catalog,
                progressMap = e1rmByExercise,
                onOpenExerciseDetail = onOpenExerciseDetail,
                onOpenCreateDialog = { showCreateExerciseDialog = true },
            )
        }
    }

    if (showCreateExerciseDialog) {
        CreateCustomExerciseDialog(
            onDismiss = { showCreateExerciseDialog = false },
            onCreate = { name, muscle, repMin, repMax, inc ->
                val app = context.applicationContext as GymApp
                scope.launch {
                    val id = app.repository.createCustomExercise(name, muscle, repMin, repMax, inc)
                    showCreateExerciseDialog = false
                    onOpenExerciseDetail(id)
                }
            },
        )
    }

    if (editorOpen) {
        CustomRoutineEditor(
            catalog = state.catalog,
            existing = editorPlan,
            onDismiss = { editorOpen = false },
            onSave = { name, days ->
                val app = context.applicationContext as GymApp
                val existingId = editorPlan?.id?.removePrefix("custom:")?.toLongOrNull()
                scope.launch {
                    val id = app.repository.saveCustomRoutine(existingId, name, days)
                    activeRoutineId = "custom:$id"
                    RestTimerStore.saveActiveRoutineId(context, "custom:$id")
                    editorOpen = false
                }
            },
        )
    }
}

@Composable
private fun RoutinesPane(
    routineFilter: String,
    onSelectFilter: (String) -> Unit,
    expandedRoutineId: String,
    onToggleExpand: (String) -> Unit,
    activeRoutineId: String,
    onSelectActiveRoutine: (String) -> Unit,
    customPlans: List<RoutinePlan>,
    onCompose: () -> Unit,
    onEditPlan: (RoutinePlan) -> Unit,
    onDeletePlan: (RoutinePlan) -> Unit,
    onStartRoutineDay: (RoutineDaySpec) -> Unit,
) {
    val filters = listOf(
        "all" to "Tất cả",
        "custom" to "Của tôi",
        "3day" to "3 ngày/tuần",
        "4day" to "4 ngày/tuần",
        "6day" to "5 - 6 ngày/tuần",
        "upperlower" to "Upper / Lower",
    )
    val filteredRoutines = IronLogCatalog.routines.filter { plan ->
        routineFilter == "all" || plan.filterTag.contains(routineFilter)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = CircleShape,
                        color = IronSurfaceHigh,
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
                                text = "HYPERTROPHY SPLITS",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonPrimary,
                            )
                        }
                    }
                    Text(
                        text = "Chu kỳ Hypertrophy",
                        style = MaterialTheme.typography.labelSmall,
                        color = IronOnSurfaceVariant,
                    )
                }
                Text(
                    text = "Giáo án tập luyện",
                    style = MaterialTheme.typography.headlineMedium,
                    color = IronOnSurface,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "Tối ưu hóa tần suất, RPE & khối lượng từng nhóm cơ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IronOnSurfaceVariant,
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeonPrimaryContainer,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable(onClick = onCompose),
                ) {
                    Text(
                        text = "Soạn giáo án",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = OnNeonPrimary,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }

        // Filter Chips
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                filters.forEach { (key, label) ->
                    val selected = routineFilter == key
                    Surface(
                        shape = CircleShape,
                        color = if (selected) NeonPrimary else IronSurfaceHigh,
                        modifier = Modifier.clickable { onSelectFilter(key) },
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) OnNeonPrimary else IronOnSurfaceVariant,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                        )
                    }
                }
            }
        }

        if (routineFilter == "all" || routineFilter == "custom") {
            items(customPlans, key = { it.id }) { plan ->
                RoutinePlanCard(
                    plan = plan,
                    isExpanded = expandedRoutineId == plan.id,
                    isActive = activeRoutineId == plan.id,
                    onToggleExpand = { onToggleExpand(plan.id) },
                    onSetActive = { onSelectActiveRoutine(plan.id) },
                    onStartDay = onStartRoutineDay,
                    onEdit = { onEditPlan(plan) },
                    onDelete = { onDeletePlan(plan) },
                )
            }
        }

        if (routineFilter != "custom") {
        items(filteredRoutines, key = { it.id }) { plan ->
            RoutinePlanCard(
                plan = plan,
                isExpanded = expandedRoutineId == plan.id,
                isActive = activeRoutineId == plan.id,
                onToggleExpand = { onToggleExpand(plan.id) },
                onSetActive = { onSelectActiveRoutine(plan.id) },
                onStartDay = onStartRoutineDay,
            )
        }
        }

        // Motivational Progress Callout
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = IronSurfaceContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = NeonPrimary,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Quy luật Progressive Overload",
                            style = MaterialTheme.typography.titleMedium,
                            color = IronOnSurface,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Ghi nhận mức tạ mỗi hiệp để hệ thống tự động tính mức tăng +1.25kg – 2.5kg cho chu kỳ tiếp theo.",
                            style = MaterialTheme.typography.labelSmall,
                            color = IronOnSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutinePlanCard(
    plan: RoutinePlan,
    isExpanded: Boolean,
    isActive: Boolean,
    onToggleExpand: () -> Unit,
    onSetActive: () -> Unit,
    onStartDay: (RoutineDaySpec) -> Unit,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = IronSurfaceLow,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            // Card Header
            Surface(
                color = IronSurfaceContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand),
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
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f),
                        ) {
                            if (isActive) {
                                Surface(
                                    shape = CircleShape,
                                    color = NeonPrimaryContainer,
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = OnNeonPrimary,
                                            modifier = Modifier.size(13.dp),
                                        )
                                        Text(
                                            text = "ĐANG ÁP DỤNG",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnNeonPrimary,
                                            fontWeight = FontWeight.ExtraBold,
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    shape = CircleShape,
                                    color = IronSurfaceHigh,
                                    modifier = Modifier.clickable(onClick = onSetActive),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = NeonPrimary,
                                            modifier = Modifier.size(13.dp),
                                        )
                                        Text(
                                            text = "Áp dụng giáo án",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NeonPrimary,
                                        )
                                    }
                                }
                            }
                            plan.badges.take(2).forEach { badge ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = IronSurfaceHigh,
                                ) {
                                    Text(
                                        text = badge,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IronOnSurfaceVariant,
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(IronSurfaceHigh),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = null,
                                tint = NeonPrimary,
                            )
                        }
                    }

                    Column {
                        Text(
                            text = plan.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = IronOnSurface,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = plan.subtitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = NeonPrimary,
                        )
                        if (onEdit != null || onDelete != null) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                if (onEdit != null) {
                                    Text(
                                        text = "Sửa",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = NeonPrimary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable(onClick = onEdit),
                                    )
                                }
                                if (onDelete != null) {
                                    Text(
                                        text = "Xóa",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable(onClick = onDelete),
                                    )
                                }
                            }
                        }
                    }

                    // Quick Stats 3-col Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RoutineMiniStat(
                            icon = Icons.Filled.CalendarMonth,
                            label = "Buổi",
                            value = plan.daysPerWeek,
                            modifier = Modifier.weight(1f),
                        )
                        RoutineMiniStat(
                            icon = Icons.Filled.Timer,
                            label = "Thời lượng",
                            value = plan.durationText,
                            modifier = Modifier.weight(1f),
                        )
                        RoutineMiniStat(
                            icon = Icons.Filled.Repeat,
                            label = "Tần suất",
                            value = plan.frequencyText,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Accordion Body: Workout Days
            if (isExpanded) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "LỊCH TRÌNH CHI TIẾT TRONG TUẦN",
                            style = MaterialTheme.typography.labelSmall,
                            color = IronOnSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "${plan.days.count { !it.isRest }} buổi tập",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    plan.days.forEachIndexed { idx, day ->
                        if (day.isRest) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = IronSurfaceContainer.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth(),
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
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(IronSurfaceHighest),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Bed,
                                                contentDescription = null,
                                                tint = EmeraldTertiary,
                                                modifier = Modifier.size(18.dp),
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = day.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = IronOnSurfaceVariant,
                                            )
                                            Text(
                                                text = day.subtitle,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = EmeraldTertiary,
                                            )
                                        }
                                    }
                                    Text(
                                        text = "REST",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IronOnSurfaceVariant,
                                    )
                                }
                            }
                        } else if (idx == 0) {
                            // First day highlighted with exercise breakdown + prominent CTA
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = IronSurfaceContainer,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.weight(1f),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(NeonPrimaryContainer),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Text(
                                                    text = "${day.dayNumber}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = OnNeonPrimary,
                                                    fontWeight = FontWeight.ExtraBold,
                                                )
                                            }
                                            Column {
                                                Text(
                                                    text = day.title,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = IronOnSurface,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                                Text(
                                                    text = day.subtitle,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = IronOnSurfaceVariant,
                                                )
                                            }
                                        }
                                        Surface(
                                            shape = CircleShape,
                                            color = NeonPrimary.copy(alpha = 0.14f),
                                        ) {
                                            Text(
                                                text = "Trọng tâm",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = NeonPrimary,
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = IronSurfaceLow,
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                        ) {
                                            day.exerciseNames.forEachIndexed { exIdx, exName ->
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
                                                                .size(6.dp)
                                                                .clip(CircleShape)
                                                                .background(NeonPrimary),
                                                        )
                                                        Text(
                                                            text = exName,
                                                            style = MaterialTheme.typography.labelMedium,
                                                            color = IronOnSurface,
                                                        )
                                                    }
                                                    Text(
                                                        text = if (exIdx == 0) "4 hiệp × 6-10" else "3 hiệp × 8-12",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = IronOnSurfaceVariant,
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = { onStartDay(day) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = NeonPrimaryContainer,
                                            contentColor = OnNeonPrimary,
                                        ),
                                    ) {
                                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "BẮT ĐẦU BUỔI NÀY",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                        )
                                    }
                                }
                            }
                        } else {
                            // Compact day card
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = IronSurfaceContainer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onStartDay(day) },
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
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(IronSurfaceHigh),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = "${day.dayNumber}",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = IronOnSurface,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                text = day.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = IronOnSurface,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                            Text(
                                                text = day.exerciseNames.joinToString(", "),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = IronOnSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(IronSurfaceHigh),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ChevronRight,
                                            contentDescription = "Bắt đầu",
                                            tint = NeonPrimary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineMiniStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = IronSurfaceLow,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeonPrimary,
                    modifier = Modifier.size(14.dp),
                )
                Text(label, style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
            }
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = IronOnSurface,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun ExerciseLibraryPane(
    catalog: List<Exercise>,
    progressMap: Map<Long, com.example.gymapp.ui.home.ExerciseProgressUi>,
    onOpenExerciseDetail: (Long) -> Unit,
    onOpenCreateDialog: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedMuscle by rememberSaveable { mutableStateOf<MuscleGroup?>(null) }
    var selectedEquip by rememberSaveable { mutableStateOf("ALL") }

    val equipFilters = listOf(
        "ALL" to "Tất cả thiết bị",
        "Barbell" to "Tạ đòn (Barbell)",
        "Dumbbell" to "Tạ đơn (Dumbbell)",
        "Cable" to "Dây cáp (Cable)",
        "Machine" to "Máy tạ (Machine)",
        "Bodyweight" to "Bodyweight",
    )

    val filtered = catalog.filter { ex ->
        val meta = IronLogCatalog.metaFor(ex)
        val matchMuscle = selectedMuscle == null || ex.muscle == selectedMuscle
        val matchEquip = selectedEquip == "ALL" || meta.equipment.contains(selectedEquip, ignoreCase = true)
        val matchQuery = query.isBlank() ||
            ex.name.contains(query, ignoreCase = true) ||
            meta.vietnameseName.contains(query, ignoreCase = true) ||
            ex.muscle.label.contains(query, ignoreCase = true)
        matchMuscle && matchEquip && matchQuery
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Content Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = NeonPrimary.copy(alpha = 0.12f),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
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
                                text = "KHO BÀI TẬP KHOA HỌC",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonPrimary,
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Thư viện bài tập",
                            style = MaterialTheme.typography.headlineMedium,
                            color = IronOnSurface,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = IronSurfaceHigh,
                        ) {
                            Text(
                                text = "${catalog.size}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = IronOnSurfaceVariant,
                            )
                        }
                    }
                    Text(
                        text = "Tiêu chuẩn Hypertrophy & Quá tải lũy tiến",
                        style = MaterialTheme.typography.labelSmall,
                        color = IronOnSurfaceVariant,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = IronSurfaceHigh,
                    modifier = Modifier.clickable(onClick = onOpenCreateDialog),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = NeonPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = "Tạo mới",
                            style = MaterialTheme.typography.labelMedium,
                            color = NeonPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        // Smart Search Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = IronSurfaceContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = NeonPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                        androidx.compose.foundation.text.BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = IronOnSurface),
                            modifier = Modifier.weight(1f),
                            decorationBox = { inner ->
                                if (query.isEmpty()) {
                                    Text(
                                        text = "Tìm theo tên bài (bench, ngực, kéo xô...)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = IronOnSurfaceVariant.copy(alpha = 0.6f),
                                    )
                                }
                                inner()
                            },
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = EmeraldTertiary,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = "Hỗ trợ tìm tên tiếng Việt, tiếng Anh & nhóm cơ mục tiêu",
                        style = MaterialTheme.typography.labelSmall,
                        color = IronOnSurfaceVariant,
                    )
                }
            }
        }

        // Muscle Group Horizontal Chips + Equipment Sub-Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val allSelected = selectedMuscle == null
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (allSelected) NeonPrimaryContainer else IronSurfaceContainer,
                        modifier = Modifier.clickable { selectedMuscle = null },
                    ) {
                        Text(
                            text = "Tất cả (${catalog.size})",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (allSelected) OnNeonPrimary else IronOnSurfaceVariant,
                            fontWeight = if (allSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                        )
                    }
                    MuscleGroup.entries.forEach { group ->
                        val count = catalog.count { it.muscle == group }
                        val active = selectedMuscle == group
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (active) NeonPrimaryContainer else IronSurfaceContainer,
                            modifier = Modifier.clickable {
                                selectedMuscle = if (active) null else group
                            },
                        ) {
                            Text(
                                text = "${group.label} ($count)",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (active) OnNeonPrimary else IronOnSurfaceVariant,
                                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.SemiBold,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    equipFilters.forEach { (key, label) ->
                        val active = selectedEquip == key
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (active) IronSurfaceHigh else IronSurfaceLow,
                            modifier = Modifier.clickable { selectedEquip = key },
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (active) NeonPrimary else IronOnSurfaceVariant,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }

        // Exercise Cards
        items(filtered, key = { it.id }) { ex ->
            val meta = IronLogCatalog.metaFor(ex)
            val prog = progressMap[ex.id]
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = IronSurfaceContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenExerciseDetail(ex.id) },
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(IronSurfaceHigh),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FitnessCenter,
                            contentDescription = null,
                            tint = if (meta.tier == "Tier S") NeonPrimary else EmeraldTertiary,
                            modifier = Modifier.size(26.dp),
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = ex.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = IronOnSurface,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = if (meta.tier == "Tier S") BurntSecondaryContainer else NeonPrimary.copy(alpha = 0.14f),
                            ) {
                                Text(
                                    text = meta.tier,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (meta.tier == "Tier S") OnBurntSecondaryContainer else NeonPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                )
                            }
                        }

                        Text(
                            text = meta.vietnameseName,
                            style = MaterialTheme.typography.labelSmall,
                            color = IronOnSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = NeonPrimary.copy(alpha = 0.12f),
                            ) {
                                Text(
                                    text = ex.muscle.label,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonPrimary,
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = IronSurfaceHighest,
                            ) {
                                Text(
                                    text = if (meta.isCompound) "Compound" else "Isolation",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IronOnSurfaceVariant,
                                )
                            }
                            Text(
                                text = "${ex.repMin}–${ex.repMax} reps",
                                style = MaterialTheme.typography.labelSmall,
                                color = IronOnSurfaceVariant,
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MilitaryTech,
                                contentDescription = null,
                                tint = NeonPrimary,
                                modifier = Modifier.size(14.dp),
                            )
                            if (prog?.latestE1rm != null) {
                                Text(
                                    text = "e1RM ${formatEstimate(prog.latestE1rm)} kg • Tốt nhất ${prog.bestReps} reps",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonPrimary,
                                    fontWeight = FontWeight.Bold,
                                )
                            } else {
                                Text(
                                    text = "Khởi điểm: ${formatKg(ex.startKg)} kg • Bước +${formatKg(ex.incrementKg)} kg",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IronOnSurfaceVariant,
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = IronOnSurfaceVariant,
                    )
                }
            }
        }

        // AI Coach Assistant Prompt Box
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = IronSurfaceLow,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(IronSurfaceHighest),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SmartToy,
                            contentDescription = null,
                            tint = NeonPrimary,
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "TRỢ LÝ AI IRONLOG",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Chưa tìm thấy bài tập mong muốn?",
                            style = MaterialTheme.typography.titleMedium,
                            color = IronOnSurface,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Bạn có thể tự cấu hình bài tập mới với khoảng rep mục tiêu và bước nhảy tạ riêng cho phòng gym của mình.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = IronOnSurfaceVariant,
                        )
                        Button(
                            onClick = onOpenCreateDialog,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPrimaryContainer,
                                contentColor = OnNeonPrimary,
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Tạo bài tập tùy chỉnh",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateCustomExerciseDialog(
    onDismiss: () -> Unit,
    onCreate: (String, MuscleGroup, Int, Int, Double) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var muscle by remember { mutableStateOf(MuscleGroup.CHEST) }
    var repMin by remember { mutableStateOf("8") }
    var repMax by remember { mutableStateOf("12") }
    var incKg by remember { mutableStateOf("2.5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo bài tập tùy chỉnh") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên bài tập (VD: Machine Chest Press)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text("Nhóm cơ chính:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    MuscleGroup.entries.forEach { g ->
                        val active = muscle == g
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (active) NeonPrimaryContainer else IronSurfaceHigh,
                            modifier = Modifier.clickable { muscle = g },
                        ) {
                            Text(
                                text = g.label,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (active) OnNeonPrimary else IronOnSurface,
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = repMin,
                        onValueChange = { repMin = it },
                        label = { Text("Rep sàn") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = repMax,
                        onValueChange = { repMax = it },
                        label = { Text("Rep trần") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = incKg,
                        onValueChange = { incKg = it },
                        label = { Text("Bước kg") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val min = repMin.toIntOrNull() ?: 8
                        val max = repMax.toIntOrNull() ?: 12
                        val step = incKg.replace(',', '.').toDoubleOrNull() ?: 2.5
                        onCreate(name, muscle, min, max, step)
                    }
                },
            ) {
                Text("Lưu bài tập", color = NeonPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        },
    )
}
