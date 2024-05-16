package com.logomann.datascanner20.ui.camera

sealed class CameraScreenState {
    data class Result(val result: String) : CameraScreenState()
    data object Default : CameraScreenState()
}