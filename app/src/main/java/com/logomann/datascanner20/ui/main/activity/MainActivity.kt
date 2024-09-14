package com.logomann.datascanner20.ui.main.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.logomann.datascanner20.ui.authorization.Authorization
import com.logomann.datascanner20.ui.main.BaseActivity
import com.logomann.datascanner20.ui.menu.BottomNavigationBar
import com.logomann.datascanner20.ui.camera.Camera
import com.logomann.datascanner20.ui.car.CarMainScreen
import com.logomann.datascanner20.ui.container.ContainerMainScreen
import com.logomann.datascanner20.ui.snackbar.CreateSnackbarHost
import com.logomann.datascanner20.ui.screens.LoadingScreen
import com.logomann.datascanner20.ui.screens.Screen
import com.logomann.datascanner20.ui.settings.Settings
import com.logomann.datascanner20.ui.theme.DataScanner20Theme

class MainActivity : BaseActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state = stateIsCameraReady.collectAsState()
            val navController = rememberNavController()
            var bottomBarVisibility by remember { mutableStateOf(true) }
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            DataScanner20Theme(dynamicColor = false) {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    snackbarHost = {
                        CreateSnackbarHost(
                            snackbarHostState = snackbarHostState,
                            isError = false,
                            modifier = Modifier
                        )
                    },
                    bottomBar = { if (bottomBarVisibility) BottomNavigationBar(navController) }
                ) { _ ->
                    NavHost(navController, startDestination = Screen.Authorization.route) {
                        composable(Screen.Authorization.route) {
                            bottomBarVisibility = false
                            Authorization(
                                navController,
                                snackbarHostState = snackbarHostState,
                                scope = scope
                            )
                        }
                        composable(Screen.Settings.route) {
                            bottomBarVisibility = false
                            Settings(navController)
                        }

                        composable(Screen.Car.route) {
                            bottomBarVisibility = true
                            CarMainScreen(navController)
                        }
                        composable(Screen.Container.route) {
                            bottomBarVisibility = true
                            ContainerMainScreen(navController)
                        }
                        composable(Screen.Loading.route) {
                            bottomBarVisibility = false
                            LoadingScreen()
                        }
                        composable(Screen.Camera.route) {
                            if (state.value) {
                                bottomBarVisibility = false
                                Camera(navController)
                            } else {
                                requestPermissions()
                            }
                        }
                    }
                }
            }
        }
    }
}
