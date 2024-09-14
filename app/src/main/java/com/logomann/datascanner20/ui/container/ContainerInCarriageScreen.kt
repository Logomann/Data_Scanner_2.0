package com.logomann.datascanner20.ui.container

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.logomann.datascanner20.R
import com.logomann.datascanner20.ui.screens.ScreenState
import com.logomann.datascanner20.ui.container.view_model.ContainerInCarriageViewModel
import com.logomann.datascanner20.ui.screens.CreateButtonsRow
import com.logomann.datascanner20.ui.screens.CreateVinField
import com.logomann.datascanner20.ui.screens.LoadingScreen
import com.logomann.datascanner20.ui.snackbar.CreateSnackbarHost
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import org.koin.androidx.compose.koinViewModel

const val WAGON_NUMBER_LENGTH = 8

@Composable
fun ContainerInCarriage(
    viewModel: ContainerInCarriageViewModel = koinViewModel()
) {

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val state = viewModel.state.collectAsState()
        var isLoading by rememberSaveable { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        fun validateContainer(container: String) {
            viewModel.isContainerError = container.length < CONTAINER_NUMBER_LENGTH
        }

        fun validateWagon(wagon: String) {
            viewModel.isWagonError = wagon.length < WAGON_NUMBER_LENGTH
        }
        when (val collectState = state.value) {
            is ScreenState.AddressCleared -> {}
            is ScreenState.CameraResult -> {}
            is ScreenState.Content -> {
                viewModel.isErrorMessage = false
                isLoading = false
                SnackbarMessage(
                    message = collectState.message.toString(),
                    snackbarHostState = snackbarHostState,
                    scope = scope
                )
                viewModel.setDefaultState()
                viewModel.clearFields()
            }

            ScreenState.Default -> {}
            is ScreenState.Error -> {
                viewModel.isErrorMessage = true
                isLoading = false
                SnackbarMessage(
                    message = collectState.message.toString(),
                    snackbarHostState = snackbarHostState,
                    scope = scope
                )
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
                    snackbarHostState = snackbarHostState,
                    scope = scope
                )
                viewModel.setDefaultState()
            }

            ScreenState.ServerError -> {
                viewModel.isErrorMessage = true
                isLoading = false
                SnackbarMessage(
                    message = stringResource(id = R.string.server_error),
                    snackbarHostState = snackbarHostState,
                    scope = scope
                )
                viewModel.setDefaultState()
            }
        }

        if (!isLoading) {
            val (vin, container) = createRefs()
            CreateVinField(
                text = { viewModel.containerNumber },
                setText = { viewModel.containerNumber = it },
                charMax = CONTAINER_NUMBER_LENGTH,
                charMin = CONTAINER_NUMBER_LENGTH,
                validateVin = { validateContainer(it) },
                name = stringResource(id = R.string.enter_container_number),
                isError = viewModel.isContainerError,
                trailingIconEndPadding = 0,
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.vin_row_top_padding))
                    .constrainAs(vin) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    })
            CreateVinField(
                text = { viewModel.wagonNumber },
                setText = { viewModel.wagonNumber = it },
                charMax = WAGON_NUMBER_LENGTH,
                charMin = WAGON_NUMBER_LENGTH,
                validateVin = { validateWagon(it) },
                name = stringResource(id = R.string.wagon),
                isError = viewModel.isWagonError,
                trailingIconEndPadding = 0,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .constrainAs(container) {
                        top.linkTo(vin.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    })
            val btnRow = createRef()
            CreateButtonsRow(
                modifier = Modifier.constrainAs(btnRow) {
                    top.linkTo(container.bottom, 12.dp)
                },
                onClickOk = { viewModel.request() },
                onClickClear = { viewModel.clearFields() })
            val snack = createRef()
            CreateSnackbarHost(
                snackbarHostState = snackbarHostState,
                viewModel.isErrorMessage,
                modifier = Modifier.constrainAs(snack) {
                    bottom.linkTo(parent.bottom, 100.dp)
                })

        }
    }

}