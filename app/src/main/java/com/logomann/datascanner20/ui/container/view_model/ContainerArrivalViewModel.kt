package com.logomann.datascanner20.ui.container.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.util.CONTAINER_ARRIVAL


class ContainerArrivalViewModel(private val interactor: ConnectionInteractor) : ViewModel() {
    private var number = ""
    private var field = 0
    private var sector = 0
    private var cell = 0

    private val screenStateLiveData =
        MutableLiveData<ScreenState>(ScreenState.Default)

    private fun setContainerAddress() {
        setScreenState(ScreenState.Loading)
        interactor.request(
            ConnectionModel.Container(
                number, ConnectionModel.Address(field, sector, cell, 0),
                CONTAINER_ARRIVAL, null
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

    fun request(number: String, field: Int, sector: Int, cell: Int) {
        this.number = number
        this.field = field
        this.sector = sector
        this.cell = cell
        setContainerAddress()
    }

    fun getScreenStateLiveData(): LiveData<ScreenState> = screenStateLiveData
}