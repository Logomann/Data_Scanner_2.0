package com.logomann.datascanner20.ui.authorization.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.ScreenState



class AuthorizationViewModel(
    private val connectionInteractor: ConnectionInteractor
) : ViewModel() {
    private val screenStateLiveData =
        MutableLiveData<ScreenState>(ScreenState.Default)
    private var pinCode = ""
    private var isResumed = false

    private fun authorize() {
        setScreenState(ScreenState.Loading)
        val user = ConnectionModel.User(pinCode)
        connectionInteractor.request(user, onComplete = { data, code ->
            if (data == null) {
                screenStateLiveData.postValue(ScreenState.NoInternet)
            } else if (code == 1) {
                screenStateLiveData.postValue(ScreenState.Content(data))
            } else if (code == 2) {
                screenStateLiveData.postValue(ScreenState.ServerError)
            } else {
                screenStateLiveData.postValue(ScreenState.Error(data))
            }

        })
    }

    fun getScreenStateLiveData(): LiveData<ScreenState> = screenStateLiveData

    fun setIsResumed(isResumed: Boolean) {
        this.isResumed = isResumed
    }

    fun isResumed(): Boolean {
        return isResumed
    }

    private fun setScreenState(state: ScreenState) {
        screenStateLiveData.postValue(state)
    }

    fun setScreenStateDefault() {
        screenStateLiveData.postValue(ScreenState.Default)
    }

    fun setPinCode(pinCode: String) {
        this.pinCode = pinCode
        authorize()
    }
}