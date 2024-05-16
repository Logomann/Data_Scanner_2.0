package com.logomann.datascanner20.ui.camera.fragment


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.logomann.datascanner20.R
import com.logomann.datascanner20.databinding.FragmentCameraBinding
import com.logomann.datascanner20.ui.camera.CameraScreenState
import com.logomann.datascanner20.ui.camera.view_model.CameraViewModel
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import com.logomann.datascanner20.util.CAMERA_REQUEST_KEY
import com.logomann.datascanner20.util.CAMERA_RESULT
import org.koin.androidx.viewmodel.ext.android.viewModel


class CameraFragment : Fragment() {


    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private var isTorchOn = false
    private var cameraController: LifecycleCameraController? = null
    private val viewModel by viewModel<CameraViewModel>()

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                showMessage(getString(R.string.permission_request_denied))
            } else {
                startCamera()
            }
        }

    private fun showMessage(message: String) {
        SnackbarMessage.showMessageError(binding.cl, message, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraController = LifecycleCameraController(requireContext())
        binding.cameraFlashBtn.setOnClickListener {
            switchFlash()
        }
        binding.cameraArrow.setOnClickListener {
            closeFragment()
        }
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                CameraScreenState.Default -> {}
                is CameraScreenState.Result -> {
                    sendResult(screenState.result)
                }
            }

        }
        startCamera()
    }

    private fun sendResult(value: String) {
        requireActivity().supportFragmentManager.setFragmentResult(
            CAMERA_REQUEST_KEY,
            bundleOf(CAMERA_RESULT to value)
        )
        closeFragment()
    }

    private fun closeFragment() {
        requireActivity().supportFragmentManager.commit {
            remove(this@CameraFragment)
        }
    }


    private fun startCamera() {
        val previewView: PreviewView = binding.cameraPreview

        cameraController!!.isTapToFocusEnabled = true
        val scanner = viewModel.getScanner()
        cameraController!!.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(requireContext()),
            MlKitAnalyzer(
                listOf(scanner),
                COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(requireContext())
            ) { result: MlKitAnalyzer.Result? ->
                val barcodeResults = result?.getValue(scanner)
                if ((barcodeResults == null) ||
                    (barcodeResults.size == 0) ||
                    (barcodeResults.first() == null)
                ) {
                    previewView.overlay.clear()
                    return@MlKitAnalyzer
                }
                viewModel.setResult(barcodeResults[0])

                previewView.overlay.clear()

            }
        )
        cameraController!!.bindToLifecycle(this)
        previewView.controller = cameraController
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun switchFlash() {

        if (cameraController!!.cameraInfo!!.hasFlashUnit())
            isTorchOn = if (isTorchOn) {
                cameraController!!.cameraControl!!.enableTorch(false)
                binding.cameraFlashBtn.imageTintList =
                    requireActivity().getColorStateList(R.color.white)
                false
            } else {
                cameraController!!.cameraControl!!.enableTorch(true)
                binding.cameraFlashBtn.imageTintList =
                    requireActivity().getColorStateList(R.color.yellow)
                true
            }

    }

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
            }.toTypedArray()
    }
}