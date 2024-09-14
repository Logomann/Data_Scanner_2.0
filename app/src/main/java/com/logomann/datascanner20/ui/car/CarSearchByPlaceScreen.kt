package com.logomann.datascanner20.ui.car

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.logomann.datascanner20.R
import com.logomann.datascanner20.ui.car.view_model.CarSearchByPlaceViewModel
import com.logomann.datascanner20.ui.screens.CreateButtonsRow
import com.logomann.datascanner20.ui.screens.CreateCompoundField
import com.logomann.datascanner20.ui.screens.LoadingScreen
import com.logomann.datascanner20.ui.screens.ScreenState
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import com.logomann.datascanner20.ui.theme.green
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun CarSearchByPlaceScreen(
    viewModel: CarSearchByPlaceViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState()
    val stateErrorFields = viewModel.stateErrorFields.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
        is ScreenState.AddressCleared -> {}
        is ScreenState.CameraResult -> {}
        is ScreenState.Content -> {
            viewModel.isErrorMessage = false
            isLoading = false
            val actionName = stringResource(id = R.string.ok)
            LaunchedEffect(Unit) {
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = collectState.message.toString(),
                        actionLabel = actionName
                    )
                    when (result) {
                        SnackbarResult.Dismissed -> {}
                        SnackbarResult.ActionPerformed -> {}
                    }
                }
            }
            viewModel.setDefaultState()
            viewModel.clearEditTexts()
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

    if (!isLoading) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val row = createRef()
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(id = R.dimen.vin_row_top_padding))
                    .constrainAs(row) {
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
                    top.linkTo(row.bottom)
                },
                onClickOk = { viewModel.request() },
                onClickClear = { viewModel.clearEditTexts() })
            val snack = createRef()
            CreateSnackbarHostForAction(
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

@Composable
fun CreateSnackbarHostForAction(
    snackbarHostState: SnackbarHostState,
    isError: Boolean,
    modifier: Modifier
) {
    SnackbarHost(
        snackbarHostState,
        modifier = modifier
            .fillMaxWidth()
    ) { data ->
        Snackbar(
            containerColor = if (isError) MaterialTheme.colorScheme.error else green,
            modifier = Modifier.padding(16.dp),
            content = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = data.visuals.message,
                        color = Color.White,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    )
                    if(!isError) {
                        TextButton(
                            onClick = { snackbarHostState.currentSnackbarData?.dismiss() }) {
                            Text(
                                color = Color.White,
                                text = data.visuals.actionLabel.toString()
                            )
                        }
                    }

                }
            })
    }
}