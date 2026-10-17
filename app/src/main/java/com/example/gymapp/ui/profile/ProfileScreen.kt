package com.example.gymapp.ui.profile

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gymapp.service.RestTimerStore
import com.example.gymapp.ui.theme.BurntSecondary
import com.example.gymapp.ui.theme.BurntSecondaryContainer
import com.example.gymapp.ui.theme.EmeraldTertiary
import com.example.gymapp.ui.theme.IronOnSurface
import com.example.gymapp.ui.theme.IronOnSurfaceVariant
import com.example.gymapp.ui.theme.IronOutline
import com.example.gymapp.ui.theme.IronSurfaceContainer
import com.example.gymapp.ui.theme.IronSurfaceHigh
import com.example.gymapp.ui.theme.IronSurfaceHighest
import com.example.gymapp.ui.theme.IronSurfaceLow
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.NeonPrimaryContainer
import com.example.gymapp.ui.theme.OnNeonPrimary
import java.util.Locale

@Composable
fun ProfileScreen(
    totalSessionsCount: Int,
) {
    val context = LocalContext.current
    var athleteName by remember { mutableStateOf(RestTimerStore.readAthleteName(context)) }
    var weightKg by remember { mutableDoubleStateOf(RestTimerStore.readBodyWeightKg(context)) }
    var bodyFatPct by remember { mutableDoubleStateOf(RestTimerStore.readBodyFatPct(context)) }
    var heightCm by remember { mutableIntStateOf(RestTimerStore.readHeightCm(context)) }
    var restSeconds by remember { mutableIntStateOf(RestTimerStore.readRestSeconds(context)) }
    var smallestInc by remember { mutableDoubleStateOf(RestTimerStore.readSmallestIncrementKg(context)) }
    var audioBell by remember { mutableStateOf(RestTimerStore.readAudioBell(context)) }
    var hapticEnabled by remember { mutableStateOf(RestTimerStore.readHapticEnabled(context)) }
    var autoOverload by remember { mutableStateOf(true) }
    var syncToast by remember { mutableStateOf(false) }
    var showEditBodyDialog by remember { mutableStateOf(false) }

    val lbmKg = weightKg * (1.0 - (bodyFatPct / 100.0))

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (syncToast) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeonPrimaryContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CloudDone,
                                contentDescription = null,
                                tint = OnNeonPrimary,
                            )
                            Text(
                                text = "Đã lưu và đồng bộ cài đặt thành công!",
                                style = MaterialTheme.typography.labelMedium,
                                color = OnNeonPrimary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            text = "Đóng",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnNeonPrimary,
                            modifier = Modifier.clickable { syncToast = false },
                        )
                    }
                }
            }
        }

        // Top Actions Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Hồ sơ & Cài đặt",
                        style = MaterialTheme.typography.titleLarge,
                        color = IronOnSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "CẤU HÌNH VẬN ĐỘNG VIÊN",
                        style = MaterialTheme.typography.labelSmall,
                        color = IronOnSurfaceVariant,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = IronSurfaceHigh,
                        modifier = Modifier.clickable { syncToast = true },
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Sync,
                                contentDescription = null,
                                tint = NeonPrimary,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = "Đồng bộ",
                                style = MaterialTheme.typography.labelMedium,
                                color = NeonPrimary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = IronSurfaceHigh,
                        modifier = Modifier.clickable { showEditBodyDialog = true },
                    ) {
                        Box(
                            modifier = Modifier.size(38.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Sửa hồ sơ",
                                tint = IronOnSurface,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }

        // SECTION 1: User Profile & Body Composition Bento Card
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = IronSurfaceContainer,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(modifier = Modifier.size(72.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(IronSurfaceHighest),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = NeonPrimary,
                                    modifier = Modifier.size(38.dp),
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(NeonPrimaryContainer),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Verified,
                                    contentDescription = null,
                                    tint = OnNeonPrimary,
                                    modifier = Modifier.size(14.dp),
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = BurntSecondaryContainer.copy(alpha = 0.3f),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.WorkspacePremium,
                                        contentDescription = null,
                                        tint = BurntSecondary,
                                        modifier = Modifier.size(12.dp),
                                    )
                                    Text(
                                        text = "PRO HYPERTROPHY",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BurntSecondary,
                                    )
                                }
                            }
                            Text(
                                text = athleteName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = IronOnSurface,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "@ironlog.lift • Chu kỳ Hypertrophy",
                                style = MaterialTheme.typography.labelMedium,
                                color = NeonPrimary,
                            )
                        }
                    }

                    // Body Metrics 3-col Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        BodyMetricBox(
                            label = "CÂN NẶNG",
                            value = String.format(Locale.US, "%.1f", weightKg),
                            unit = "kg",
                            sub = "Lean Bulk",
                            subColor = NeonPrimary,
                            modifier = Modifier.weight(1f),
                        )
                        BodyMetricBox(
                            label = "BODY FAT",
                            value = String.format(Locale.US, "%.1f", bodyFatPct),
                            unit = "%",
                            sub = "Lean Zone",
                            subColor = BurntSecondary,
                            modifier = Modifier.weight(1f),
                        )
                        BodyMetricBox(
                            label = "KHỐI NẠC (LBM)",
                            value = String.format(Locale.US, "%.1f", lbmKg),
                            unit = "kg",
                            sub = "Cao: $heightCm cm",
                            subColor = IronOnSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = IronSurfaceHigh,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showEditBodyDialog = true },
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MonitorWeight,
                                    contentDescription = null,
                                    tint = NeonPrimary,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = "Cập nhật Cân nặng & InBody",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = NeonPrimary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = null,
                                tint = IronOnSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }

        // SECTION 2: Workout Engine & Rest Timer Settings
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = null,
                        tint = NeonPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "CẤU HÌNH BUỔI TẬP (WORKOUT ENGINE)",
                        style = MaterialTheme.typography.labelSmall,
                        color = IronOnSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = IronSurfaceContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        // Rest Timer Presets
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Thời gian nghỉ mặc định",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = IronOnSurface,
                                )
                                Text(
                                    text = "$restSeconds giây",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = NeonPrimary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Text(
                                text = "Đồng hồ đếm ngược tự động chạy sau mỗi hiệp (kể cả khi tắt màn hình).",
                                style = MaterialTheme.typography.labelSmall,
                                color = IronOnSurfaceVariant,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                listOf(60, 90, 120, 180).forEach { sec ->
                                    val active = restSeconds == sec
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (active) NeonPrimaryContainer else IronSurfaceLow)
                                            .clickable {
                                                restSeconds = sec
                                                RestTimerStore.saveRestSeconds(context, sec)
                                            },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "${sec}s",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (active) OnNeonPrimary else IronOnSurface,
                                            fontWeight = if (active) FontWeight.ExtraBold else FontWeight.SemiBold,
                                        )
                                    }
                                }
                            }
                        }

                        // Smallest Weight Increment
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Bước nhảy tạ nhỏ nhất",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = IronOnSurface,
                                )
                                Text(
                                    text = "$smallestInc kg",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = NeonPrimary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                listOf(
                                    0.5 to "Micro-plates",
                                    1.25 to "Chuẩn Gym VN",
                                    2.5 to "Phổ thông",
                                ).forEach { (inc, sub) ->
                                    val active = smallestInc == inc
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (active) NeonPrimaryContainer else IronSurfaceLow)
                                            .clickable {
                                                smallestInc = inc
                                                RestTimerStore.saveSmallestIncrementKg(context, inc.toFloat())
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        Text(
                                            text = "$inc kg",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (active) OnNeonPrimary else IronOnSurface,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = sub,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (active) OnNeonPrimary else IronOnSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }

                        // Audio & Haptic Switches
                        SettingToggleRow(
                            icon = Icons.AutoMirrored.Filled.VolumeUp,
                            iconTint = NeonPrimary,
                            title = "Âm thanh chuông báo",
                            subtitle = "Đếm ngược 3-2-1 chuẩn bị hiệp tiếp theo",
                            checked = audioBell,
                            onCheckedChange = {
                                audioBell = it
                                RestTimerStore.saveFeedbackPrefs(context, audioBell, hapticEnabled)
                            },
                        )

                        SettingToggleRow(
                            icon = Icons.Filled.Vibration,
                            iconTint = BurntSecondary,
                            title = "Rung phản hồi (Haptic)",
                            subtitle = "Rung mạnh khi hoàn thành hiệp và hết giờ nghỉ",
                            checked = hapticEnabled,
                            onCheckedChange = {
                                hapticEnabled = it
                                RestTimerStore.saveFeedbackPrefs(context, audioBell, hapticEnabled)
                            },
                        )
                    }
                }
            }
        }

        // SECTION 3: RIR & Progressive Overload Engine
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = EmeraldTertiary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "HỆ THỐNG QUÁ TẢI LŨY TIẾN & RIR",
                        style = MaterialTheme.typography.labelSmall,
                        color = IronOnSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = IronSurfaceContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("Công thức tính 1RM", style = MaterialTheme.typography.titleMedium, color = IronOnSurface)
                                Text("Thuật toán quy đổi sức mạnh tối đa (1–12 reps)", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = IronSurfaceHigh,
                            ) {
                                Text(
                                    text = "Epley Formula",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = NeonPrimary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("Ngưỡng RIR Hypertrophy", style = MaterialTheme.typography.titleMedium, color = IronOnSurface)
                                Text("Mục tiêu reps dự trữ chuẩn kích thích phì đại cơ", style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = IronSurfaceHigh,
                            ) {
                                Text(
                                    text = "RIR 1 - 2",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = BurntSecondary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        SettingToggleRow(
                            icon = Icons.Filled.Bolt,
                            iconTint = NeonPrimary,
                            title = "Tự động gợi ý tăng tạ",
                            subtitle = "Đề xuất mức tạ tiếp theo khi đạt trần rep buổi trước",
                            checked = autoOverload,
                            onCheckedChange = { autoOverload = it },
                        )
                    }
                }
            }
        }

        // SECTION 4: Data Management & Cloud Sync
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudSync,
                        contentDescription = null,
                        tint = NeonPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "DỮ LIỆU & BẢO MẬT ĐỒNG BỘ",
                        style = MaterialTheme.typography.labelSmall,
                        color = IronOnSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = IronSurfaceContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = IronSurfaceLow,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NeonPrimary.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CloudDone,
                                        contentDescription = null,
                                        tint = NeonPrimary,
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Room Local Database + Auto Backup",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = IronOnSurface,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "Đã lưu an toàn $totalSessionsCount buổi tập trên thiết bị",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IronOnSurfaceVariant,
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { syncToast = true }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(IronSurfaceHigh),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.TableView,
                                        contentDescription = null,
                                        tint = IronOnSurface,
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Xuất dữ liệu buổi tập (CSV/Excel)",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = IronOnSurface,
                                    )
                                    Text(
                                        text = "Tải toàn bộ buổi tập & lịch sử khối lượng",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IronOnSurfaceVariant,
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = null,
                                tint = IronOnSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }

        // App Version Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Terminal,
                        contentDescription = null,
                        tint = NeonPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "IRONLOG HYPERTROPHY ENGINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = IronOnSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = "Phiên bản v2.4.0 (Pro Edition)",
                    style = MaterialTheme.typography.labelSmall,
                    color = IronOutline,
                )
            }
        }
    }

    if (showEditBodyDialog) {
        EditBodyMetricsDialog(
            initialName = athleteName,
            initialWeight = weightKg.toString(),
            initialFat = bodyFatPct.toString(),
            initialHeight = heightCm.toString(),
            onDismiss = { showEditBodyDialog = false },
            onSave = { newName, w, f, h ->
                athleteName = newName
                weightKg = w
                bodyFatPct = f
                heightCm = h
                RestTimerStore.saveBodyMetrics(context, w.toFloat(), f.toFloat(), h, newName)
                showEditBodyDialog = false
                syncToast = true
            },
        )
    }
}

