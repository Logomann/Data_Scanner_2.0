package com.logomann.datascanner20.ui.authorization

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.logomann.datascanner20.R
import com.logomann.datascanner20.ui.screens.ScreenState
import com.logomann.datascanner20.ui.authorization.view_model.AuthorizationViewModel
import com.logomann.datascanner20.ui.snackbar.CreateSnackbarHost
import com.logomann.datascanner20.ui.screens.LoadingScreen
import com.logomann.datascanner20.ui.screens.Screen
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Authorization(
    navController: NavController,
    viewModel: AuthorizationViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    DisposableEffect(Unit) {
        onDispose {
            if (viewModel.pinCode.isNotEmpty()) {
                viewModel.isResumed = true
            }
        }
    }
    val currentSnackbar = remember { SnackbarHostState() }
    var isLoading = false
    val scopeCurrent = rememberCoroutineScope()
    val state = viewModel.state.collectAsState()

    when (val collectState = state.value) {
        is ScreenState.AddressCleared -> {}
        is ScreenState.CameraResult -> {}
        is ScreenState.Content -> {
            viewModel.isErrorMessage = false
            isLoading = false
            viewModel.pinCode = ""
            SnackbarMessage(
                message = collectState.message.toString(),
                snackbarHostState = snackbarHostState,
                scope = scope
            )
           viewModel.setDefaultState()
            navController.navigate(Screen.Car.route)
        }

        ScreenState.Default -> {
        }

        is ScreenState.Error -> {
            viewModel.isErrorMessage = true
            isLoading = false
            SnackbarMessage(
                message = collectState.message.toString(),
                snackbarHostState = currentSnackbar,
                scope = scopeCurrent
            )
            viewModel.isResumed = true
            viewModel.setDefaultState()
        }

        ScreenState.Loading -> {
            isLoading = true
            LoadingScreen()
        }

        ScreenState.NoInternet -> {
            viewModel.isErrorMessage = true
            isLoading = false
            SnackbarMessage(
                message = stringResource(id = R.string.nointernet),
                snackbarHostState = currentSnackbar,
                scope = scopeCurrent
            )
            viewModel.isResumed = true
            viewModel.setDefaultState()
        }

        ScreenState.ServerError -> {
            viewModel.isErrorMessage = true
            isLoading = false
            SnackbarMessage(
                message = stringResource(id = R.string.server_error),
                snackbarHostState = currentSnackbar,
                scope = scopeCurrent
            )
            viewModel.isResumed = true
            viewModel.setDefaultState()
        }
    }

    if (!isLoading) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (authorName, pinCodeInput, refreshBtn, settingBtn) = createRefs()
            Text(
                text = stringResource(id = R.string.authorization),
                fontSize = 26.sp,
                modifier = Modifier.constrainAs(authorName) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )
            TextField(
                value = viewModel.pinCode,
                onValueChange = {
                    if (it.length <= 4) {
                        viewModel.pinCode = it
                        viewModel.validateField()
                        if (it.length == 4) {
                            viewModel.authorize()
                        }
                    }
                },
                isError = viewModel.isErrorPin,
                supportingText = {
                    if (viewModel.isErrorPin) {
                        Text(text = stringResource(R.string.pin_can_t_be_empty))
                    }
                },
                label = { Text(text = stringResource(id = R.string.input_pin)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                modifier = Modifier
                    .constrainAs(pinCodeInput) {
                        top.linkTo(authorName.bottom, 16.dp)
                        start.linkTo(authorName.start)
                        end.linkTo(authorName.end)
                        width = Dimension.fillToConstraints

                    })
            if (viewModel.isResumed) {
                IconButton(
                    modifier = Modifier.constrainAs(refreshBtn) {
                        top.linkTo(pinCodeInput.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    onClick = {
                        viewModel.authorize()
                        viewModel.isResumed = false
                    }
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                }
            }

            IconButton(
                onClick = {
                    navController.navigate(Screen.Settings.route)
                },
                modifier = Modifier.constrainAs(settingBtn) {
                    bottom.linkTo(parent.bottom, 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            ) {
                Icon(Icons.Filled.Settings, contentDescription = null)
            }
            val snack = createRef()
            CreateSnackbarHost(
                snackbarHostState = currentSnackbar,
                viewModel.isErrorMessage,
                modifier = Modifier
                    .constrainAs(snack) {
                        bottom.linkTo(settingBtn.top)
                    }
            )
        }
    }
}






