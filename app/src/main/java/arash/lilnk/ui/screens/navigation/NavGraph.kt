package arash.lilnk.ui.screens.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import arash.lilnk.ui.screens.AboutScreen
import arash.lilnk.ui.screens.HomeScreen
import arash.lilnk.ui.screens.NotesScreen
import arash.lilnk.ui.screens.StartScreen
import arash.lilnk.ui.screens.StatsScreen
import arash.lilnk.ui.screens.WithdrawalsStatsScreen
import arash.lilnk.utilities.Preferences
import arash.lilnk.utilities.Statics
import kotlinx.coroutines.CoroutineScope

@Composable
fun SetupNavGraph(
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
) {
    NavHost(
        navController = navHostController,
        startDestination = if (Preferences[Statics.USER_ID, 0] == 0) Screens.Start.route else Screens.Home.route
    ) {
        composable(route = Screens.Start.route) {
            StartScreen(
                navController = navHostController,
                coroutineScope = coroutineScope,
                snackbarHostState = snackbarHostState
            )
        }
        composable(route = Screens.Home.route) {
            HomeScreen(
                coroutineScope = coroutineScope,
                snackbarHostState = snackbarHostState
            )
        }
        composable(route = Screens.Notes.route) {
            NotesScreen(
                coroutineScope = coroutineScope,
                snackbarHostState = snackbarHostState
            )
        }
        composable(route = Screens.Stats.route) {
            StatsScreen(
                navController = navHostController,
                coroutineScope = coroutineScope,
                snackbarHostState = snackbarHostState
            )
        }
        composable(route = Screens.Withdrawals.route) {
            WithdrawalsStatsScreen(
                navController = navHostController,
                coroutineScope = coroutineScope,
                snackbarHostState = snackbarHostState
            )
        }
        composable(route = Screens.About.route) {
            AboutScreen()
        }
    }
}