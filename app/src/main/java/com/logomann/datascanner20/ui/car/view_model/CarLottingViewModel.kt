package com.logomann.datascanner20.ui.car.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.util.CAR_LOTTING_CODE

class CarLottingViewModel(private val interactor: ConnectionInteractor) : ViewModel() {

    private var driver = ""
    private val lot = mutableListOf<String>()
    private val screenStateLiveData =
        MutableLiveData<ScreenState>(ScreenState.Default)

    private fun update() {
        setScreenState(ScreenState.Loading)
        interactor.request(
            ConnectionModel.Driver(driver, CAR_LOTTING_CODE, lot), onComplete = { data, code ->
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

    fun request(driver: String) {
        this.driver = driver
        update()
    }

    fun addToList(vin: String) {
        if (!lot.contains(vin)) {
            lot.add(vin)
            screenStateLiveData.postValue(ScreenState.ListRefreshed(lot))
        }

    }

    fun clearList() {
        lot.clear()
        screenStateLiveData.postValue(ScreenState.ListRefreshed(lot))
    }

    fun removeFromList(vin: String) {
        if (lot.contains(vin)) {
            lot.remove(vin)
            screenStateLiveData.postValue(ScreenState.ListRefreshed(lot))
        }
    }

    fun setCameraResult(result: String) {
        screenStateLiveData.postValue(ScreenState.CameraResult(result))
    }

    fun getScreenStateLiveData(): LiveData<ScreenState> = screenStateLiveData
}