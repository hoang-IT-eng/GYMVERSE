package com.example.gymapp.ui.routines

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gymapp.data.RoutineDayDraft
import com.example.gymapp.domain.Exercise
import com.example.gymapp.domain.RoutinePlan
import com.example.gymapp.ui.theme.IronOnSurface
import com.example.gymapp.ui.theme.IronOnSurfaceVariant
import com.example.gymapp.ui.theme.IronSurface
import com.example.gymapp.ui.theme.IronSurfaceContainer
import com.example.gymapp.ui.theme.IronSurfaceHigh
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.NeonPrimaryContainer
import com.example.gymapp.ui.theme.OnNeonPrimary

private class EditorDay(
    title: String,
    isRest: Boolean,
    exerciseIds: List<Long>,
) {
    var title by mutableStateOf(title)
    var isRest by mutableStateOf(isRest)
    val exerciseIds = mutableStateListOf<Long>().apply { addAll(exerciseIds) }
}

@Composable
fun CustomRoutineEditor(
    catalog: List<Exercise>,
    existing: RoutinePlan?,
    onDismiss: () -> Unit,
    onSave: (String, List<RoutineDayDraft>) -> Unit,
) {
    var name by rememberSaveable(existing?.id) { mutableStateOf(existing?.name ?: "") }
    val days = remember(existing?.id) {
        mutableStateListOf<EditorDay>().apply {
            val source = existing?.days?.filter { it.exerciseIds.isNotEmpty() || it.isRest }.orEmpty()
            if (source.isEmpty()) {
                add(EditorDay("Ngày 1", false, emptyList()))
            } else {
                source.forEach { day ->
                    add(EditorDay(day.title, day.isRest, day.exerciseIds))
                }
            }
        }
    }
    var pickingDay by remember { mutableStateOf<Int?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    val byId = catalog.associateBy { it.id }
    val canSave = name.isNotBlank() && days.any { it.isRest || it.exerciseIds.isNotEmpty() }

    Box(Modifier.fillMaxSize()) {
    Surface(Modifier.fillMaxSize(), color = IronSurface) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding(),
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Đóng", tint = IronOnSurface)
                }
                Text(
                    text = if (existing == null) "Soạn giáo án" else "Sửa giáo án",
                    style = MaterialTheme.typography.titleLarge,
                    color = IronOnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "Lưu",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (canSave) OnNeonPrimary else IronOnSurfaceVariant,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .background(
                            if (canSave) NeonPrimaryContainer else IronSurfaceHigh,
                            RoundedCornerShape(10.dp),
                        )
                        .clickable(enabled = canSave) {
                            onSave(
                                name.trim(),
                                days.map {
                                    RoutineDayDraft(
                                        title = it.title,
                                        isRest = it.isRest,
                                        exerciseIds = it.exerciseIds.toList(),
                                    )
                                },
                            )
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }

            LazyColumn(
                Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tên giáo án") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = "Mỗi ngày là một buổi. Bài được gắn theo id, nên đổi tên bài sau này giáo án vẫn đúng.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = IronOnSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                items(days.size) { index ->
                    val day = days[index]
                    Surface(shape = RoundedCornerShape(16.dp), color = IronSurfaceContainer) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = day.title,
                                onValueChange = { day.title = it },
                                label = { Text("Tên ngày ${index + 1}") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Ngày nghỉ", color = IronOnSurface)
                                Switch(
                                    checked = day.isRest,
                                    onCheckedChange = { day.isRest = it },
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = NeonPrimaryContainer,
                                        checkedThumbColor = OnNeonPrimary,
                                    ),
                                )
                            }
                            if (!day.isRest) {
                                day.exerciseIds.forEach { id ->
                                    val exercise = byId[id]
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            exercise?.name ?: "Bài đã xóa",
                                            color = IronOnSurface,
                                            modifier = Modifier.weight(1f),
                                        )
                                        Text(
                                            "Bỏ",
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.clickable { day.exerciseIds.remove(id) },
                                        )
                                    }
                                }
                                Text(
                                    text = "Thêm bài",
                                    color = NeonPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { pickingDay = index },
                                )
                            }
                            if (days.size > 1) {
                                Text(
                                    text = "Xóa ngày",
                                    color = IronOnSurfaceVariant,
                                    modifier = Modifier.clickable { days.removeAt(index) },
                                )
                            }
                        }
                    }
                }
                item {
                    Text(
                        text = "Thêm ngày",
                        color = OnNeonPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(NeonPrimaryContainer, RoundedCornerShape(12.dp))
                            .clickable { days.add(EditorDay("Ngày ${days.size + 1}", false, emptyList())) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                    )
                }
            }
        }
    }

    val dayIndex = pickingDay
    if (dayIndex != null && dayIndex in days.indices) {
        val day = days[dayIndex]
        Surface(Modifier.fillMaxSize(), color = IronSurface) {
            Column(Modifier.fillMaxSize().statusBarsPadding()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { pickingDay = null }) {
                        Icon(Icons.Filled.Close, contentDescription = "Đóng", tint = IronOnSurface)
                    }
                    Text("Chọn bài", style = MaterialTheme.typography.titleLarge, color = IronOnSurface)
                }
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    singleLine = true,
                    label = { Text("Tìm bài") },
                )
                LazyColumn(Modifier.weight(1f).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val shown = catalog.filter {
                        query.isBlank() || it.name.contains(query, ignoreCase = true) || it.muscle.label.contains(query, ignoreCase = true)
                    }
                    items(shown, key = { it.id }) { exercise ->
                        val selected = exercise.id in day.exerciseIds
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) IronSurfaceHigh else IronSurfaceContainer,
                            modifier = Modifier.fillMaxWidth().clickable {
                                if (selected) day.exerciseIds.remove(exercise.id) else day.exerciseIds.add(exercise.id)
                            },
                        ) {
                            Row(
                                Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column {
                                    Text(exercise.name, color = IronOnSurface, fontWeight = FontWeight.SemiBold)
                                    Text(exercise.muscle.label, color = IronOnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                                }
                                if (selected) {
                                    Icon(Icons.Filled.Add, contentDescription = null, tint = NeonPrimary)
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
