package com.example.gymapp.ui.workout

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp.domain.Exercise
import com.example.gymapp.domain.Session
import com.example.gymapp.domain.SetEntry
import com.example.gymapp.domain.formatElapsed
import com.example.gymapp.domain.formatEstimate
import com.example.gymapp.domain.formatKg
import com.example.gymapp.ui.appViewModelFactory
import com.example.gymapp.ui.components.RestTimerBar
import com.example.gymapp.ui.theme.BurntSecondary
import com.example.gymapp.ui.theme.EmeraldTertiary
import com.example.gymapp.ui.theme.IronOnSurface
import com.example.gymapp.ui.theme.IronOnSurfaceVariant
import com.example.gymapp.ui.theme.IronSurface
import com.example.gymapp.ui.theme.IronSurfaceContainer
import com.example.gymapp.ui.theme.IronSurfaceHigh
import com.example.gymapp.ui.theme.IronSurfaceLowest
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.NeonPrimaryContainer
import com.example.gymapp.ui.theme.OnNeonPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

private sealed interface WorkoutDialog {
    data object Finish : WorkoutDialog
    data object DeleteSession : WorkoutDialog
    data class RemoveExercise(val exercise: Exercise) : WorkoutDialog
    data class EditWeight(val exerciseId: Long, val initial: String) : WorkoutDialog
    data class EditReps(val exerciseId: Long, val initial: String) : WorkoutDialog
    data class EditSet(val set: SetEntry, val addedLoad: Boolean) : WorkoutDialog
}

