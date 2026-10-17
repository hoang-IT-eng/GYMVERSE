package com.example.gymapp.ui.routines

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GppMaybe
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp.domain.IronLogCatalog
import com.example.gymapp.domain.ProgressiveOverload
import com.example.gymapp.domain.SetPerformance
import com.example.gymapp.domain.formatEstimate
import com.example.gymapp.domain.formatKg
import com.example.gymapp.domain.formatLoad
import com.example.gymapp.domain.formatSessionWhen
import com.example.gymapp.domain.formatVolume
import com.example.gymapp.ui.appViewModelFactory
import com.example.gymapp.ui.progress.ProgressViewModel
import com.example.gymapp.ui.theme.BurntSecondary
import com.example.gymapp.ui.theme.BurntSecondaryContainer
import com.example.gymapp.ui.theme.CoralError
import com.example.gymapp.ui.theme.CoralErrorContainer
import com.example.gymapp.ui.theme.EmeraldTertiary
import com.example.gymapp.ui.theme.IronOnSurface
import com.example.gymapp.ui.theme.IronOnSurfaceVariant
import com.example.gymapp.ui.theme.IronOutline
import com.example.gymapp.ui.theme.IronSurface
import com.example.gymapp.ui.theme.IronSurfaceContainer
import com.example.gymapp.ui.theme.IronSurfaceHigh
import com.example.gymapp.ui.theme.IronSurfaceHighest
import com.example.gymapp.ui.theme.IronSurfaceLow
import com.example.gymapp.ui.theme.IronSurfaceLowest
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.NeonPrimaryContainer
import com.example.gymapp.ui.theme.OnCoralErrorContainer
import com.example.gymapp.ui.theme.OnNeonPrimary
import kotlinx.coroutines.launch

