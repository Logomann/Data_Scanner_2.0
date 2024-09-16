package com.logomann.datascanner20.ui.camera.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

class CameraViewModel : ViewModel() {
    var result by mutableStateOf("")

    fun setResult(barcode: Barcode) {
        result = if (barcode.rawValue!!.startsWith("VinCode:")) {
            barcode.rawValue!!.substring(8)
        } else if (barcode.rawValue!!.startsWith("*")) {
            barcode.rawValue!!.substring(1)
        } else {
            barcode.rawValue.toString().uppercase()
        }
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
}