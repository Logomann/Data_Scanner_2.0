package com.logomann.datascanner20.ui.car.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.util.SEARCH_BY_VIN_CODE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CarSearchByVINViewModel(private val interactor: ConnectionInteractor) : ViewModel() {

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Default)
    val state: StateFlow<ScreenState> = _state

    private val _stateErrorFields = MutableStateFlow(false)
    val stateErrorFields: StateFlow<Boolean> = _stateErrorFields

    var vin by mutableStateOf("")
    var isErrorVin by mutableStateOf(false)
    var isErrorMessage by mutableStateOf(false)

    private fun search() {
        _state.value = ScreenState.Loading
        interactor.request(ConnectionModel.Car(
            vin, null,
            SEARCH_BY_VIN_CODE
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
        if (isVinEmpty()) {
            _stateErrorFields.value = true
        } else {
            search()
        }
    }

    fun clearVin() {
        vin = ""
    }

    private fun isVinEmpty(): Boolean {
        return vin.isEmpty()
    }

    fun setCameraResult(result: String) {
        vin = result
        _state.value = ScreenState.CameraResult(result)
    }

    fun setDefaultState() {
        _state.value = ScreenState.Default
    }

}