@Composable
fun ExerciseDetailScreen(
    exerciseId: Long,
    onBack: () -> Unit,
    onStartWithExercise: (String) -> Unit,
    onOpenOverloadDetail: (Long) -> Unit,
) {
    val viewModel: ProgressViewModel = viewModel(
        key = "ex-detail-$exerciseId",
        factory = appViewModelFactory { ProgressViewModel(it.repository, exerciseId) },
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val exercise = state.exercise
    var selectedTab by rememberSaveable { mutableStateOf("form") }
    var selectedAngle by rememberSaveable { mutableStateOf("Góc 45°") }
    var slowMo by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IronSurface)
            .statusBarsPadding(),
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = IronOnSurface,
                    )
                }
                Text(
                    text = exercise?.name ?: "Chi tiết bài tập",
                    style = MaterialTheme.typography.titleLarge,
                    color = IronOnSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(8.dp))
                if (exercise != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = IronSurfaceHigh,
                    ) {
                        Text(
                            text = exercise.muscle.label.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonPrimary)
            }
            return
        }
        if (exercise == null) {
            Text("Không tìm thấy bài tập.", modifier = Modifier.padding(20.dp), color = IronOnSurface)
            return
        }

        val meta = remember(exercise) { IronLogCatalog.metaFor(exercise) }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Top Headline / Quick Tag Bar
            item {
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
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NeonPrimaryContainer),
                        )
                        Text(
                            text = "HYPERTROPHY ${meta.tier.uppercase()}",
                            style = MaterialTheme.typography.labelMedium,
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
                                imageVector = Icons.Filled.FitnessCenter,
                                contentDescription = null,
                                tint = NeonPrimary,
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = meta.equipment,
                                style = MaterialTheme.typography.labelSmall,
                                color = IronOnSurface,
                            )
                        }
                    }
                }
            }

            // 1. Video / Form Loop Player Card (16:9)
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = IronSurfaceLowest,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(196.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        NeonPrimaryContainer.copy(alpha = 0.22f),
                                        IronSurfaceLowest,
                                    ),
                                ),
                            )
                            .padding(12.dp),
                    ) {
                        // Top overlay badges
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = IronSurfaceLowest.copy(alpha = 0.85f),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
                                            text = "Loop 4K Form chuẩn",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NeonPrimary,
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = IronSurfaceHigh.copy(alpha = 0.85f),
                                ) {
                                    Text(
                                        text = meta.vietnameseName,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IronOnSurfaceVariant,
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = IronSurfaceHigh,
                                modifier = Modifier.clickable { slowMo = !slowMo },
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.SlowMotionVideo,
                                        contentDescription = null,
                                        tint = NeonPrimary,
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Text(
                                        text = if (slowMo) "0.5x Slow" else "1.0x",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonPrimary,
                                    )
                                }
                            }
                        }

                        // Center Play / Biomechanical cue
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(IronSurfaceHigh),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    tint = NeonPrimary,
                                    modifier = Modifier.size(28.dp),
                                )
                            }
                            Text(
                                text = "Tempo chuẩn: 3-0-1-0 • Biên độ tối đa",
                                style = MaterialTheme.typography.labelMedium,
                                color = IronOnSurface,
                            )
                        }

                        // Bottom Angle Switcher & Scrubber
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(IronSurfaceLowest.copy(alpha = 0.85f))
                                        .padding(2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    listOf("Góc 45°", "Ngang", "Góc trên").forEach { angle ->
                                        val active = selectedAngle == angle
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (active) NeonPrimary else Color.Transparent)
                                                .clickable { selectedAngle = angle }
                                                .padding(horizontal = 8.dp, vertical = 3.dp),
                                        ) {
                                            Text(
                                                text = angle,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (active) OnNeonPrimary else IronOnSurfaceVariant,
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${exercise.repMin}–${exercise.repMax} reps",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonPrimary,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(CircleShape)
                                    .background(IronSurfaceHighest),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.68f)
                                        .height(4.dp)
                                        .clip(CircleShape)
                                        .background(NeonPrimaryContainer),
                                )
                            }
                        }
                    }
                }
            }

            // Quick Specification Matrix (3 cols)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SpecBox(
                        label = "KIỂU BÀI TẬP",
                        value = if (meta.isCompound) "Compound" else "Isolation",
                        sub = if (meta.isCompound) "Đa khớp" else "Cô lập cơ",
                        subColor = NeonPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    SpecBox(
                        label = "ĐỘ KHÓ",
                        value = meta.difficulty,
                        sub = "Yêu cầu form",
                        subColor = BurntSecondary,
                        modifier = Modifier.weight(1f),
                    )
                    SpecBox(
                        label = "BƯỚC TĂNG TẠ",
                        value = "+${formatKg(exercise.incrementKg)} kg",
                        sub = "${exercise.repMin}–${exercise.repMax} reps",
                        subColor = IronOnSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // 2. Muscle Anatomy Map Card
            item {
                MuscleAnatomyCard(
                    primaryLabel = exercise.muscle.label,
                    secondaryLabels = exercise.secondaryMuscles.map { it.label },
                )
            }

            // 3. Segmented Navigation Tabs (Form chuẩn vs Kỷ lục & PR)
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = IronSurfaceLow,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        val isForm = selectedTab == "form"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isForm) NeonPrimary else Color.Transparent)
                                .clickable { selectedTab = "form" }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Verified,
                                    contentDescription = null,
                                    tint = if (isForm) OnNeonPrimary else IronOnSurfaceVariant,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = "Form chuẩn",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isForm) OnNeonPrimary else IronOnSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        val isHistory = selectedTab == "history"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isHistory) NeonPrimary else Color.Transparent)
                                .clickable { selectedTab = "history" }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Analytics,
                                    contentDescription = null,
                                    tint = if (isHistory) OnNeonPrimary else IronOnSurfaceVariant,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = "Kỷ lục & PR",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isHistory) OnNeonPrimary else IronOnSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }

            if (selectedTab == "form") {
                // 4-Step Pre-lift Checklist
                item {
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
                                    Icon(
                                        imageVector = Icons.Filled.Checklist,
                                        contentDescription = null,
                                        tint = NeonPrimary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Text(
                                        text = "Quy trình Setup 4 bước",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = IronOnSurface,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Text(
                                    text = "Pre-lift Protocol",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IronOnSurfaceVariant,
                                )
                            }

                            meta.setupSteps.forEachIndexed { idx, (title, body) ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = IronSurfaceLow,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.Top,
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(NeonPrimary.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = "${idx + 1}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = NeonPrimary,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = IronOnSurface,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                            Text(
                                                text = body,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = IronOnSurfaceVariant,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Pro Tip Hypertrophy Callout
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = IronSurfaceHigh,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonPrimaryContainer.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.TipsAndUpdates,
                                    contentDescription = null,
                                    tint = NeonPrimary,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "Mẹo Hypertrophy tối ưu ${exercise.muscle.label}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = NeonPrimary,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = meta.proTip,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = IronOnSurface,
                                )
                            }
                        }
                    }
                }

                // Injury Prevention & Red Flags
                item {
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
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.GppMaybe,
                                    contentDescription = null,
                                    tint = BurntSecondary,
                                    modifier = Modifier.size(22.dp),
                                )
                                Text(
                                    text = "Lỗi sai phổ biến & Cảnh báo an toàn",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = IronOnSurface,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CoralErrorContainer.copy(alpha = 0.25f),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Dangerous,
                                        contentDescription = null,
                                        tint = CoralError,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            text = meta.redFlagTitle,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = CoralError,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = meta.redFlagBody,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = OnCoralErrorContainer,
                                        )
                                    }
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BurntSecondaryContainer.copy(alpha = 0.22f),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = null,
                                        tint = BurntSecondary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            text = meta.warningTitle,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = BurntSecondary,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = meta.warningBody,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = IronOnSurface,
                                        )
                                    }
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = IronSurfaceLow,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Security,
                                        contentDescription = null,
                                        tint = NeonPrimary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            text = "Quy tắc an toàn tạ nặng (>85% 1RM)",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = IronOnSurface,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                        Text(
                                            text = "Luôn khởi động 2 hiệp nhẹ tăng dần tải và nhờ Spotter hoặc cài thanh đỡ an toàn trước khi vào hiệp chính.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = IronOnSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Tab Pane 2: Kỷ lục & PR Stats
                val bestPoint = state.points.filter { it.bestE1rm != null }.maxByOrNull { it.bestE1rm ?: 0.0 }
                val latestPoint = state.points.lastOrNull()
                val suggestion = ProgressiveOverload.suggest(
                    lastSessionSets = latestPoint?.let {
                        val w = it.e1rmWeightKg ?: exercise.startKg
                        val r = it.e1rmReps ?: it.bestReps
                        listOf(SetPerformance(w, r))
                    } ?: emptyList(),
                    repMin = exercise.repMin,
                    repMax = exercise.repMax,
                    incrementKg = exercise.incrementKg,
                    startKg = exercise.startKg,
                )

                item {
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
                                    Icon(
                                        imageVector = Icons.Filled.EmojiEvents,
                                        contentDescription = null,
                                        tint = NeonPrimary,
                                    )
                                    Text(
                                        text = "Kỷ lục cá nhân (All-time PR)",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = IronOnSurface,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Text(
                                    text = bestPoint?.let { formatSessionWhen(it.startedAt) } ?: "Chưa ghi nhận",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IronOnSurfaceVariant,
                                )
                            }

                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = bestPoint?.bestE1rm?.let { formatEstimate(it) } ?: formatKg(exercise.startKg),
                                    style = MaterialTheme.typography.displaySmall,
                                    color = NeonPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                )
                                Text(
                                    text = "kg (Estimated 1RM)",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = IronOnSurfaceVariant,
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text("Set nặng nhất thực tế", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                                    Text(
                                        text = if (bestPoint?.e1rmWeightKg != null) {
                                            "${formatLoad(bestPoint.e1rmWeightKg, exercise.addedLoad)} × ${bestPoint.e1rmReps} reps"
                                        } else {
                                            "Chưa có dữ liệu"
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        color = IronOnSurface,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Column(Modifier.weight(1f)) {
                                    Text("Khối lượng tích lũy/buổi", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                                    Text(
                                        text = latestPoint?.let { formatVolume(it.volumeKg) } ?: "0 kg",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = NeonPrimary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }

                // Next Progressive Overload Target
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = IronSurfaceContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenOverloadDetail(exercise.id) },
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Flag,
                                        contentDescription = null,
                                        tint = BurntSecondary,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Text(
                                        text = "MỤC TIÊU BUỔI TỚI",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = BurntSecondary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Text(
                                    text = "${formatLoad(suggestion.weightKg, exercise.addedLoad)} × ${suggestion.targetReps} reps",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = IronOnSurface,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = suggestion.reason,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IronOnSurfaceVariant,
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BurntSecondaryContainer.copy(alpha = 0.25f),
                            ) {
                                Text(
                                    text = "+${formatKg(exercise.incrementKg)}k",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = BurntSecondary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { onOpenOverloadDetail(exercise.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IronSurfaceHigh,
                            contentColor = NeonPrimary,
                        ),
                    ) {
                        Icon(imageVector = Icons.Filled.Timeline, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Xem chi tiết chu kỳ Progressive Overload", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Sticky Bottom Footer CTAs
        Surface(
            color = IronSurfaceLow.copy(alpha = 0.96f),
            shadowElevation = 16.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { onStartWithExercise(exercise.name) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPrimaryContainer,
                        contentColor = OnNeonPrimary,
                    ),
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Thêm vào buổi tập & Luyện tập ngay",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecBox(
    label: String,
    value: String,
    sub: String,
    subColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = IronSurfaceContainer,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
            Text(value, style = MaterialTheme.typography.labelLarge, color = IronOnSurface, fontWeight = FontWeight.Bold)
            Text(sub, style = MaterialTheme.typography.labelSmall, color = subColor)
        }
    }
}

@Composable
private fun MuscleAnatomyCard(
    primaryLabel: String,
    secondaryLabels: List<String>,
) {
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
                    Icon(
                        imageVector = Icons.Filled.AccessibilityNew,
                        contentDescription = null,
                        tint = NeonPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = "Bản đồ kích hoạt cơ bắp",
                        style = MaterialTheme.typography.titleMedium,
                        color = IronOnSurface,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = NeonPrimary.copy(alpha = 0.14f),
                ) {
                    Text(
                        text = "EMG TỐI ĐA",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Stylized Torso EMG Activation Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(IronSurfaceLow),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(200.dp, 132.dp)) {
                    val cx = size.width / 2f
                    // Base torso silhouette
                    drawRoundRect(
                        color = IronSurfaceHighest,
                        topLeft = Offset(cx - 42.dp.toPx(), 16.dp.toPx()),
                        size = Size(84.dp.toPx(), 100.dp.toPx()),
                        cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                    )
                    // Primary muscle pads (Neon Green 100%)
                    drawRoundRect(
                        color = NeonPrimaryContainer.copy(alpha = 0.90f),
                        topLeft = Offset(cx - 36.dp.toPx(), 26.dp.toPx()),
                        size = Size(33.dp.toPx(), 28.dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                    )
                    drawRoundRect(
                        color = NeonPrimaryContainer.copy(alpha = 0.90f),
                        topLeft = Offset(cx + 3.dp.toPx(), 26.dp.toPx()),
                        size = Size(33.dp.toPx(), 28.dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                    )
                    // Secondary delts/arms (Emerald 65%)
                    drawRoundRect(
                        color = EmeraldTertiary.copy(alpha = 0.78f),
                        topLeft = Offset(cx - 56.dp.toPx(), 24.dp.toPx()),
                        size = Size(16.dp.toPx(), 42.dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                    )
                    drawRoundRect(
                        color = EmeraldTertiary.copy(alpha = 0.78f),
                        topLeft = Offset(cx + 40.dp.toPx(), 24.dp.toPx()),
                        size = Size(16.dp.toPx(), 42.dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                    )
                    // Core stabilizers (35%)
                    drawRoundRect(
                        color = IronOutline.copy(alpha = 0.45f),
                        topLeft = Offset(cx - 26.dp.toPx(), 60.dp.toPx()),
                        size = Size(52.dp.toPx(), 44.dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = IronSurfaceHighest.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                ) {
                    Text(
                        text = "$primaryLabel: 100%",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPrimary,
                    )
                }
            }

            // Legend rows
            MuscleLegendRow(
                dotColor = NeonPrimaryContainer,
                name = "Cơ chủ vận: $primaryLabel",
                pct = "100%",
                tag = "CHÍNH",
            )
            MuscleLegendRow(
                dotColor = EmeraldTertiary,
                name = "Cơ hỗ trợ: ${secondaryLabels.ifEmpty { listOf("Cơ phụ trợ") }.joinToString(", ")}",
                pct = "65%",
                tag = "HỖ TRỢ",
            )
            MuscleLegendRow(
                dotColor = IronOutline,
                name = "Lưng xô & Cơ lõi (Core Stabilizers)",
                pct = "35%",
                tag = "ỔN ĐỊNH",
            )
        }
    }
}

@Composable
private fun MuscleLegendRow(
    dotColor: Color,
    name: String,
    pct: String,
    tag: String,
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = IronSurfaceHigh.copy(alpha = 0.55f),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(dotColor),
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = IronOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = pct,
                    style = MaterialTheme.typography.labelMedium,
                    color = dotColor,
                    fontWeight = FontWeight.Bold,
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = IronSurfaceLowest,
                ) {
                    Text(
                        text = tag,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = IronOnSurfaceVariant,
                    )
                }
            }
        }
    }
}
