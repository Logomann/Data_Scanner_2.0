package com.logomann.datascanner20.ui.car.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.util.SEARCH_BY_VIN_CODE


class CarSearchByVINViewModel(private val interactor: ConnectionInteractor) : ViewModel() {
    private var vin = ""
    private val screenStateLiveData =
        MutableLiveData<ScreenState>(ScreenState.Default)

    private fun search() {
        setScreenState(ScreenState.Loading)
        interactor.request(ConnectionModel.Car(
            vin, null,
            SEARCH_BY_VIN_CODE
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

    fun request(vin: String) {
        this.vin = vin
        search()
    }

    fun setCameraResult(result: String) {
        screenStateLiveData.postValue(ScreenState.CameraResult(result))
    }

    fun getScreenStateLiveData(): LiveData<ScreenState> = screenStateLiveData
}