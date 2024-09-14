package com.logomann.datascanner20.ui.screens

sealed class Screen(val route: String) {
    data object Authorization : Screen(route = "authorization")
    data object Settings : Screen(route = "settings")
    data object Car : Screen(route = "car")
    data object Container : Screen(route = "container")
    data object Loading : Screen(route = "loading")
    data object Camera : Screen(route = "camera")
}