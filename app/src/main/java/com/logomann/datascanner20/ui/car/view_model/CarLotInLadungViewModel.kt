package com.logomann.datascanner20.ui.car.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.screens.ScreenState
import com.logomann.datascanner20.util.LOT_IN_LADUNG_CODE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class CarLotInLadungViewModel(private val interactor: ConnectionInteractor) : ViewModel() {

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Default)
    val state: StateFlow<ScreenState> = _state

    private val _stateErrorFields = MutableStateFlow(false)
    val stateErrorFields: StateFlow<Boolean> = _stateErrorFields

    var lot by mutableStateOf("")
    var row by mutableStateOf("")
    var isErrorLot by mutableStateOf(false)
    var isErrorRow by mutableStateOf(false)
    var isErrorMessage by mutableStateOf(false)

    private fun setLot() {
        _state.value = ScreenState.Loading
        interactor.request(ConnectionModel.Lot(
            lot, row,
            LOT_IN_LADUNG_CODE
        ), onComplete = { data, code ->
            if (data == null) {
                _state.value = ScreenState.NoInternet
            } else if (code == 1) {
                _state.value = ScreenState.Content(data)
            } else if (code == 2) {
                _state.value = ScreenState.ServerError
            } else {
                _state.value = ScreenState.Error(data)
            }
        })
    }

    fun request() {
        if (isFieldsEmpty()) {
            _stateErrorFields.value = true
        } else {
            setLot()
        }
    }

    fun setCameraResult(result: String) {
        _state.value = ScreenState.CameraResult(result)
    }

    fun clearFields() {
        lot = ""
        row = ""
        isErrorLot = false
        isErrorRow = false
    }

    private fun isFieldsEmpty(): Boolean {
        return lot.isEmpty() || row.isEmpty()
    }

    fun setDefaultState() {
        _state.value = ScreenState.Default
    }
}