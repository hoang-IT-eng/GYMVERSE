package com.example.gymapp.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.gymapp.domain.IronLogCatalog
import com.example.gymapp.domain.SetEntry
import com.example.gymapp.domain.formatKg
import com.example.gymapp.domain.formatLoad
import com.example.gymapp.ui.theme.BurntSecondary
import com.example.gymapp.ui.theme.EmeraldTertiary
import com.example.gymapp.ui.theme.IronOnSurface
import com.example.gymapp.ui.theme.IronOnSurfaceVariant
import com.example.gymapp.ui.theme.IronSurfaceContainer
import com.example.gymapp.ui.theme.IronSurfaceHigh
import com.example.gymapp.ui.theme.IronSurfaceHighest
import com.example.gymapp.ui.theme.IronSurfaceLow
import com.example.gymapp.ui.theme.IronSurfaceLowest
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.NeonPrimaryContainer
import com.example.gymapp.ui.theme.OnNeonPrimary

@Composable
fun ExerciseCard(
    orderNumber: Int,
    item: WorkoutExerciseItem,
    draft: Draft,
    onNudgeWeight: (Int) -> Unit,
    onDeltaWeight: (Double) -> Unit,
    onNudgeReps: (Int) -> Unit,
    onEditWeight: () -> Unit,
    onEditReps: () -> Unit,
    onLog: () -> Unit,
    onEditSet: (SetEntry) -> Unit,
    onRemove: () -> Unit,
    onOpenProgress: () -> Unit,
) {
    val exercise = item.exercise
    val meta = remember(exercise) { IronLogCatalog.metaFor(exercise) }
    val haptic = LocalHapticFeedback.current
    var selectedRir by rememberSaveable(exercise.id) { mutableIntStateOf(2) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = IronSurfaceContainer,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Exercise Header & Meta
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onOpenProgress),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonPrimaryContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "$orderNumber",
                                style = MaterialTheme.typography.labelLarge,
                                color = OnNeonPrimary,
                                fontWeight = FontWeight.Black,
                            )
                        }
                        Text(
                            text = exercise.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = IronOnSurface,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = onOpenProgress,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(IronSurfaceHigh),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timeline,
                                contentDescription = "Chi tiết Overload",
                                tint = NeonPrimary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(IronSurfaceHigh),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Xóa bài",
                                tint = IronOnSurfaceVariant,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                Text(
                    text = meta.vietnameseName,
                    style = MaterialTheme.typography.labelMedium,
                    color = IronOnSurfaceVariant,
                )

                // Muscle Chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NeonPrimary.copy(alpha = 0.12f),
                    ) {
                        Text(
                            text = "${exercise.muscle.label.uppercase()} (CHÍNH)",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    exercise.secondaryMuscles.forEach { sec ->
                        Surface(
                            shape = CircleShape,
                            color = IronSurfaceHighest,
                        ) {
                            Text(
                                text = sec.label,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = IronOnSurfaceVariant,
                            )
                        }
                    }
                }
            }

            // Progressive Overload Prompt Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = IronSurfaceLowest,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonPrimary.copy(alpha = 0.22f), RoundedCornerShape(12.dp)),
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = BurntSecondary,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = "GỢI Ý PROGRESSIVE OVERLOAD",
                                style = MaterialTheme.typography.labelSmall,
                                color = BurntSecondary,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = NeonPrimaryContainer,
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowUpward,
                                    contentDescription = null,
                                    tint = OnNeonPrimary,
                                    modifier = Modifier.size(12.dp),
                                )
                                Text(
                                    text = if (item.suggestion.reason.contains("tăng")) "TĂNG TẠ" else "DOUBLE PROG",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnNeonPrimary,
                                    fontWeight = FontWeight.Black,
                                )
                            }
                        }
                    }

                    item.lastSummary?.let { summary ->
                        Text(
                            text = buildAnnotatedString {
                                append("Buổi trước: ")
                                withStyle(SpanStyle(color = IronOnSurface, fontWeight = FontWeight.SemiBold)) {
                                    append(summary)
                                }
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = IronOnSurfaceVariant,
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NeonPrimary.copy(alpha = 0.10f),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = NeonPrimary,
                                modifier = Modifier.size(18.dp),
                            )
                            Column {
                                Text(
                                    text = buildAnnotatedString {
                                        append("Mục tiêu hôm nay: ")
                                        withStyle(SpanStyle(color = NeonPrimary, fontWeight = FontWeight.Black)) {
                                            append("${formatLoad(item.suggestion.weightKg, exercise.addedLoad)} × ${item.suggestion.targetReps} reps @ RIR 2")
                                        }
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = IronOnSurface,
                                )
                                Text(
                                    text = item.suggestion.reason,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IronOnSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }

            // Set Logger Table
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Table Headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("SET", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant, modifier = Modifier.width(34.dp))
                    Text("MỤC TIÊU", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant, modifier = Modifier.weight(1.2f))
                    Text("KG", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                    Text("REPS", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                    Text("RIR", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.width(44.dp))
                    Text("XONG", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant, textAlign = TextAlign.End, modifier = Modifier.width(48.dp))
                }

                // Completed Sets Rows
                item.sets.forEachIndexed { idx, set ->
                    val isPr = set.id == item.recordSetId
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isPr) NeonPrimaryContainer.copy(alpha = 0.16f) else IronSurfaceHigh.copy(alpha = 0.55f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditSet(set) },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .padding(horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isPr) NeonPrimaryContainer else IronSurfaceHighest),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = if (isPr) "PR" else "${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isPr) OnNeonPrimary else IronOnSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${formatKg(item.suggestion.weightKg)}kg × ${item.suggestion.targetReps}",
                                style = MaterialTheme.typography.labelMedium,
                                color = IronOnSurfaceVariant,
                                modifier = Modifier.weight(1.2f),
                                maxLines = 1,
                            )
                            Text(
                                text = formatKg(set.weightKg),
                                style = MaterialTheme.typography.titleMedium,
                                color = IronOnSurface,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = "${set.reps}",
                                style = MaterialTheme.typography.titleMedium,
                                color = IronOnSurface,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = "$selectedRir",
                                style = MaterialTheme.typography.labelMedium,
                                color = NeonPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(44.dp),
                            )
                            Box(
                                modifier = Modifier.width(48.dp),
                                contentAlignment = Alignment.CenterEnd,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NeonPrimary.copy(alpha = 0.22f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Đã xong",
                                        tint = NeonPrimary,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                // Active Working Set Row (with RIR selector & 1-tap Done button)
                val nextSetNum = item.sets.size + 1
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = IronSurfaceHighest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, NeonPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonPrimaryContainer),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "$nextSetNum",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = OnNeonPrimary,
                                    fontWeight = FontWeight.Black,
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text(
                                    text = "${formatKg(item.suggestion.weightKg)}kg × ${item.suggestion.targetReps}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = IronOnSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                )
                                Text(
                                    text = "@ RIR $selectedRir",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BurntSecondary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            // Editable Weight Cell
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 3.dp)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(IronSurfaceLowest)
                                    .clickable(onClick = onEditWeight),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = formatKg(draft.weightKg),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = NeonPrimary,
                                    fontWeight = FontWeight.Black,
                                )
                            }

                            // Editable Reps Cell
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 3.dp)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(IronSurfaceLowest)
                                    .clickable(onClick = onEditReps),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "${draft.reps}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = NeonPrimary,
                                    fontWeight = FontWeight.Black,
                                )
                            }

                            // Active RIR Display Chip
                            Box(
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonPrimary.copy(alpha = 0.18f))
                                    .border(1.dp, NeonPrimaryContainer, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$selectedRir",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = NeonPrimary,
                                        fontWeight = FontWeight.Black,
                                    )
                                    Text(
                                        text = "RIR",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonPrimary,
                                    )
                                }
                            }

                            // 1-Tap Action Check Target
                            Box(
                                modifier = Modifier.width(48.dp),
                                contentAlignment = Alignment.CenterEnd,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NeonPrimaryContainer)
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onLog()
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = "Ghi set",
                                        tint = OnNeonPrimary,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            }
                        }

                        // Quick 1-Tap RIR Selector Strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(IronSurfaceLowest)
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(
                                    text = "CHỌN RIR THỰC TẾ:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IronOnSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = NeonPrimary.copy(alpha = 0.12f),
                                ) {
                                    Text(
                                        text = "Gợi ý: 2",
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonPrimary,
                                    )
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                listOf(0, 1, 2, 3, 4).forEach { rir ->
                                    val active = selectedRir == rir
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (active) NeonPrimaryContainer else IronSurfaceHigh)
                                            .clickable { selectedRir = rir },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = if (rir == 4) "4+" else "$rir",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (active) OnNeonPrimary else IronOnSurface,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Weight & Rep Adjuster Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "CHỈNH NHANH:",
                        style = MaterialTheme.typography.labelSmall,
                        color = IronOnSurfaceVariant,
                    )
                    QuickPill("-2.5kg") { onDeltaWeight(-2.5) }
                    QuickPill("+1.25kg") { onDeltaWeight(1.25) }
                    QuickPill("+2.5kg") { onDeltaWeight(2.5) }
                    QuickPill("+5kg") { onDeltaWeight(5.0) }
                    QuickPill("−1 rep") { onNudgeReps(-1) }
                    QuickPill("+1 rep") { onNudgeReps(1) }
                }
            }

            // Exercise Micro Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonPrimaryContainer)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLog()
                        }
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = OnNeonPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Ghi hiệp (${formatLoad(draft.weightKg, exercise.addedLoad)} × ${draft.reps})",
                        style = MaterialTheme.typography.labelMedium,
                        color = OnNeonPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(0.9f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(IronSurfaceHigh)
                        .clickable(onClick = onOpenProgress)
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = EmeraldTertiary,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Biểu đồ & PR",
                        style = MaterialTheme.typography.labelMedium,
                        color = IronOnSurface,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickPill(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = IronSurfaceHigh,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = IronOnSurface,
            fontWeight = FontWeight.Bold,
        )
    }
}
