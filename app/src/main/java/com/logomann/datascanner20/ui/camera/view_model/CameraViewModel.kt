package com.logomann.datascanner20.ui.camera.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.ui.camera.CameraScreenState
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class CameraViewModel : ViewModel() {
    private var result = ""

    private val _state = MutableStateFlow<CameraScreenState>(CameraScreenState.Default)
    val state: StateFlow<CameraScreenState> = _state

    fun setResult(barcode: Barcode) {
        result = if (barcode.rawValue!!.startsWith("VinCode:")) {
            barcode.rawValue!!.substring(8)
        } else if (barcode.rawValue!!.startsWith("*")) {
            barcode.rawValue!!.substring(1)
        } else {
            barcode.rawValue.toString()
        }
        updateResult()
    }

    private fun updateResult() {
        _state.value = CameraScreenState.Result(result)
    }

    fun getScanner(): BarcodeScanner {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_EAN_13
            )
            .build()
        return BarcodeScanning.getClient(options)
    }
    fun setDefaultState() {
        _state.value = CameraScreenState.Default
    }
}