@Composable
private fun BodyMetricBox(
    label: String,
    value: String,
    unit: String,
    sub: String,
    subColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = IronSurfaceLow,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(value, style = MaterialTheme.typography.titleLarge, color = IronOnSurface, fontWeight = FontWeight.Bold)
                Text(unit, style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
            }
            Text(sub, style = MaterialTheme.typography.labelSmall, color = subColor)
        }
    }
}

@Composable
private fun SettingToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(IronSurfaceHigh),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = IronOnSurface)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = IronOnSurfaceVariant)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = OnNeonPrimary,
                checkedTrackColor = NeonPrimaryContainer,
                uncheckedThumbColor = IronOnSurfaceVariant,
                uncheckedTrackColor = IronSurfaceHighest,
            ),
        )
    }
}

@Composable
private fun EditBodyMetricsDialog(
    initialName: String,
    initialWeight: String,
    initialFat: String,
    initialHeight: String,
    onDismiss: () -> Unit,
    onSave: (String, Double, Double, Int) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var weight by remember { mutableStateOf(initialWeight) }
    var fat by remember { mutableStateOf(initialFat) }
    var height by remember { mutableStateOf(initialHeight) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cập nhật Hồ sơ & InBody") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên vận động viên") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Cân nặng (kg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                OutlinedTextField(
                    value = fat,
                    onValueChange = { fat = it },
                    label = { Text("Tỷ lệ mỡ Body Fat (%)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Chiều cao (cm)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val w = weight.replace(',', '.').toDoubleOrNull() ?: 74.5
                    val f = fat.replace(',', '.').toDoubleOrNull() ?: 14.2
                    val h = height.toIntOrNull() ?: 175
                    onSave(name.ifBlank { "Minh Quân" }, w, f, h)
                },
            ) {
                Text("Lưu", color = NeonPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        },
    )
}
