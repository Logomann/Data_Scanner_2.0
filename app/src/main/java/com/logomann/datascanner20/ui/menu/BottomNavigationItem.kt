package com.logomann.datascanner20.ui.menu

import com.logomann.datascanner20.R
import com.logomann.datascanner20.ui.screens.Screen

sealed class BottomNavigationItem(
    val route: String,
    val iconId: Int,
    val titleStringId: Int
) {
    data object Car : BottomNavigationItem(
        Screen.Car.route,
        iconId = R.drawable.car,
        R.string.car_operations
    )

    data object Container : BottomNavigationItem(
        Screen.Container.route,
        iconId = R.drawable.train_car_container,
        R.string.container_operations
    )
}



