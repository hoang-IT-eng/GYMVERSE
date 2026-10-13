package com.example.gymapp.ui.workout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
                    "PR mới · ${event.exerciseName} · 1RM ${formatEstimate(event.e1rm)} kg",
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

    Box(Modifier.fillMaxSize()) {
        val session = state.session
        if (state.loading || session == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(Modifier.fillMaxSize().statusBarsPadding()) {
                WorkoutHeader(
                    session = session,
                    setCount = state.items.sumOf { it.sets.size },
                    onBack = {
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
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (state.items.isEmpty()) {
                        item {
                            Column(
                                Modifier.fillMaxWidth().padding(vertical = 28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text("Thêm bài để bắt đầu.", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Sau đó mỗi set chỉ cần một chạm.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Button(
                                    onClick = { picking = true },
                                    shape = RoundedCornerShape(14.dp),
                                ) { Text("Thêm bài tập") }
                            }
                        }
                    } else {
                        items(state.items, key = { it.exercise.id }) { item ->
                            val draft = drafts[item.exercise.id]
                                ?: Draft(item.suggestion.weightKg, item.suggestion.targetReps)
                            ExerciseCard(
                                item = item,
                                draft = draft,
                                onNudgeWeight = { viewModel.nudgeWeight(item.exercise.id, it) },
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
                        item {
                            TextButton(onClick = { picking = true }, modifier = Modifier.fillMaxWidth()) {
                                Text("Thêm bài tập")
                            }
                        }
                    }
                }
                SnackbarHost(snackbar)
                RestTimerBar(
                    restSeconds = restSeconds,
                    onSelectRest = viewModel::setRestSeconds,
                )
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
            body = "Các set đã ghi sẽ dùng để gợi ý tạ và rep cho buổi sau.",
            confirm = "Kết thúc",
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
            body = "Toàn bộ set trong buổi sẽ mất.",
            confirm = "Xóa",
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
            body = "Các set của bài này trong buổi cũng bị xóa.",
            confirm = "Xóa",
            destructive = true,
            onConfirm = {
                viewModel.removeExercise(current.exercise.id)
                dialog = null
            },
            onDismiss = { dialog = null },
        )
        is WorkoutDialog.EditWeight -> NumberDialog(
            title = "Tạ (kg)",
            initial = current.initial,
            integer = false,
            onDismiss = { dialog = null },
            onConfirm = { value ->
                viewModel.setWeight(current.exerciseId, value)
                dialog = null
            },
        )
        is WorkoutDialog.EditReps -> NumberDialog(
            title = "Số rep",
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
private fun WorkoutHeader(
    session: Session,
    setCount: Int,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    onDelete: () -> Unit,
) {
    val finished = session.finishedAt != null
    val elapsed = rememberSessionElapsed(session.startedAt, session.finishedAt)
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
        }
        Column(Modifier.weight(1f)) {
            Text(if (finished) "Buổi đã lưu" else "Đang tập", style = MaterialTheme.typography.titleLarge)
            Text(
                "${formatElapsed(elapsed)} · $setCount set",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        TextButton(onClick = { if (finished) onDelete() else onFinish() }) {
            Text(
                if (finished) "Xóa" else "Kết thúc",
                color = if (finished) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
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
                Text(confirm, color = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
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
            ) { Text("Lưu") }
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
        title = { Text(if (addedLoad) "Sửa set · tạ thêm" else "Sửa set") },
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
                    label = { Text("Rep") },
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
            ) { Text("Lưu") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDelete) {
                    Text("Xóa set", color = MaterialTheme.colorScheme.error)
                }
                TextButton(onClick = onDismiss) { Text("Hủy") }
            }
        },
    )
}
