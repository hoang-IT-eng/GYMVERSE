package com.example.gymapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp.domain.formatDuration
import com.example.gymapp.domain.formatElapsed
import com.example.gymapp.domain.formatEstimate
import com.example.gymapp.domain.formatSessionWhen
import com.example.gymapp.domain.formatSignedEstimate
import com.example.gymapp.service.RestPhase
import com.example.gymapp.service.RestTimerBus
import com.example.gymapp.ui.appViewModelFactory
import com.example.gymapp.ui.components.ActiveRestBanner
import com.example.gymapp.ui.components.Sparkline
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun HomeScreen(
    onOpenWorkout: (Long) -> Unit,
    onOpenProgress: (Long) -> Unit,
) {
    val viewModel: HomeViewModel = viewModel(factory = appViewModelFactory { HomeViewModel(it.repository) })
    val state by viewModel.state.collectAsStateWithLifecycle()
    val restPhase by RestTimerBus.phase.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val restVisible = restPhase !is RestPhase.Idle

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .then(if (restVisible) Modifier else Modifier.navigationBarsPadding()),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Text("GymApp", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Một chạm cho mỗi set.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item {
                Button(
                    onClick = {
                        scope.launch {
                            onOpenWorkout(viewModel.startOrResume())
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(if (state.active == null) 64.dp else 84.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (state.active == null) "Bắt đầu buổi tập" else "Tiếp tục buổi tập",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        val active = state.active
                        if (active != null) {
                            val elapsed = rememberElapsedSince(active.startedAt)
                            Text(
                                "${active.setCount} set · ${formatElapsed(elapsed)}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
                Text(
                    "Khi mọi set chạm trần rep, buổi sau tăng tạ.",
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (state.progress.isNotEmpty()) {
                item { Text("Tiến bộ", style = MaterialTheme.typography.titleLarge) }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.progress, key = { it.exerciseId }) { card ->
                            ProgressCard(card, onClick = { onOpenProgress(card.exerciseId) })
                        }
                    }
                }
            }
            item { Text("Buổi gần đây", style = MaterialTheme.typography.titleLarge) }
            if (state.recent.isEmpty() && state.active == null) {
                item {
                    Text(
                        "Chưa có buổi nào. Bấm bắt đầu, thêm bài, rồi chạm Ghi set.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(state.recent, key = { it.sessionId }) { session ->
                RecentSessionCard(session, onClick = { onOpenWorkout(session.sessionId) })
            }
        }
        if (restVisible) {
            ActiveRestBanner(onOpenSession = onOpenWorkout)
        }
    }
}

@Composable
private fun ProgressCard(card: ExerciseProgressUi, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .width(176.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(card.name, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleMedium)
            if (card.latestE1rm != null) {
                Text(formatEstimate(card.latestE1rm), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("1RM ước tính", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                val delta = card.deltaE1rm
                if (delta != null && abs(delta) >= 0.05) {
                    Text(
                        formatSignedEstimate(delta),
                        color = if (delta > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            } else {
                Text("${card.bestReps}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("rep tốt nhất", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Sparkline(
                values = card.sparkline,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().height(36.dp).padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun RecentSessionCard(session: RecentSessionUi, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    formatSessionWhen(session.startedAt),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    when (val finished = session.finishedAt) {
                        null -> "Đang mở"
                        else -> formatDuration(session.startedAt, finished)
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(session.title, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "${session.setCount} set",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun rememberElapsedSince(startedAt: Long): Long {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    androidx.compose.runtime.LaunchedEffect(startedAt) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1_000)
        }
    }
    return (now - startedAt).coerceAtLeast(0L)
}
