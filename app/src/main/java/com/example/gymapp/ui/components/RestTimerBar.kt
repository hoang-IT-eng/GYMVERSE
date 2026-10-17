package com.example.gymapp.ui.components

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymapp.domain.formatElapsed
import com.example.gymapp.service.RestPhase
import com.example.gymapp.service.RestTimerBus
import com.example.gymapp.service.RestTimerController
import com.example.gymapp.ui.theme.BurntSecondary
import com.example.gymapp.ui.theme.BurntSecondaryContainer
import com.example.gymapp.ui.theme.IronOnSurface
import com.example.gymapp.ui.theme.IronOnSurfaceVariant
import com.example.gymapp.ui.theme.IronSurfaceContainer
import com.example.gymapp.ui.theme.IronSurfaceHigh
import com.example.gymapp.ui.theme.IronSurfaceHighest
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.NeonPrimaryContainer
import com.example.gymapp.ui.theme.OnBurntSecondaryContainer
import com.example.gymapp.ui.theme.OnNeonPrimary
import kotlinx.coroutines.delay

private val RestChoices = listOf(60, 90, 120, 180)

@Composable
fun rememberTickingNow(active: Boolean): Long {
    var now by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }
    LaunchedEffect(active) {
        if (!active) return@LaunchedEffect
        while (true) {
            now = SystemClock.elapsedRealtime()
            delay(200)
        }
    }
    return now
}

/**
 * Stitch Live Workout Sticky Rest Timer Banner:
 * Circular progress ring + Hourglass icon + NGHỈ GIỮA HIỆP + MM:SS + [-15s] [+30s] [Bỏ qua]
 * Or preset selector chips (60s/90s/120s/180s) when idle.
 */
@Composable
fun RestTimerBar(
    restSeconds: Int,
    onSelectRest: (Int) -> Unit,
    sessionId: Long = -1L,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val phase by RestTimerBus.phase.collectAsStateWithLifecycle()
    val running = phase as? RestPhase.Running
    val now = rememberTickingNow(running != null)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = IronSurfaceHigh,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val remaining = running?.let { (it.snapshot.endElapsedRealtime - now).coerceAtLeast(0L) } ?: 0L
            val fraction = if (running != null && running.snapshot.durationMs > 0) {
                (remaining.toFloat() / running.snapshot.durationMs.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier.size(46.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            progress = { if (running != null) fraction else 1f },
                            modifier = Modifier.size(46.dp),
                            color = if (running != null) BurntSecondaryContainer else IronSurfaceHighest,
                            trackColor = IronSurfaceHighest,
                            strokeWidth = 4.dp,
                        )
                        Icon(
                            imageVector = Icons.Filled.HourglassTop,
                            contentDescription = null,
                            tint = BurntSecondary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Column {
                        Text(
                            text = when (phase) {
                                is RestPhase.Finished -> "HẾT GIỜ NGHỈ — VÀO HIỆP TIẾP"
                                is RestPhase.Running -> "NGHỈ GIỮA HIỆP (CHẠY CẢ KHI TẮT MÀN)"
                                RestPhase.Idle -> "NGHỈ GIỮA HIỆP (${restSeconds}S)"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = BurntSecondary,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = if (running != null) {
                                formatElapsed(remaining)
                            } else {
                                formatElapsed(restSeconds * 1000L)
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            color = IronOnSurface,
                            fontWeight = FontWeight.Black,
                        )
                    }
                }

                if (running != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(IronSurfaceContainer)
                                .clickable { RestTimerController.subtractFifteen(context) }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("-15s", style = MaterialTheme.typography.labelMedium, color = IronOnSurface, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(IronSurfaceContainer)
                                .clickable { RestTimerController.addThirty(context) }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("+30s", style = MaterialTheme.typography.labelMedium, color = NeonPrimary, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BurntSecondaryContainer)
                                .clickable { RestTimerController.skip(context) }
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SkipNext,
                                contentDescription = "Bỏ qua",
                                tint = OnBurntSecondaryContainer,
                                modifier = Modifier.size(16.dp),
                            )
                            Text("Bỏ qua", style = MaterialTheme.typography.labelMedium, color = OnBurntSecondaryContainer, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonPrimaryContainer)
                            .clickable { RestTimerController.start(context, restSeconds, sessionId) }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Bấm giờ ${restSeconds}s",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnNeonPrimary,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }

            if (running == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    RestChoices.forEach { sec ->
                        val active = sec == restSeconds
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) NeonPrimaryContainer.copy(alpha = 0.22f) else IronSurfaceContainer)
                                .clickable { onSelectRest(sec) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "${sec}s",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (active) NeonPrimary else IronOnSurfaceVariant,
                                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveRestBanner(onOpenSession: (Long) -> Unit) {
    val context = LocalContext.current
    val phase by RestTimerBus.phase.collectAsStateWithLifecycle()
    val running = phase as? RestPhase.Running
    val finished = phase is RestPhase.Finished
    if (running == null && !finished) return
    val now = rememberTickingNow(running != null)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable {
                val sessionId = running?.snapshot?.sessionId ?: return@clickable
                if (sessionId >= 0) onOpenSession(sessionId)
            },
        color = IronSurfaceHigh,
        shape = RoundedCornerShape(14.dp),
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Filled.HourglassTop,
                    contentDescription = null,
                    tint = BurntSecondary,
                    modifier = Modifier.size(20.dp),
                )
                Column {
                    if (finished || running == null) {
                        Text("HẾT GIỜ NGHỈ", style = MaterialTheme.typography.labelSmall, color = BurntSecondary, fontWeight = FontWeight.Bold)
                        Text("Chạm để quay lại buổi tập", style = MaterialTheme.typography.bodyMedium, color = IronOnSurface)
                    } else {
                        val remaining = (running.snapshot.endElapsedRealtime - now).coerceAtLeast(0L)
                        Text(
                            "ĐANG NGHỈ GIỮA HIỆP • ${formatElapsed(remaining)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = BurntSecondary,
                        )
                        Text("Chạm để mở buổi tập đang diễn ra", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                    }
                }
            }
            if (running != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(IronSurfaceContainer)
                            .clickable { RestTimerController.addThirty(context) }
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("+30s", style = MaterialTheme.typography.labelSmall, color = NeonPrimary)
                    }
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BurntSecondaryContainer)
                            .clickable { RestTimerController.skip(context) }
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Bỏ qua", style = MaterialTheme.typography.labelSmall, color = OnBurntSecondaryContainer)
                    }
                }
            }
        }
    }
}
