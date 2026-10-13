package com.example.gymapp.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gymapp.service.RestTimerController
import com.example.gymapp.ui.home.HomeScreen
import com.example.gymapp.ui.progress.ProgressScreen
import com.example.gymapp.ui.workout.WorkoutScreen

@Composable
fun GymNavHost() {
    val nav = rememberNavController()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!granted) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        RestTimerController.ensureService(context)
    }
    NavHost(navController = nav, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onOpenWorkout = { id -> nav.navigate("workout/$id") { launchSingleTop = true } },
                onOpenProgress = { id -> nav.navigate("progress/$id") },
            )
        }
        composable(
            route = "workout/{sessionId}",
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType }),
        ) { entry ->
            val sessionId = entry.arguments?.getLong("sessionId") ?: return@composable
            WorkoutScreen(
                sessionId = sessionId,
                onBack = { nav.popBackStack() },
                onOpenProgress = { id -> nav.navigate("progress/$id") },
            )
        }
        composable(
            route = "progress/{exerciseId}",
            arguments = listOf(navArgument("exerciseId") { type = NavType.LongType }),
        ) { entry ->
            val exerciseId = entry.arguments?.getLong("exerciseId") ?: return@composable
            ProgressScreen(
                exerciseId = exerciseId,
                onBack = { nav.popBackStack() },
                onOpenWorkout = { id -> nav.navigate("workout/$id") { launchSingleTop = true } },
            )
        }
    }
}
