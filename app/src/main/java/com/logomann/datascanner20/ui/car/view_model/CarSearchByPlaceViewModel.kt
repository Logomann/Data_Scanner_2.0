package com.logomann.datascanner20.ui.car.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.util.SEARCH_BY_PLACE_CODE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class CarSearchByPlaceViewModel(private val interactor: ConnectionInteractor) : ViewModel() {

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Default)
    val state: StateFlow<ScreenState> = _state

    private val _stateErrorFields = MutableStateFlow(false)
    val stateErrorFields: StateFlow<Boolean> = _stateErrorFields

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
        if (isFieldsEmpty()) {
            _stateErrorFields.value = true
        } else {
            search()
        }
    }

    private fun isFieldsEmpty(): Boolean {
        return field.isEmpty() || row.isEmpty() || cell.isEmpty()
    }

    fun clearEditTexts() {
        field = ""
        row = ""
        cell = ""
        _stateErrorFields.value = false
    }

    fun setDefaultState() {
        _state.value = ScreenState.Default
    }
}