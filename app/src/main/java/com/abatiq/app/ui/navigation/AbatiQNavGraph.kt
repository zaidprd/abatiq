package com.abatiq.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abatiq.app.AbatiQApplication
import com.abatiq.app.data.domain.ProgressSummary
import com.abatiq.app.ui.game.GameScreen
import com.abatiq.app.ui.game.GameViewModel
import com.abatiq.app.ui.home.HomeScreen
import com.abatiq.app.ui.result.ResultScreen
import com.abatiq.app.ui.settings.ParentalGateScreen
import com.abatiq.app.ui.settings.SettingsScreen
import com.abatiq.app.ui.settings.SettingsViewModel
import kotlin.random.Random

@Composable
fun AbatiQNavGraph(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val app = context.applicationContext as AbatiQApplication

    val navController = rememberNavController()

    // Ensure the audio engine is shut down when the root leaves the tree.
    DisposableEffect(app.gameAudioPlayer) {
        onDispose { app.gameAudioPlayer.shutdown() }
    }

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = Destination.Home.route) {
            composable(Destination.Home.route) {
                HomeScreen(
                    viewModel = viewModel(
                        factory = viewModelFactory {
                            com.abatiq.app.ui.home.HomeViewModel(
                                progressRepository = app.progressRepository,
                                audioPlayer = app.gameAudioPlayer,
                            )
                        }
                    ),
                    onStartGame = {
                        navController.navigate(Destination.Game.route)
                    },
                    onOpenSettings = {
                        navController.navigate(Destination.ParentalGate.route)
                    },
                )
            }

            composable(Destination.Game.route) {
                val vm: GameViewModel = viewModel(
                    factory = viewModelFactory {
                        GameViewModel(
                            gameRepository = app.gameRepository,
                            audioPlayer = app.gameAudioPlayer,
                            random = Random.Default,
                        )
                    }
                )
                GameScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onSessionComplete = { result ->
                        navController.navigate(
                            Destination.Result.build(
                                score = result.score,
                                correctTaps = result.correctTaps,
                                totalTaps = result.totalTaps,
                            ),
                        ) {
                            popUpTo(Destination.Home.route) { inclusive = false }
                        }
                    },
                )
            }

            composable(
                route = Destination.Result.route,
                arguments = listOf(
                    navArgument(Destination.Result.ARG_SCORE) { type = NavType.IntType },
                    navArgument(Destination.Result.ARG_CORRECT) { type = NavType.IntType },
                    navArgument(Destination.Result.ARG_TOTAL) { type = NavType.IntType },
                ),
            ) { backStack ->
                val score = backStack.arguments?.getInt(Destination.Result.ARG_SCORE) ?: 0
                val correct = backStack.arguments?.getInt(Destination.Result.ARG_CORRECT) ?: 0
                val total = backStack.arguments?.getInt(Destination.Result.ARG_TOTAL) ?: 0
                val best by app.progressRepository.summary.collectAsStateWithLifecycle(
                    initialValue = ProgressSummary.Empty,
                )
                ResultScreen(
                    score = score,
                    correctTaps = correct,
                    totalTaps = total,
                    bestScore = best.bestScore,
                    onPlayAgain = {
                        navController.navigate(Destination.Game.route) {
                            popUpTo(Destination.Home.route) { inclusive = false }
                        }
                    },
                    onHome = {
                        navController.popBackStack(Destination.Home.route, inclusive = false)
                    },
                )
            }

            composable(Destination.ParentalGate.route) {
                ParentalGateScreen(
                    onUnlocked = {
                        navController.navigate(Destination.Settings.route) {
                            popUpTo(Destination.ParentalGate.route) { inclusive = true }
                        }
                    },
                    onCancel = { navController.popBackStack() },
                )
            }

            composable(Destination.Settings.route) {
                val settingsVm: SettingsViewModel = viewModel(
                    factory = viewModelFactory {
                        SettingsViewModel(
                            preferences = app.preferences,
                            gameRepository = app.gameRepository,
                            audioPlayer = app.gameAudioPlayer,
                        )
                    }
                )
                SettingsScreen(
                    viewModel = settingsVm,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

/** Tiny factory builder so each call site stays one-screen-readable. We avoid
 *  Hilt in Phase 1 because every ViewModel takes at most three collaborators
 *  and we want zero kapt/ksp magic beyond Room. */
inline fun <reified T : androidx.lifecycle.ViewModel> viewModelFactory(
    crossinline create: () -> T,
): androidx.lifecycle.ViewModelProvider.Factory =
    object : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <V : androidx.lifecycle.ViewModel> create(modelClass: Class<V>): V {
            require(modelClass.isAssignableFrom(T::class.java))
            return create() as V
        }
    }
