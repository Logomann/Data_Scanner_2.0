package com.logomann.datascanner20.ui.car.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.screens.ScreenState
import com.logomann.datascanner20.util.SEARCH_BY_PLACE_CODE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class CarSearchByPlaceViewModel(private val interactor: ConnectionInteractor) : ViewModel() {

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Default)
    val state: StateFlow<ScreenState> = _state

    var isErrorMessage by mutableStateOf(false)
    var isErrorField by mutableStateOf(false)
    var isErrorRow by mutableStateOf(false)
    var isErrorCell by mutableStateOf(false)
    var field by mutableStateOf("")
    var row by mutableStateOf("")
    var cell by mutableStateOf("")

    private fun search() {
        _state.value = ScreenState.Loading
        interactor.request(
            ConnectionModel.Address(
                field.toInt(), row.toInt(), cell.toInt(),
                SEARCH_BY_PLACE_CODE
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
        if (!isErrorFields()) {
            search()
        }
    }

    private fun isFieldsEmpty() {
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

    private fun isErrorFields(): Boolean {
        return isErrorField || isErrorRow || isErrorCell
    }

    fun clearEditTexts() {
        field = ""
        row = ""
        cell = ""
        isErrorField = false
        isErrorRow = false
        isErrorCell = false
    }

    fun setDefaultState() {
        _state.value = ScreenState.Default
    }
}