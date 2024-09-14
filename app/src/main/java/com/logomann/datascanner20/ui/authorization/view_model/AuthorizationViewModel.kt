package com.logomann.datascanner20.ui.authorization.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.screens.ScreenState
import com.logomann.datascanner20.ui.car.ROW_MINIMUM_SYMBOLS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthorizationViewModel(
    private val connectionInteractor: ConnectionInteractor
) : ViewModel() {

    var pinCode by mutableStateOf("")
    var isResumed by mutableStateOf(false)
    var isErrorMessage by mutableStateOf(false)
    var isErrorPin by mutableStateOf(false)

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Default)
    val state: StateFlow<ScreenState> = _state.asStateFlow()

    fun validateField() {
        isErrorPin = pinCode.length < ROW_MINIMUM_SYMBOLS
    }

    fun authorize() {
        _state.value = ScreenState.Loading
        val user = ConnectionModel.User(pinCode)
        connectionInteractor.request(user, onComplete = { data, code ->
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

    fun setIsResumed(isResumed: Boolean) {
        this.isResumed = isResumed
    }

    fun setDefaultState() {
        _state.value = ScreenState.Default
    }
}