package com.logomann.datascanner20.ui.car.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.util.LOT_IN_LADUNG_CODE


class CarLotInLadungViewModel(private val interactor: ConnectionInteractor) : ViewModel() {
    private var lot = ""
    private var row = ""
    private val screenStateLiveData =
        MutableLiveData<ScreenState>(ScreenState.Default)

    private fun setLot() {
        setScreenState(ScreenState.Loading)
        interactor.request(ConnectionModel.Lot(
            lot, row,
            LOT_IN_LADUNG_CODE
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

    fun request(lot: String, row: String) {
        this.lot = lot
        this.row = row
        setLot()
    }

    fun setCameraResult(result: String) {
        screenStateLiveData.postValue(ScreenState.CameraResult(result))
    }

    fun getScreenStateLiveData(): LiveData<ScreenState> = screenStateLiveData
}