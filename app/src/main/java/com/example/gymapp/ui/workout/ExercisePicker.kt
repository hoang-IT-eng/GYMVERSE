package com.example.gymapp.ui.workout

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gymapp.domain.Exercise
import com.example.gymapp.domain.IronLogCatalog
import com.example.gymapp.domain.MuscleGroup
import com.example.gymapp.domain.formatKg
import com.example.gymapp.domain.muscleLine
import com.example.gymapp.ui.theme.BurntSecondaryContainer
import com.example.gymapp.ui.theme.EmeraldTertiary
import com.example.gymapp.ui.theme.IronOnSurface
import com.example.gymapp.ui.theme.IronOnSurfaceVariant
import com.example.gymapp.ui.theme.IronSurface
import com.example.gymapp.ui.theme.IronSurfaceContainer
import com.example.gymapp.ui.theme.IronSurfaceHigh
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.NeonPrimaryContainer
import com.example.gymapp.ui.theme.OnBurntSecondaryContainer
import com.example.gymapp.ui.theme.OnNeonPrimary

@Composable
fun ExercisePicker(
    catalog: List<Exercise>,
    alreadyAdded: Set<Long>,
    onAdd: (Long) -> Unit,
    onClose: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedMuscle by rememberSaveable { mutableStateOf<MuscleGroup?>(null) }

    val filtered = catalog.filter { exercise ->
        val meta = IronLogCatalog.metaFor(exercise)
        val matchMuscle = selectedMuscle == null || exercise.muscle == selectedMuscle
        val matchQuery = query.isBlank() ||
            exercise.name.contains(query, ignoreCase = true) ||
            meta.vietnameseName.contains(query, ignoreCase = true) ||
            exercise.muscle.label.contains(query, ignoreCase = true)
        matchMuscle && matchQuery
    }

    Surface(Modifier.fillMaxSize(), color = IronSurface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 12.dp, top = 4.dp),
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = IronOnSurface,
                    )
                }
                Column {
                    Text(
                        text = "KHO BÀI TẬP HYPERTROPHY",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Thêm bài vào buổi tập",
                        style = MaterialTheme.typography.titleLarge,
                        color = IronOnSurface,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Search bar
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = IronSurfaceContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                                    text = "Tìm bài tập hoặc nhóm cơ (bench, ngực, kéo xô...)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = IronOnSurfaceVariant.copy(alpha = 0.6f),
                                )
                            }
                            inner()
                        },
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Muscle filter chips
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val allActive = selectedMuscle == null
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (allActive) NeonPrimaryContainer else IronSurfaceContainer,
                    modifier = Modifier.clickable { selectedMuscle = null },
                ) {
                    Text(
                        text = "Tất cả (${catalog.size})",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (allActive) OnNeonPrimary else IronOnSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                    )
                }
                MuscleGroup.entries.forEach { m ->
                    val active = selectedMuscle == m
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (active) NeonPrimaryContainer else IronSurfaceContainer,
                        modifier = Modifier.clickable {
                            selectedMuscle = if (active) null else m
                        },
                    ) {
                        Text(
                            text = m.label,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (active) OnNeonPrimary else IronOnSurfaceVariant,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                }
            }

            if (filtered.isEmpty()) {
                Text(
                    text = "Không có bài tập phù hợp.",
                    modifier = Modifier.padding(20.dp),
                    color = IronOnSurfaceVariant,
                )
                return@Column
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(filtered, key = { it.id }) { exercise ->
                    val added = exercise.id in alreadyAdded
                    val meta = IronLogCatalog.metaFor(exercise)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (!added) onAdd(exercise.id)
                                onClose()
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = IronSurfaceContainer,
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(IronSurfaceHigh),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FitnessCenter,
                                    contentDescription = null,
                                    tint = if (meta.tier == "Tier S") NeonPrimary else EmeraldTertiary,
                                )
                            }
                            Column(Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text(
                                        text = exercise.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = IronOnSurface,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (meta.tier == "Tier S") BurntSecondaryContainer else NeonPrimary.copy(alpha = 0.14f),
                                    ) {
                                        Text(
                                            text = meta.tier,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (meta.tier == "Tier S") OnBurntSecondaryContainer else NeonPrimary,
                                        )
                                    }
                                }
                                Text(
                                    text = meta.vietnameseName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IronOnSurfaceVariant,
                                )
                                Text(
                                    text = exercise.detailLine(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonPrimary,
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (added) IronSurfaceHigh else NeonPrimaryContainer,
                            ) {
                                Text(
                                    text = if (added) "Đã thêm" else "+ Thêm",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    color = if (added) IronOnSurfaceVariant else OnNeonPrimary,
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
}

private fun Exercise.detailLine(): String {
    val step = if (incrementKg > 0) "bước +${formatKg(incrementKg)} kg" else "tăng bằng rep"
    val muscles = muscleLine()
    return "$repMin–$repMax rep · $step · $muscles"
}
