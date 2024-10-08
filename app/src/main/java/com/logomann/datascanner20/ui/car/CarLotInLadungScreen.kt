package com.logomann.datascanner20.ui.car

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.logomann.datascanner20.R
import com.logomann.datascanner20.ui.car.view_model.CarLotInLadungViewModel
import com.logomann.datascanner20.ui.screens.CreateButtonsRow
import com.logomann.datascanner20.ui.screens.CreateCameraButton
import com.logomann.datascanner20.ui.screens.CreateVinField
import com.logomann.datascanner20.ui.screens.LoadingScreen
import com.logomann.datascanner20.ui.screens.ScreenState
import com.logomann.datascanner20.ui.snackbar.CreateSnackbarHost
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import com.logomann.datascanner20.util.CAMERA_RESULT
import org.koin.androidx.compose.koinViewModel

const val FIELD_LOT_MAXIMUM_SYMBOLS = 25
const val FIELD_ROW_MAXIMUS_SYMBOLS = 2

@Composable
fun CarLotInLadungScreen(
    navController: NavController,
    viewModel: CarLotInLadungViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val cameraScreenResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>(CAMERA_RESULT)

    fun validateLot(lot: String) {
        viewModel.isErrorLot = lot.length < ROW_MINIMUM_SYMBOLS
    }

    fun validateRow(row: String) {
        viewModel.isErrorRow = row.length < ROW_MINIMUM_SYMBOLS
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
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (lotRow, cameraBtn, row) = createRefs()

            if (cameraScreenResult?.isNotEmpty() == true) {
                viewModel.lot = cameraScreenResult.toString()
                navController.currentBackStackEntry!!.savedStateHandle.remove<String>(CAMERA_RESULT)
                validateLot(viewModel.lot)
            }
            CreateVinField(
                text = { viewModel.lot },
                setText = { viewModel.lot = it },
                charMax = FIELD_LOT_MAXIMUM_SYMBOLS,
                validateVin = { validateLot(it) },
                isError = viewModel.isErrorLot,
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.vin_row_top_padding))
                    .constrainAs(lotRow) {
                    },
                name = stringResource(id = R.string.lot)
            )
            CreateCameraButton(
                navController = navController,
                modifier = Modifier.constrainAs(cameraBtn) {
                    top.linkTo(lotRow.top)
                    end.linkTo(parent.end)
                })
            CreateVinField(
                text = { viewModel.row },
                setText = { viewModel.row = it },
                charMax = FIELD_ROW_MAXIMUS_SYMBOLS,
                validateVin = { validateRow(it) },
                isError = viewModel.isErrorRow,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .constrainAs(row) {
                        top.linkTo(lotRow.bottom)
                    },
                trailingIconEndPadding = 0,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                name = stringResource(id = R.string.row)
            )
            val btnRow = createRef()
            CreateButtonsRow(
                modifier = Modifier.constrainAs(btnRow) {
                    top.linkTo(row.bottom)
                },
                onClickOk = { viewModel.request() },
                onClickClear = { viewModel.clearFields() })
            val snack = createRef()
            CreateSnackbarHost(
                snackbarHostState = snackbarHostState,
                viewModel.isErrorMessage,
                modifier = Modifier
                    .constrainAs(snack) {
                        bottom.linkTo(parent.bottom, 100.dp)
                    }
            )
        }
    }

}

@Preview
@Composable
fun CarLotInLadungScreenPreview() {
    CarLotInLadungScreen(rememberNavController())
}