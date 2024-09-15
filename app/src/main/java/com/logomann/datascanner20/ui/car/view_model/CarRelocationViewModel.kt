package com.logomann.datascanner20.ui.car.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.screens.ScreenState
import com.logomann.datascanner20.util.CLEAR_CELL_CODE
import com.logomann.datascanner20.util.RELOCATION_CODE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CarRelocationViewModel(private val interactor: ConnectionInteractor) : ViewModel() {
    private lateinit var car: ConnectionModel.Car

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Default)
    val state: StateFlow<ScreenState> = _state

    var vin by mutableStateOf("")
    var isErrorVin by mutableStateOf(false)
    var isErrorField by mutableStateOf(false)
    var isErrorRow by mutableStateOf(false)
    var isErrorCell by mutableStateOf(false)
    var field by mutableStateOf("")
    var row by mutableStateOf("")
    var cell by mutableStateOf("")
    var isErrorMessage by mutableStateOf(false)

    private fun updateData() {
        _state.value = ScreenState.Loading
        interactor.request(car, onComplete = { data, code ->
            if (data == null) {
                _state.value = ScreenState.Loading
            } else if (code == 1) {
                _state.value = ScreenState.Content(data)
            } else {
                _state.value = ScreenState.Error(data)
            }
        })
    }

    fun clearCell() {
        if (!isFieldsError()) {
            _state.value = ScreenState.Loading
            val address =
                ConnectionModel.Address(field.toInt(), row.toInt(), cell.toInt(), CLEAR_CELL_CODE)
            interactor.request(address, onComplete = { data, code ->
                if (data == null) {
                    _state.value = ScreenState.Loading
                } else if (code == 1) {
                    _state.value = ScreenState.AddressCleared(data)
                } else if (code == 2) {
                    _state.value = ScreenState.ServerError
                } else {
                    _state.value = ScreenState.Error(data)
                }
            })
        }
    }

    fun request() {
        isFieldsEmpty()
        if (!isFieldsError() && !isErrorVin) {
            val address = ConnectionModel.Address(field.toInt(), row.toInt(), cell.toInt(), 0)
            car = ConnectionModel.Car(vin, address, RELOCATION_CODE)
            updateData()
        }
    }

    fun clearEditTexts() {
        vin = ""
        field = ""
        row = ""
        cell = ""
        isErrorVin = false
        isErrorField = false
        isErrorRow = false
        isErrorCell = false
    }

    private fun isFieldsEmpty() {
        if (vin.isEmpty()) {
            isErrorVin = true
        }
        if (field.isEmpty()) {
            isErrorField = true
        }
        if (row.isEmpty()) {
            isErrorRow = true
        }
        if (cell.isEmpty()) {
            isErrorCell = true
        }
    }

    fun setDefaultState() {
        _state.value = ScreenState.Default
    }

    private fun isFieldsError(): Boolean {
        return isErrorField || isErrorRow || isErrorCell
    }
}