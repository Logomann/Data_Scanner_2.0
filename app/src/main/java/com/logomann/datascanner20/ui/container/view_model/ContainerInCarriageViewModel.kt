package com.logomann.datascanner20.ui.container.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.util.CONTAINER_IN_CARRIAGE_CODE

class ContainerInCarriageViewModel(private val interactor: ConnectionInteractor) : ViewModel() {
    private var number = ""
    private var wagon = ""

    private val screenStateLiveData =
        MutableLiveData<ScreenState>(ScreenState.Default)

    private fun setWagon() {
        setScreenState(ScreenState.Loading)
        interactor.request(
            ConnectionModel.Container(
                number, null, CONTAINER_IN_CARRIAGE_CODE, wagon
            ), onComplete = { data, code ->
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

    private fun setScreenState(state: ScreenState) {
        screenStateLiveData.postValue(state)
    }

    fun request(number: String, wagon: String) {
        this.number = number
        this.wagon = wagon
        setWagon()
    }

    fun getScreenStateLiveData(): LiveData<ScreenState> = screenStateLiveData
}