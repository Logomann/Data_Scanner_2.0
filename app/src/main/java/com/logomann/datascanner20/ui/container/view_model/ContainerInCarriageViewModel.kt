package com.logomann.datascanner20.ui.container.view_model


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.screens.ScreenState
import com.logomann.datascanner20.util.CONTAINER_IN_CARRIAGE_CODE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ContainerInCarriageViewModel(private val interactor: ConnectionInteractor) : ViewModel() {
    var containerNumber by mutableStateOf("")
    var isContainerError by mutableStateOf(false)
    var isWagonError by mutableStateOf(false)
    var wagonNumber by mutableStateOf("")
    var isErrorMessage by mutableStateOf(false)

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Default)
    val state: StateFlow<ScreenState> = _state

    private fun setWagon() {
        _state.value = ScreenState.Loading
        interactor.request(
            ConnectionModel.Container(
                containerNumber, null, CONTAINER_IN_CARRIAGE_CODE, wagonNumber
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
        checkFields()
        if (!isError()) {
            setWagon()
        }
    }

    fun clearFields() {
        containerNumber = ""
        wagonNumber = ""
        isWagonError = false
        isContainerError = false
    }

    private fun checkFields() {
        if (containerNumber.isEmpty()) {
            isContainerError = true
        }
        if (wagonNumber.isEmpty()) {
            isWagonError = true
        }
    }

    private fun isError(): Boolean {
        return isContainerError || isWagonError
    }

    fun setDefaultState() {
        _state.value = ScreenState.Default
    }
}