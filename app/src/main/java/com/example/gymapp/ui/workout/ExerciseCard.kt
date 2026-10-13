package com.example.gymapp.ui.workout

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import com.example.gymapp.domain.SetEntry
import com.example.gymapp.domain.formatLoad
import com.example.gymapp.ui.components.StepperButton

@Composable
fun ExerciseCard(
    item: WorkoutExerciseItem,
    draft: Draft,
    onNudgeWeight: (Int) -> Unit,
    onNudgeReps: (Int) -> Unit,
    onEditWeight: () -> Unit,
    onEditReps: () -> Unit,
    onLog: () -> Unit,
    onEditSet: (SetEntry) -> Unit,
    onRemove: () -> Unit,
    onOpenProgress: () -> Unit,
) {
    val exercise = item.exercise
    val haptic = LocalHapticFeedback.current
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    TextButton(onClick = onOpenProgress, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                        Text(exercise.name, style = MaterialTheme.typography.titleLarge, color = LocalContentColor.current)
                    }
                    Text(
                        "${exercise.muscle.label} · ${exercise.repMin}–${exercise.repMax} rep",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Filled.Delete, contentDescription = "Xóa bài")
                }
            }
            item.lastSummary?.let { summary ->
                Text(
                    "Lần trước: $summary",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(
                        "${formatLoad(item.suggestion.weightKg, exercise.addedLoad)} × ${item.suggestion.targetReps}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        item.suggestion.reason,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (exercise.addedLoad) "Tạ thêm" else "Tạ",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = onEditWeight) {
                        Text(
                            formatLoad(draft.weightKg, exercise.addedLoad),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StepperButton("−", "Giảm tạ") { onNudgeWeight(-1) }
                        StepperButton("+", "Tăng tạ") { onNudgeWeight(1) }
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Rep",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = onEditReps) {
                        Text("${draft.reps}", style = MaterialTheme.typography.titleMedium)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StepperButton("−", "Giảm rep") { onNudgeReps(-1) }
                        StepperButton("+", "Tăng rep") { onNudgeReps(1) }
                    }
                }
            }
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLog()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    "Ghi ${formatLoad(draft.weightKg, exercise.addedLoad)} × ${draft.reps}",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (item.sets.isNotEmpty()) {
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item.sets.forEachIndexed { index, set ->
                        val isRecord = set.id == item.recordSetId
                        Surface(
                            onClick = { onEditSet(set) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isRecord) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                        ) {
                            Text(
                                buildString {
                                    append("${index + 1}  ")
                                    append(formatLoad(set.weightKg, exercise.addedLoad))
                                    append(" × ")
                                    append(set.reps)
                                    if (isRecord) append("  PR")
                                },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}
