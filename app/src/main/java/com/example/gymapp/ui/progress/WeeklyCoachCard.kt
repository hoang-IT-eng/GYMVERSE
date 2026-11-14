package com.example.gymapp.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymapp.domain.CoachReport
import com.example.gymapp.domain.MuscleVolumeLandmark
import com.example.gymapp.domain.formatSessionWhen
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.BurntSecondary
import com.example.gymapp.ui.theme.CoralError
import com.example.gymapp.ui.theme.EmeraldTertiary
import com.example.gymapp.ui.theme.IronOnSurface
import com.example.gymapp.ui.theme.IronOnSurfaceVariant
import com.example.gymapp.ui.theme.IronSurfaceContainer
import com.example.gymapp.ui.theme.IronSurfaceHigh
import com.example.gymapp.ui.theme.NeonPrimaryContainer
import com.example.gymapp.ui.theme.OnNeonPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeeklyCoachCard(viewModel: CoachViewModel, landmarks: List<MuscleVolumeLandmark>) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Surface(shape = RoundedCornerShape(16.dp), color = IronSurfaceHigh, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.SmartToy, contentDescription = null, tint = IronOnSurface, modifier = Modifier.size(18.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text("Huấn luyện viên AI", style = MaterialTheme.typography.titleMedium, color = IronOnSurface, fontWeight = FontWeight.Bold)
                    Text(
                        text = state.report?.let { "Tổng kết lúc ${stamp(it.generatedAt)}" } ?: "Tổng kết tập luyện & dinh dưỡng 7 ngày qua",
                        style = MaterialTheme.typography.labelSmall,
                        color = IronOnSurface,
                    )
                }
            }

            val report = state.report
            if (report != null) {
                ReportBody(report)
            } else {
                QuickTip(landmarks)
            }

            state.error?.let { message ->
                Text(message, style = MaterialTheme.typography.bodyMedium, color = CoralError)
            }

            if (state.configured) {
                Button(
                    onClick = viewModel::generate,
                    enabled = !state.loading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPrimaryContainer, contentColor = OnNeonPrimary),
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(color = OnNeonPrimary, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        Text("  AI đang phân tích…", fontWeight = FontWeight.Bold)
                    } else {
                        Text(if (report == null) "Tạo tổng kết tuần" else "Tổng kết lại", fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    "Gửi số liệu tổng hợp (không gửi tên hay ảnh) cho Gemini để nhận xét.",
                    style = MaterialTheme.typography.labelSmall,
                    color = IronOnSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ReportBody(report: CoachReport) {
    Text(report.headline, style = MaterialTheme.typography.titleSmall, color = IronOnSurface, fontWeight = FontWeight.Bold)
    if (report.deloadSuggested) {
        Surface(shape = RoundedCornerShape(10.dp), color = BurntSecondary.copy(alpha = 0.14f), modifier = Modifier.fillMaxWidth()) {
            Text(
                "Nên cân nhắc tuần giảm tải (deload): giảm khoảng một nửa số hiệp, giữ mức tạ.",
                style = MaterialTheme.typography.labelMedium,
                color = BurntSecondary,
                modifier = Modifier.padding(10.dp),
            )
        }
    }
    ReportSection("Làm tốt", report.wins, Icons.Filled.CheckCircle, EmeraldTertiary)
    ReportSection("Cần chú ý", report.issues, Icons.Filled.Warning, BurntSecondary)
    ReportSection("Tuần tới", report.actions, Icons.Filled.TaskAlt, IronOnSurface)
}

@Composable
private fun ReportSection(title: String, lines: List<String>, icon: ImageVector, color: Color) {
    if (lines.isEmpty()) return
    Surface(shape = RoundedCornerShape(10.dp), color = IronSurfaceContainer, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                Text(title, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
            }
            lines.forEach { line ->
                Text("• $line", style = MaterialTheme.typography.bodyMedium, color = IronOnSurfaceVariant)
            }
        }
    }
}

@Composable
private fun QuickTip(landmarks: List<MuscleVolumeLandmark>) {
    val underMev = landmarks.firstOrNull { it.isUnderMev }
    Surface(shape = RoundedCornerShape(10.dp), color = IronSurfaceContainer, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = BurntSecondary, modifier = Modifier.size(20.dp))
            Column {
                Text(
                    text = if (underMev != null) "Cần bổ sung khối lượng (${underMev.title})" else "Khối lượng tuần đạt ngưỡng tối ưu",
                    style = MaterialTheme.typography.labelMedium,
                    color = BurntSecondary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (underMev != null) {
                        "Nhóm ${underMev.title} hiện có ${underMev.setsCompleted}/${underMev.mevSets} sets (dưới MEV). Thêm ${(underMev.mevSets - underMev.setsCompleted).coerceAtLeast(2)} sets vào buổi tới."
                    } else {
                        "Mọi nhóm cơ đều trong vùng MEV–MRV. Tiếp tục double progression."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = IronOnSurfaceVariant,
                )
            }
        }
    }
}

private fun stamp(ms: Long): String =
    "${SimpleDateFormat("HH:mm", Locale.US).format(Date(ms))} ${formatSessionWhen(ms).lowercase()}"
