package com.logomann.datascanner20.ui.container.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.screens.ScreenState
import com.logomann.datascanner20.util.CONTAINER_ARRIVAL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class ContainerArrivalViewModel(private val interactor: ConnectionInteractor) : ViewModel() {
    var containerNumber by mutableStateOf("")
    var field by mutableStateOf("")
    var row by mutableStateOf("")
    var cell by mutableStateOf("")
    var isContainerError by mutableStateOf(false)
    var isErrorField by mutableStateOf(false)
    var isErrorRow by mutableStateOf(false)
    var isErrorCell by mutableStateOf(false)
    var isErrorMessage by mutableStateOf(false)

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Default)
    val state: StateFlow<ScreenState> = _state

    private fun setContainerAddress() {
        _state.value = ScreenState.Loading
        interactor.request(
            ConnectionModel.Container(
                containerNumber,
                ConnectionModel.Address(field.toInt(), row.toInt(), cell.toInt(), 0),
                CONTAINER_ARRIVAL,
                null
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
        isFieldsEmpty()
        if (!isError()) {
            setContainerAddress()
        }
    }

    fun setDefaultState() {
        _state.value = ScreenState.Default
    }

    private fun isFieldsEmpty() {
        if (containerNumber.isEmpty()) {
            isContainerError = true
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

    private fun isError(): Boolean {
        return isContainerError || isErrorRow || isErrorCell || isErrorField
    }

    fun clearFields() {
        containerNumber = ""
        field = ""
        row = ""
        cell = ""
        isContainerError = false
        isErrorField = false
        isErrorRow = false
        isErrorCell = false
    }

}