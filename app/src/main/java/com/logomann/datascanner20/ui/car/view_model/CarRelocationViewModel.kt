package com.logomann.datascanner20.ui.car.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.util.CLEAR_CELL_CODE
import com.logomann.datascanner20.util.RELOCATION_CODE

class CarRelocationViewModel(private val interactor: ConnectionInteractor) : ViewModel() {
    private lateinit var car: ConnectionModel.Car
    private val screenStateLiveData =
        MutableLiveData<ScreenState>(ScreenState.Default)

    private fun updateData() {
        setScreenState(ScreenState.Loading)
        interactor.request(car, onComplete = { data, code ->
            if (data == null) {
                screenStateLiveData.postValue(ScreenState.NoInternet)
            } else if (code == 1) {
                screenStateLiveData.postValue(ScreenState.Content(data))
            } else {
                screenStateLiveData.postValue(ScreenState.Error(data))
            }

        })
    }

    fun clearCell(field: Int, row: Int, cell: Int) {
        setScreenState(ScreenState.Loading)
        val address = ConnectionModel.Address(field, row, cell, CLEAR_CELL_CODE)
        interactor.request(address, onComplete = { data, code ->
            if (data == null) {
                screenStateLiveData.postValue(ScreenState.NoInternet)
            } else if (code == 1) {
                screenStateLiveData.postValue(ScreenState.AddressCleared(data))
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

    fun request(vin: String, field: Int, row: Int, cell: Int) {
        val address = ConnectionModel.Address(field, row, cell, 0)
        car = ConnectionModel.Car(vin, address, RELOCATION_CODE)
        updateData()
    }

    fun setCameraResult(result: String) {
        screenStateLiveData.postValue(ScreenState.CameraResult(result))
    }

    fun getScreenStateLiveData(): LiveData<ScreenState> = screenStateLiveData
}