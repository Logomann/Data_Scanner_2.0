package com.logomann.datascanner20.ui.camera.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.ui.camera.CameraScreenState
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode


class CameraViewModel : ViewModel() {
    private var result = ""
    private val screenStateLiveData =
        MutableLiveData<CameraScreenState>(CameraScreenState.Default)

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
        setScreenState(CameraScreenState.Result(result))
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

    private fun setScreenState(state: CameraScreenState) {
        screenStateLiveData.postValue(state)
    }


    fun getScreenStateLiveData(): LiveData<CameraScreenState> = screenStateLiveData
}