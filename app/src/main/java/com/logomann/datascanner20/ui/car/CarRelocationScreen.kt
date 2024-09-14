package com.logomann.datascanner20.ui.car

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.logomann.datascanner20.R
import com.logomann.datascanner20.ui.screens.ScreenState
import com.logomann.datascanner20.ui.screens.CreateButtonsRow
import com.logomann.datascanner20.ui.screens.CreateCameraButton
import com.logomann.datascanner20.ui.screens.CreateCompoundField
import com.logomann.datascanner20.ui.screens.CreateVinField
import com.logomann.datascanner20.ui.screens.LoadingScreen
import com.logomann.datascanner20.ui.snackbar.CreateSnackbarHost
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import com.logomann.datascanner20.ui.theme.yellow
import com.logomann.datascanner20.util.CAMERA_RESULT
import org.koin.androidx.compose.koinViewModel
import com.logomann.datascanner20.ui.car.view_model.CarRelocationViewModel as CarRelocationViewModel1

const val VIN_MINIMUM_SYMBOLS = 17
const val ROW_MINIMUM_SYMBOLS = 1

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CarRelocationScreen(
    navController: NavController,
    viewModel: CarRelocationViewModel1 = koinViewModel()
) {
    val state = viewModel.state.collectAsState()
    val stateErrorFields = viewModel.stateErrorFields.collectAsState()
    val stateErrorVin = viewModel.stateErrorVin.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val cameraScreenResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>(CAMERA_RESULT)


    fun validateVin(vin: String) {
        viewModel.isErrorVin = vin.length < VIN_MINIMUM_SYMBOLS
    }

    fun validateField(field: String) {
        viewModel.isErrorField = field.length < ROW_MINIMUM_SYMBOLS
    }

    fun validateRow(row: String) {
        viewModel.isErrorRow = row.length < ROW_MINIMUM_SYMBOLS
    }

    fun validateCell(cell: String) {
        viewModel.isErrorCell = cell.length < ROW_MINIMUM_SYMBOLS
    }
    when (val collectState = state.value) {
        is ScreenState.AddressCleared -> {
            viewModel.isErrorMessage = false
            isLoading = false
            SnackbarMessage(
                message = collectState.message.toString(),
                snackbarHostState = snackbarHostState,
                scope = scope
            )
            viewModel.setDefaultState()
        }

        is ScreenState.CameraResult -> {
            validateVin(collectState.result)
            viewModel.setDefaultState()
        }

        is ScreenState.Content -> {
            viewModel.isErrorMessage = false
            isLoading = false
            SnackbarMessage(
                message = collectState.message.toString(),
                snackbarHostState = snackbarHostState,
                scope = scope
            )
            viewModel.clearEditTexts()
            viewModel.setDefaultState()
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
    if (stateErrorFields.value) {
        validateField(viewModel.field)
        validateRow(viewModel.row)
        validateCell(viewModel.cell)
    } else {
        viewModel.isErrorField = false
        viewModel.isErrorRow = false
        viewModel.isErrorCell = false
    }
    if (stateErrorVin.value) {
        validateVin(viewModel.vin)
    } else {
        viewModel.isErrorVin = false
    }

    if (!isLoading) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (vinRow, cameraBtn, editTexts, box) = createRefs()

            if (cameraScreenResult?.isNotEmpty() == true) {
                viewModel.setCameraResult(cameraScreenResult.toString())
                navController.currentBackStackEntry!!.savedStateHandle.remove<String>(CAMERA_RESULT)
            }
            CreateVinField(
                text = { viewModel.vin },
                setText = { viewModel.vin = it },
                charMax = VIN_MINIMUM_SYMBOLS,
                charMin = VIN_MINIMUM_SYMBOLS,
                validateVin = { validateVin(it) },
                isError = viewModel.isErrorVin,
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.vin_row_top_padding))
                    .constrainAs(vinRow) {
                    }
            )

            CreateCameraButton(
                navController = navController,
                modifier = Modifier.constrainAs(cameraBtn) {
                    top.linkTo(vinRow.top)
                    end.linkTo(parent.end)
                })

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .constrainAs(editTexts) {
                        top.linkTo(vinRow.bottom)
                    }
                    .fillMaxWidth()
                    .padding(top = 16.dp)
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
                    top.linkTo(editTexts.bottom)
                },
                onClickOk = { viewModel.request() },
                onClickClear = { viewModel.clearEditTexts() })

            val clearBtn = createRef()
            Button(
                onClick = { viewModel.clearCell() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = yellow
                ),
                modifier = Modifier
                    .padding(top = 24.dp)
                    .constrainAs(clearBtn) {
                        top.linkTo(btnRow.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text(
                    text = stringResource(id = R.string.clear_cell),
                    fontSize = 20.sp
                )
            }
            CreateSnackbarHost(
                snackbarHostState = snackbarHostState,
                viewModel.isErrorMessage,
                modifier = Modifier
                    .constrainAs(box) {
                        bottom.linkTo(parent.bottom, 100.dp)
                    }
            )
        }
    }
}


