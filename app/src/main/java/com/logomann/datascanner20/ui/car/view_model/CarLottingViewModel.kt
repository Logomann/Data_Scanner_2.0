package com.logomann.datascanner20.ui.car.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.screens.ScreenState
import com.logomann.datascanner20.util.CAR_LOTTING_CODE
import com.logomann.datascanner20.util.debounce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CarLottingViewModel(private val interactor: ConnectionInteractor) : ViewModel() {

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Default)
    val state: StateFlow<ScreenState> = _state

    private val _stateErrorFields = MutableStateFlow(false)
    val stateErrorFields: StateFlow<Boolean> = _stateErrorFields

    private val _stateErrorList = MutableStateFlow(false)
    val stateErrorList: StateFlow<Boolean> = _stateErrorList

    private val _stateIsClickable = MutableStateFlow(true)
    val stateIsClickable: StateFlow<Boolean> = _stateIsClickable

    var vin by mutableStateOf("")
    var lot = mutableStateListOf<String>()
    var driver by mutableStateOf("")
    var isErrorVin by mutableStateOf(false)
    var isErrorDriver by mutableStateOf(false)
    var isErrorMessage by mutableStateOf(false)

    private val vinOnClickDebounce =
        debounce<Boolean>(2000L, viewModelScope, false) {
            _stateIsClickable.value = it
        }
    fun onVinClicked() {
        vinOnClickDebounce(true)
    }

    private fun update() {
        _state.value = ScreenState.Loading
        interactor.request(
            ConnectionModel.Driver(driver, CAR_LOTTING_CODE, lot), onComplete = { data, code ->
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
        if (driver.isNotEmpty() && lot.isNotEmpty()) {
            update()
        } else {
            _stateErrorFields.value = true
        }
    }

    fun addToList() {
        if (vin.isNotEmpty()) {
            if (!lot.contains(vin)) {
                lot.add(vin)
            } else {
                _stateErrorList.value = true
            }
        } else {
            _stateErrorFields.value = true
        }
    }

    fun clearFields() {
        vin = ""
        driver = ""
    }

    fun clearList() {
        lot.clear()
    }

    fun removeFromList(vin: String) {
        if (lot.contains(vin)) {
            lot.remove(vin)
        }
    }

    fun setCameraResult(result: String) {
        vin = result
        _state.value = ScreenState.CameraResult(result)
    }

    fun setDefaultState() {
        _state.value = ScreenState.Default
    }

    fun setDefaultErrorFieldsState() {
        _stateErrorFields.value = false
    }

    fun setDefaultErrorListState() {
        _stateErrorList.value = false
    }

}