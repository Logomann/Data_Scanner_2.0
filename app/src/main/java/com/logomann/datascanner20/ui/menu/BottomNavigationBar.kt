package com.logomann.datascanner20.ui.menu

import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.logomann.datascanner20.ui.theme.backgroundBottomNav
import com.logomann.datascanner20.ui.theme.primaryLight


@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val screens = listOf(
        BottomNavigationItem.Car,
        BottomNavigationItem.Container
    )
    NavigationBar(
        containerColor = primaryLight
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val context = LocalContext.current

        screens.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                modifier = Modifier.background(primaryLight),
                label = {
                    Text(
                        text = context.getString(screen.titleStringId),
                        color = if (!selected) backgroundBottomNav else Color.White
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(id = screen.iconId),
                        contentDescription = null,
                        tint = if (!selected) backgroundBottomNav else Color.White
                    )
                },
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                selected = currentRoute == screen.route,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = primaryLight
                )
            )
        }

    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar(navController = rememberNavController())
}