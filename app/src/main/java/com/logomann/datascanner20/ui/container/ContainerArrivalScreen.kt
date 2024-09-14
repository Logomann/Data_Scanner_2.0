package com.logomann.datascanner20.ui.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.logomann.datascanner20.R
import com.logomann.datascanner20.ui.container.view_model.ContainerArrivalViewModel
import com.logomann.datascanner20.ui.screens.CreateButtonsRow
import com.logomann.datascanner20.ui.screens.CreateCompoundField
import com.logomann.datascanner20.ui.screens.CreateVinField
import com.logomann.datascanner20.ui.screens.LoadingScreen
import com.logomann.datascanner20.ui.screens.ScreenState
import com.logomann.datascanner20.ui.snackbar.CreateSnackbarHost
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import org.koin.androidx.compose.koinViewModel

const val CONTAINER_NUMBER_LENGTH = 11

@Composable
fun ContainerArrivalScreen(
    viewModel: ContainerArrivalViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun validateContainer(container: String) {
        viewModel.isContainerError = container.length < CONTAINER_NUMBER_LENGTH
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

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!isLoading) {
            val (container, row) = createRefs()
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
                    .constrainAs(container) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    })
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .constrainAs(row) {
                        top.linkTo(container.bottom)
                    }
            ) {
                CreateCompoundField(
                    text = { viewModel.field },
                    setText = { viewModel.field = it },
                    charMax = 2,
                    isError = viewModel.isErrorField,
                    setError = { viewModel.isErrorField = it },
                    modifier = Modifier,
                    name = stringResource(id = R.string.field)
                )
                CreateCompoundField(
                    text = { viewModel.row },
                    setText = { viewModel.row = it },
                    charMax = 3,
                    isError = viewModel.isErrorRow,
                    setError = { viewModel.isErrorRow = it },
                    modifier = Modifier,
                    name = stringResource(id = R.string.row)
                )
                CreateCompoundField(
                    text = { viewModel.cell },
                    setText = { viewModel.cell = it },
                    charMax = 2,
                    isError = viewModel.isErrorCell,
                    setError = { viewModel.isErrorCell = it },
                    modifier = Modifier,
                    name = stringResource(id = R.string.cell)
                )
            }
            val btnRow = createRef()
            CreateButtonsRow(
                modifier = Modifier.constrainAs(btnRow) {
                    top.linkTo(row.bottom, 12.dp)
                },
                onClickOk = { viewModel.request() },
                onClickClear = { viewModel.clearFields() })
            val snack = createRef()
            CreateSnackbarHost(
                snackbarHostState = snackbarHostState,
                viewModel.isErrorMessage,
                modifier = Modifier.constrainAs(snack) {
                    bottom.linkTo(parent.bottom, 100.dp)
                }
            )
        }

    }
}

@Preview
@Composable
fun ContainerPreview() {
    ContainerArrivalScreen()
}