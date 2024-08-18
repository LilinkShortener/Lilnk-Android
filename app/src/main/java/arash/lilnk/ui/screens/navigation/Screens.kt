package arash.lilnk.ui.screens.navigation

sealed class Screens(val route: String) {
    data object Start : Screens(route = "startScreen")
    data object Home : Screens(route = "homeScreen")
    data object Stats : Screens(route = "statsScreen")
    data object Withdrawals : Screens(route = "withdrawalScreen")
}