@Composable
fun WorkoutScreen(
    sessionId: Long,
    onBack: () -> Unit,
    onOpenProgress: (Long) -> Unit,
) {
    val viewModel: WorkoutViewModel = viewModel(
        key = "workout-$sessionId",
        factory = appViewModelFactory { app -> WorkoutViewModel(app, sessionId) },
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val drafts by viewModel.drafts.collectAsStateWithLifecycle()
    val restSeconds by viewModel.restSeconds.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var picking by remember { mutableStateOf(false) }
    var dialog by remember { mutableStateOf<WorkoutDialog?>(null) }
    var left by remember { mutableStateOf(false) }
    val leave = {
        if (!left) {
            left = true
            onBack()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is WorkoutEvent.PersonalRecord -> snackbar.showSnackbar(
                    "🔥 PR mới • ${event.exerciseName} • e1RM ${formatEstimate(event.e1rm)} kg",
                )
            }
        }
    }
    LaunchedEffect(state.loading, state.session) {
        if (!state.loading && state.session == null) leave()
    }
    BackHandler(enabled = picking) { picking = false }
    BackHandler(enabled = !picking && dialog == null) {
        scope.launch {
            viewModel.discardIfEmpty()
            leave()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IronSurface),
    ) {
        val session = state.session
        if (state.loading || session == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonPrimary)
            }
        } else {
            val totalSets = state.items.sumOf { it.sets.size }
            val targetSets = (state.items.size * 3).coerceAtLeast(totalSets.coerceAtLeast(1))
            val totalVolumeKg = state.items.sumOf { item ->
                item.sets.sumOf { it.weightKg * it.reps }
            }
            val musclesSummary = state.items
                .map { it.exercise.muscle.label }
                .distinct()
                .joinToString(", ")
                .ifEmpty { "THÊM BÀI TẬP ĐỂ BẮT ĐẦU" }

            Column(Modifier.fillMaxSize().statusBarsPadding()) {
                // Stitch Live Workout Header
                LiveWorkoutTopBar(
                    session = session,
                    onClose = {
                        scope.launch {
                            viewModel.discardIfEmpty()
                            leave()
                        }
                    },
                    onFinish = { dialog = WorkoutDialog.Finish },
                    onDelete = { dialog = WorkoutDialog.DeleteSession },
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    // 1. SESSION OVERVIEW PANEL
                    item {
                        SessionOverviewPanel(
                            session = session,
                            musclesSummary = musclesSummary,
                            totalVolumeKg = totalVolumeKg,
                            loggedSets = totalSets,
                            targetSets = targetSets,
                        )
                    }

                    // 2. STICKY REST TIMER BANNER
                    item {
                        RestTimerBar(
                            restSeconds = restSeconds,
                            onSelectRest = viewModel::setRestSeconds,
                            sessionId = session.id,
                        )
                    }

                    // 3. EXERCISE BLOCKS
                    if (state.items.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = IronSurfaceContainer,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Text(
                                        text = "Chưa có bài tập trong buổi này",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = IronOnSurface,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "Chọn bài từ kho Hypertrophy để hệ thống tự điền mức tạ và số rep gợi ý.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = IronOnSurfaceVariant,
                                    )
                                    Button(
                                        onClick = { picking = true },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = NeonPrimaryContainer,
                                            contentColor = OnNeonPrimary,
                                        ),
                                    ) {
                                        Icon(imageVector = Icons.Filled.AddCircle, contentDescription = null)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Thêm bài tập ngay", fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    } else {
                        itemsIndexed(state.items, key = { _, it -> it.exercise.id }) { idx, item ->
                            val draft = drafts[item.exercise.id]
                                ?: Draft(item.suggestion.weightKg, item.suggestion.targetReps)
                            ExerciseCard(
                                orderNumber = idx + 1,
                                item = item,
                                draft = draft,
                                onNudgeWeight = { viewModel.nudgeWeight(item.exercise.id, it) },
                                onDeltaWeight = { viewModel.addDeltaWeight(item.exercise.id, it) },
                                onNudgeReps = { viewModel.nudgeReps(item.exercise.id, it) },
                                onEditWeight = {
                                    dialog = WorkoutDialog.EditWeight(
                                        item.exercise.id,
                                        formatKg(draft.weightKg),
                                    )
                                },
                                onEditReps = {
                                    dialog = WorkoutDialog.EditReps(item.exercise.id, draft.reps.toString())
                                },
                                onLog = { viewModel.logSet(item.exercise.id) },
                                onEditSet = { set ->
                                    dialog = WorkoutDialog.EditSet(set, item.exercise.addedLoad)
                                },
                                onRemove = { dialog = WorkoutDialog.RemoveExercise(item.exercise) },
                                onOpenProgress = { onOpenProgress(item.exercise.id) },
                            )
                        }
                    }
                }

                SnackbarHost(snackbar)

                // 4. FIXED FLOATING WORKOUT HUD / BOTTOM ACTION DOCK
                Surface(
                    color = IronSurface.copy(alpha = 0.96f),
                    shadowElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            DockAuxButton(
                                icon = Icons.Filled.AddCircle,
                                iconTint = NeonPrimary,
                                label = "Thêm bài",
                                onClick = { picking = true },
                                modifier = Modifier.weight(1f),
                            )
                            DockAuxButton(
                                icon = Icons.Filled.SwapHoriz,
                                iconTint = EmeraldTertiary,
                                label = "Đổi / Chọn bài",
                                onClick = { picking = true },
                                modifier = Modifier.weight(1f),
                            )
                            DockAuxButton(
                                icon = Icons.Filled.DeleteOutline,
                                iconTint = BurntSecondary,
                                label = "Hủy buổi",
                                onClick = { dialog = WorkoutDialog.DeleteSession },
                                modifier = Modifier.weight(1f),
                            )
                        }

                        if (session.finishedAt == null) {
                            Button(
                                onClick = { dialog = WorkoutDialog.Finish },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonPrimaryContainer,
                                    contentColor = OnNeonPrimary,
                                ),
                            ) {
                                Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "KẾT THÚC BUỔI TẬP",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                )
                            }
                        }
                    }
                }
            }
        }

        if (picking) {
            ExercisePicker(
                catalog = state.catalog,
                alreadyAdded = state.items.map { it.exercise.id }.toSet(),
                onAdd = viewModel::addExercise,
                onClose = { picking = false },
            )
        }
    }

    when (val current = dialog) {
        null -> Unit
        WorkoutDialog.Finish -> ConfirmDialog(
            title = "Kết thúc buổi tập?",
            body = "Các hiệp đã ghi sẽ được lưu vào lịch sử và tính toán mục tiêu Progressive Overload cho buổi tới.",
            confirm = "Hoàn tất",
            onConfirm = {
                dialog = null
                scope.launch {
                    viewModel.finishAndWait()
                    leave()
                }
            },
            onDismiss = { dialog = null },
        )
        WorkoutDialog.DeleteSession -> ConfirmDialog(
            title = "Xóa buổi tập này?",
            body = "Toàn bộ hiệp tập trong buổi này sẽ bị xóa.",
            confirm = "Xóa buổi",
            destructive = true,
            onConfirm = {
                dialog = null
                scope.launch {
                    viewModel.deleteSessionAndWait()
                    leave()
                }
            },
            onDismiss = { dialog = null },
        )
        is WorkoutDialog.RemoveExercise -> ConfirmDialog(
            title = "Xóa ${current.exercise.name}?",
            body = "Các hiệp đã ghi của bài này trong buổi hiện tại cũng sẽ bị xóa.",
            confirm = "Xóa",
            destructive = true,
            onConfirm = {
                viewModel.removeExercise(current.exercise.id)
                dialog = null
            },
            onDismiss = { dialog = null },
        )
        is WorkoutDialog.EditWeight -> NumberDialog(
            title = "Nhập mức tạ (kg)",
            initial = current.initial,
            integer = false,
            onDismiss = { dialog = null },
            onConfirm = { value ->
                viewModel.setWeight(current.exerciseId, value)
                dialog = null
            },
        )
        is WorkoutDialog.EditReps -> NumberDialog(
            title = "Nhập số rep",
            initial = current.initial,
            integer = true,
            onDismiss = { dialog = null },
            onConfirm = { value ->
                viewModel.setReps(current.exerciseId, value.roundToInt())
                dialog = null
            },
        )
        is WorkoutDialog.EditSet -> EditSetDialog(
            set = current.set,
            addedLoad = current.addedLoad,
            onDismiss = { dialog = null },
            onSave = { weight, reps ->
                viewModel.updateSet(current.set.id, weight, reps)
                dialog = null
            },
            onDelete = {
                viewModel.deleteSet(current.set.id)
                dialog = null
            },
        )
    }
}

