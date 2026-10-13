package com.example.gymapp.ui.components

import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymapp.domain.formatElapsed
import com.example.gymapp.service.RestPhase
import com.example.gymapp.service.RestTimerBus
import com.example.gymapp.service.RestTimerController
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

@Composable
fun RestTimerBar(
    restSeconds: Int,
    onSelectRest: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val phase by RestTimerBus.phase.collectAsStateWithLifecycle()
    val running = phase as? RestPhase.Running
    val now = rememberTickingNow(running != null)
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.surfaceContainer) {
        Column(
            Modifier
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            when {
                phase is RestPhase.Finished -> {
                    Text(
                        "Hết giờ nghỉ",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("Vào set tiếp theo", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                running != null -> {
                    val remaining = (running.snapshot.endElapsedRealtime - now).coerceAtLeast(0L)
                    val fraction = (remaining.toFloat() / running.snapshot.durationMs.toFloat()).coerceIn(0f, 1f)
                    Text(
                        formatElapsed(remaining),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { RestTimerController.addThirty(context) },
                            modifier = Modifier.weight(1f),
                        ) { Text("+30 giây") }
                        Button(
                            onClick = { RestTimerController.skip(context) },
                            modifier = Modifier.weight(1f),
                        ) { Text("Bỏ qua") }
                    }
                }
                else -> {
                    Text("Nghỉ giữa set", style = MaterialTheme.typography.titleMedium)
                    Row(
                        Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RestChoices.forEach { seconds ->
                            FilterChip(
                                selected = restSeconds == seconds,
                                onClick = { onSelectRest(seconds) },
                                label = { Text("${seconds}s") },
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
            .clickable {
                val sessionId = running?.snapshot?.sessionId ?: return@clickable
                if (sessionId >= 0) onOpenSession(sessionId)
            },
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            Modifier
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                if (finished || running == null) {
                    Text("Hết giờ nghỉ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text("Vào set tiếp theo", color = MaterialTheme.colorScheme.onSecondaryContainer)
                } else {
                    val remaining = (running.snapshot.endElapsedRealtime - now).coerceAtLeast(0L)
                    Text(
                        "Nghỉ ${formatElapsed(remaining)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    Text("Chạm để mở buổi tập", color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
            if (running != null) {
                TextButton(onClick = { RestTimerController.skip(context) }) {
                    Text("Bỏ qua")
                }
            }
        }
    }
}
