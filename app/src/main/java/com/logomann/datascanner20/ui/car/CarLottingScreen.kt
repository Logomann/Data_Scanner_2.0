package com.logomann.datascanner20.ui.car

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.logomann.datascanner20.R
import com.logomann.datascanner20.ui.car.view_model.CarLottingViewModel
import com.logomann.datascanner20.ui.screens.CreateButtonsRow
import com.logomann.datascanner20.ui.screens.CreateCameraButton
import com.logomann.datascanner20.ui.screens.CreateVinField
import com.logomann.datascanner20.ui.screens.LoadingScreen
import com.logomann.datascanner20.ui.screens.ScreenState
import com.logomann.datascanner20.ui.snackbar.CreateSnackbarHost
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import com.logomann.datascanner20.ui.theme.yellow
import com.logomann.datascanner20.util.CAMERA_RESULT
import org.koin.androidx.compose.koinViewModel

const val DRIVER_MAXIMUM_SYMBOLS = 4

@Composable
fun CarLottingScreen(
    navController: NavController,
    viewModel: CarLottingViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState()
    val stateErrorFields = viewModel.stateErrorFields.collectAsState()
    val stateErrorList = viewModel.stateErrorList.collectAsState()
    val stateIsClickable = viewModel.stateIsClickable.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val cameraScreenResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>(CAMERA_RESULT)

    fun validateVin(vin: String) {
        viewModel.isErrorVin = vin.length < VIN_MINIMUM_SYMBOLS
    }

    fun validateDriver(driver: String) {
        viewModel.isErrorDriver = driver.length < ROW_MINIMUM_SYMBOLS
    }
    when (val collectState = state.value) {
        is ScreenState.AddressCleared -> {}
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
            viewModel.setDefaultState()
            viewModel.clearFields()
            viewModel.clearList()
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
        validateDriver(viewModel.driver)
        validateVin(viewModel.vin)
        viewModel.setDefaultErrorFieldsState()
    }
    if (stateErrorList.value) {
        viewModel.isErrorMessage = true
        SnackbarMessage(
            message = stringResource(id = R.string.vin_already_in_list),
            snackbarHostState = snackbarHostState,
            scope = scope
        )
        viewModel.setDefaultErrorListState()
    }

    if (!isLoading) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (vinRow, cameraBtn) = createRefs()

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
            val driverRow = createRef()
            CreateVinField(
                text = { viewModel.driver },
                setText = { viewModel.driver = it },
                charMax = DRIVER_MAXIMUM_SYMBOLS,
                charMin = ROW_MINIMUM_SYMBOLS,
                validateVin = { validateDriver(it) },
                isError = viewModel.isErrorDriver,
                name = stringResource(id = R.string.driver),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                trailingIconEndPadding = 0,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .constrainAs(driverRow) {
                        top.linkTo(vinRow.bottom)
                    }
            )
            val btnRow = createRef()
            CreateButtonsRow(
                modifier = Modifier.constrainAs(btnRow) {
                    top.linkTo(driverRow.bottom)
                },
                okBtnName = stringResource(id = R.string.add),
                onClickOk = {
                    if (stateIsClickable.value) {
                        validateVin(viewModel.vin)
                        if (!viewModel.isErrorVin) {
                            viewModel.addToList()
                            viewModel.onVinClicked()
                        }
                    }
                },
                onClickClear = { viewModel.clearFields() })
            val (lazyColumn, updateBtn) = createRefs()
            val listOfVin = mutableListOf<String>()
            listOfVin.addAll(viewModel.lot)
            if (listOfVin.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(lazyColumn) {
                            top.linkTo(btnRow.bottom)
                            bottom.linkTo(updateBtn.top)
                            height = Dimension.fillToConstraints
                        }


                ) {
                    items(listOfVin.size) { index ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = (index + 1).toString())
                            Text(text = listOfVin[index])
                            IconButton(onClick = { viewModel.removeFromList(listOfVin[index]) }) {
                                Icon(Icons.Filled.Delete, contentDescription = null)
                            }
                        }
                    }
                }

                Button(
                    modifier = Modifier.constrainAs(updateBtn) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, 85.dp)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = yellow
                    ),
                    onClick = { viewModel.request() }) {
                    Text(
                        text = stringResource(id = R.string.create_lot),
                        fontSize = 20.sp
                    )
                }
            }


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
fun CarLottingScreenPreview() {
    CarLottingScreen(navController = rememberNavController())
}