@Composable
private fun LiveWorkoutTopBar(
    session: Session,
    onClose: () -> Unit,
    onFinish: () -> Unit,
    onDelete: () -> Unit,
) {
    val finished = session.finishedAt != null
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f),
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Đóng",
                    tint = IronOnSurface,
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = if (finished) "BUỔI TẬP ĐÃ LƯU" else "BUỔI TẬP ĐANG DIỄN RA",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonPrimaryContainer,
                    maxLines = 1,
                )
                Text(
                    text = "Live Workout Session",
                    style = MaterialTheme.typography.titleLarge,
                    color = IronOnSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (!finished) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NeonPrimaryContainer,
                    modifier = Modifier.clickable(onClick = onFinish),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = OnNeonPrimary,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "Xong",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnNeonPrimary,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = IronSurfaceHigh,
                    modifier = Modifier.clickable(onClick = onDelete),
                ) {
                    Text(
                        text = "Xóa buổi",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(NeonPrimary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = OnNeonPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun SessionOverviewPanel(
    session: Session,
    musclesSummary: String,
    totalVolumeKg: Double,
    loggedSets: Int,
    targetSets: Int,
) {
    val elapsed = rememberSessionElapsed(session.startedAt, session.finishedAt)
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = IronSurfaceContainer,
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(NeonPrimary),
                    )
                    Text(
                        text = if (session.finishedAt == null) "ĐANG GHI BUỔI TẬP" else "ĐÃ HOÀN THÀNH",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = IronSurfaceHigh,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = NeonPrimary,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = formatElapsed(elapsed),
                            style = MaterialTheme.typography.labelMedium,
                            color = IronOnSurface,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Column {
                Text(
                    text = "BUỔI TẬP: ${musclesSummary.uppercase()}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = IronOnSurface,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "Khối lượng tích lũy theo thời gian thực",
                    style = MaterialTheme.typography.labelMedium,
                    color = IronOnSurfaceVariant,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = IronSurfaceLowest,
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FitnessCenter,
                            contentDescription = null,
                            tint = NeonPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                        Column {
                            Text("TỔNG VOLUME", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                            Text(
                                text = "${String.format(Locale.US, "%,d", totalVolumeKg.toLong())} kg",
                                style = MaterialTheme.typography.titleMedium,
                                color = NeonPrimary,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = IronSurfaceLowest,
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = BurntSecondary,
                            modifier = Modifier.size(20.dp),
                        )
                        Column {
                            Text("TIẾN ĐỘ HIỆP", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                            Text(
                                text = "$loggedSets / $targetSets hiệp",
                                style = MaterialTheme.typography.titleMedium,
                                color = IronOnSurface,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DockAuxButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = IronSurfaceHigh,
        modifier = modifier
            .height(42.dp)
            .clickable(onClick = onClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = IronOnSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun rememberSessionElapsed(startedAt: Long, finishedAt: Long?): Long {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(startedAt, finishedAt) {
        if (finishedAt != null) return@LaunchedEffect
        while (true) {
            now = System.currentTimeMillis()
            delay(1_000)
        }
    }
    return ((finishedAt ?: now) - startedAt).coerceAtLeast(0L)
}

@Composable
private fun ConfirmDialog(
    title: String,
    body: String,
    confirm: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    destructive: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirm,
                    color = if (destructive) MaterialTheme.colorScheme.error else NeonPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } },
    )
}

@Composable
private fun NumberDialog(
    title: String,
    initial: String,
    integer: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit,
) {
    var text by remember(initial) { mutableStateOf(initial) }
    val focus = remember { FocusRequester() }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (integer) KeyboardType.Number else KeyboardType.Decimal,
                ),
                modifier = Modifier.focusRequester(focus).fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsed = text.trim().replace(',', '.').toDoubleOrNull()
                    if (parsed != null && parsed >= 0.0) onConfirm(parsed)
                },
            ) { Text("Lưu", color = NeonPrimary, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } },
    )
    LaunchedEffect(Unit) { focus.requestFocus() }
}

@Composable
private fun EditSetDialog(
    set: SetEntry,
    addedLoad: Boolean,
    onDismiss: () -> Unit,
    onSave: (Double, Int) -> Unit,
    onDelete: () -> Unit,
) {
    var weight by remember(set.id) { mutableStateOf(formatKg(set.weightKg)) }
    var reps by remember(set.id) { mutableStateOf(set.reps.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (addedLoad) "Sửa hiệp · tạ thêm" else "Sửa hiệp tập") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Tạ (kg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Số rep") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedWeight = weight.trim().replace(',', '.').toDoubleOrNull()
                    val parsedReps = reps.trim().toIntOrNull()
                    if (parsedWeight != null && parsedWeight >= 0.0 && parsedReps != null && parsedReps > 0) {
                        onSave(parsedWeight, parsedReps)
                    }
                },
            ) { Text("Lưu", color = NeonPrimary, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDelete) {
                    Text("Xóa hiệp", color = MaterialTheme.colorScheme.error)
                }
                TextButton(onClick = onDismiss) { Text("Hủy") }
            }
        },
    )
}
