package com.example.gymapp.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gymapp.service.RestPhase
import com.example.gymapp.service.RestTimerBus
import com.example.gymapp.service.RestTimerController
import com.example.gymapp.ui.components.ActiveRestBanner
import com.example.gymapp.ui.components.IronLogBottomBar
import com.example.gymapp.ui.components.IronLogTopBar
import com.example.gymapp.ui.components.MainTab
import com.example.gymapp.ui.home.HomeScreen
import com.example.gymapp.ui.home.HomeViewModel
import com.example.gymapp.ui.profile.ProfileScreen
import com.example.gymapp.ui.progress.ProgressHubScreen
import com.example.gymapp.ui.progress.ProgressScreen
import com.example.gymapp.ui.routines.ExerciseDetailScreen
import com.example.gymapp.ui.routines.RoutinesAndLibraryScreen
import com.example.gymapp.ui.theme.IronSurface
import com.example.gymapp.ui.workout.WorkoutScreen
import kotlinx.coroutines.launch

@Composable
fun GymNavHost() {
    val nav = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val homeViewModel: HomeViewModel = viewModel(
        factory = appViewModelFactory { app -> HomeViewModel(app) },
    )
    val homeState by homeViewModel.state.collectAsStateWithLifecycle()
    val restPhase by RestTimerBus.phase.collectAsStateWithLifecycle()

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

    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: MainTab.HOME.route

    val isMainTabScreen = currentRoute in setOf(
        MainTab.HOME.route,
        MainTab.ROUTINES.route,
        "library",
        MainTab.PROGRESS.route,
        MainTab.PROFILE.route,
    )

    val topSubtitle = when (currentRoute) {
        MainTab.HOME.route -> "Hôm Nay"
        MainTab.ROUTINES.route, "library" -> "Giáo Án & Kho Bài"
        MainTab.PROGRESS.route -> "Tiến Bộ"
        MainTab.PROFILE.route -> "Cá Nhân"
        else -> "Hypertrophy"
    }

    fun selectMainTab(route: String) {
        nav.navigate(route) {
            popUpTo(nav.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IronSurface),
    ) {
        if (isMainTabScreen) {
            IronLogTopBar(
                subtitle = topSubtitle,
                onNotificationsClick = { selectMainTab(MainTab.PROGRESS.route) },
                onProfileClick = { selectMainTab(MainTab.PROFILE.route) },
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            NavHost(
                navController = nav,
                startDestination = MainTab.HOME.route,
            ) {
                composable(MainTab.HOME.route) {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onOpenWorkout = { id -> nav.navigate("workout/$id") { launchSingleTop = true } },
                        onOpenProgress = { id -> nav.navigate("progress/$id") },
                        onOpenExerciseDetail = { id -> nav.navigate("exercise/$id") },
                        onOpenRoutines = { selectMainTab(MainTab.ROUTINES.route) },
                        onOpenLibrary = {
                            nav.navigate("library") { launchSingleTop = true }
                        },
                    )
                }

                composable(MainTab.ROUTINES.route) {
                    RoutinesAndLibraryScreen(
                        homeViewModel = homeViewModel,
                        initialTab = "routines",
                        onOpenWorkout = { id -> nav.navigate("workout/$id") { launchSingleTop = true } },
                        onOpenExerciseDetail = { id -> nav.navigate("exercise/$id") },
                    )
                }

                composable("library") {
                    RoutinesAndLibraryScreen(
                        homeViewModel = homeViewModel,
                        initialTab = "library",
                        onOpenWorkout = { id -> nav.navigate("workout/$id") { launchSingleTop = true } },
                        onOpenExerciseDetail = { id -> nav.navigate("exercise/$id") },
                    )
                }

                composable(MainTab.PROGRESS.route) {
                    ProgressHubScreen(
                        homeState = homeState,
                        onOpenOverloadDetail = { id -> nav.navigate("progress/$id") },
                        onOpenWorkout = { id -> nav.navigate("workout/$id") { launchSingleTop = true } },
                    )
                }

                composable(MainTab.PROFILE.route) {
                    ProfileScreen(
                        totalSessionsCount = homeState.recent.size + (if (homeState.active != null) 1 else 0),
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
                        onApplyToWorkout = { exName ->
                            scope.launch {
                                val sessionId = homeViewModel.startRoutineDay(listOf(exName))
                                nav.navigate("workout/$sessionId") { launchSingleTop = true }
                            }
                        },
                    )
                }

                composable(
                    route = "exercise/{exerciseId}",
                    arguments = listOf(navArgument("exerciseId") { type = NavType.LongType }),
                ) { entry ->
                    val exerciseId = entry.arguments?.getLong("exerciseId") ?: return@composable
                    ExerciseDetailScreen(
                        exerciseId = exerciseId,
                        onBack = { nav.popBackStack() },
                        onStartWithExercise = { exName ->
                            scope.launch {
                                val sessionId = homeViewModel.startRoutineDay(listOf(exName))
                                nav.navigate("workout/$sessionId") { launchSingleTop = true }
                            }
                        },
                        onOpenOverloadDetail = { id -> nav.navigate("progress/$id") },
                    )
                }
            }
        }

        if (isMainTabScreen && restPhase !is RestPhase.Idle) {
            ActiveRestBanner(
                onOpenSession = { id -> nav.navigate("workout/$id") { launchSingleTop = true } },
            )
        }

        if (isMainTabScreen) {
            IronLogBottomBar(
                currentRoute = currentRoute,
                hasActiveSession = homeState.active != null,
                onSelectRoute = { route -> selectMainTab(route) },
                onGhiTapClick = {
                    scope.launch {
                        val sessionId = homeViewModel.startOrResume()
                        nav.navigate("workout/$sessionId") { launchSingleTop = true }
                    }
                },
            )
        }
    }
}
