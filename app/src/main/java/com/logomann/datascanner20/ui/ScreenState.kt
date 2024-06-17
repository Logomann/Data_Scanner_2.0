package com.logomann.datascanner20.ui

sealed class ScreenState {
    data class Error(val message: String?) : ScreenState()
    data class Content(val message: String?) : ScreenState()
    data class AddressCleared(val message: String?) : ScreenState()
    data object NoInternet : ScreenState()
    data object Default : ScreenState()
    data object Loading : ScreenState()
    data class CameraResult(val result: String) : ScreenState()
    data object ServerError : ScreenState()
    data class ListRefreshed(val list: MutableList<String>) : ScreenState()
}