package com.example.gymapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gymapp.ui.theme.AlertOrange
import com.example.gymapp.ui.theme.IronOnSurface
import com.example.gymapp.ui.theme.IronOnSurfaceVariant
import com.example.gymapp.ui.theme.IronSurface
import com.example.gymapp.ui.theme.IronSurfaceHigh
import com.example.gymapp.ui.theme.IronSurfaceLowest
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.OnNeonPrimary

@Composable
fun IronLogTopBar(
    subtitle: String,
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    Surface(
        color = IronSurface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .height(68.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(NeonPrimary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Eco,
                        contentDescription = null,
                        tint = OnNeonPrimary,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Column {
                    Text(
                        text = "IronLog",
                        style = MaterialTheme.typography.titleLarge,
                        color = IronOnSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = IronOnSurfaceVariant,
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(IronSurfaceHigh)
                        .clickable(onClick = onNotificationsClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Thông báo",
                        tint = IronOnSurface,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(IronSurfaceHigh)
                        .clickable(onClick = onProfileClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Cá nhân",
                        tint = IronOnSurface,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

enum class MainTab(val route: String, val label: String, val icon: ImageVector) {
    HOME("home", "Hôm nay", Icons.Filled.Home),
    ROUTINES("routines", "Giáo án", Icons.Filled.CalendarToday),
    PROGRESS("progress_hub", "Tiến bộ", Icons.AutoMirrored.Filled.TrendingUp),
    PROFILE("profile", "Cá nhân", Icons.Filled.Person),
}

@Composable
fun IronLogBottomBar(
    currentRoute: String,
    hasActiveSession: Boolean,
    onSelectRoute: (String) -> Unit,
    onGhiTapClick: () -> Unit,
) {
    Surface(
        color = IronSurfaceLowest,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .height(72.dp)
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            BottomNavItem(
                icon = MainTab.HOME.icon,
                label = MainTab.HOME.label,
                selected = currentRoute == MainTab.HOME.route,
                onClick = { onSelectRoute(MainTab.HOME.route) },
            )
            BottomNavItem(
                icon = MainTab.ROUTINES.icon,
                label = MainTab.ROUTINES.label,
                selected = currentRoute == MainTab.ROUTINES.route || currentRoute == "library",
                onClick = { onSelectRoute(MainTab.ROUTINES.route) },
            )
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onGhiTapClick)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(NeonPrimary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.FitnessCenter,
                        contentDescription = "Ghi tập",
                        tint = OnNeonPrimary,
                        modifier = Modifier.size(22.dp),
                    )
                    if (hasActiveSession) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-2).dp, y = 2.dp)
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(AlertOrange),
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (hasActiveSession) "Đang tập" else "Ghi tập",
                    style = MaterialTheme.typography.labelSmall,
                    color = IronOnSurface,
                    fontWeight = FontWeight.Bold,
                )
            }
            BottomNavItem(
                icon = MainTab.PROGRESS.icon,
                label = MainTab.PROGRESS.label,
                selected = currentRoute == MainTab.PROGRESS.route,
                onClick = { onSelectRoute(MainTab.PROGRESS.route) },
            )
            BottomNavItem(
                icon = MainTab.PROFILE.icon,
                label = MainTab.PROFILE.label,
                selected = currentRoute == MainTab.PROFILE.route,
                onClick = { onSelectRoute(MainTab.PROFILE.route) },
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tint: Color = if (selected) IronOnSurface else IronOnSurfaceVariant
    Column(
        modifier = Modifier
            .width(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(3.dp)
                .clip(CircleShape)
                .background(if (selected) NeonPrimary else Color.Transparent),
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}
