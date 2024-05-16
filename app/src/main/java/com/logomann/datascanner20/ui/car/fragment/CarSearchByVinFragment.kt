package com.logomann.datascanner20.ui.car.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.logomann.datascanner20.R
import com.logomann.datascanner20.databinding.FragmentCarSearchByVinBinding
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.ui.camera.fragment.CameraFragment
import com.logomann.datascanner20.ui.car.view_model.CarSearchByVINViewModel
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import com.logomann.datascanner20.util.CAMERA_REQUEST_KEY
import com.logomann.datascanner20.util.CAMERA_RESULT
import org.koin.androidx.viewmodel.ext.android.viewModel


class CarSearchByVinFragment : Fragment() {

    private var _binding: FragmentCarSearchByVinBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<CarSearchByVINViewModel>()
    private lateinit var vinCodeEt: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarSearchByVinBinding.inflate(inflater, container, false)
        vinCodeEt = binding.carOpVinCodeEt
        binding.carOpBtnOk.setOnClickListener {
            if (checkRequest()) {
                hideKeyboard()
                viewModel.request(vinCodeEt.text.toString())
            }
        }
        binding.carOpBtnClear.setOnClickListener {
            clearText()
        }
        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is ScreenState.Content -> {
                    showGroup()
                    showMessage(screenState.message.toString(), false)
                    clearText()
                }

                ScreenState.Default -> {}
                is ScreenState.Error -> {
                    showGroup()
                    showMessage(screenState.message.toString(), true)
                }

                ScreenState.Loading -> {
                    hideGroup()
                }

                ScreenState.NoInternet -> {
                    showGroup()
                    showMessage(getString(R.string.nointernet), true)
                }

                is ScreenState.CameraResult -> {
                    vinCodeEt.setText(screenState.result)
                }

                ScreenState.ServerError -> {
                    showGroup()
                    showMessage(getString(R.string.server_error), true)
                }

                is ScreenState.ListRefreshed -> {}
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.carOpCamera.setOnClickListener {
            startCamera()
        }
        binding.carOpBtnClear.setOnClickListener {
            vinCodeEt.setText("")
        }
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CAMERA_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            viewModel.setCameraResult(bundle.getString(CAMERA_RESULT).toString())
        }
    }

    private fun startCamera() {
        requireActivity().supportFragmentManager.commit {
            add(R.id.fragment_container_view, CameraFragment())
            addToBackStack(null)
        }
    }

    private fun showGroup() {
        binding.carOpVinSearchPb.isVisible = false
        binding.searchVinGroup.isVisible = true
    }

    private fun hideGroup() {
        binding.searchVinGroup.isVisible = false
        binding.carOpVinSearchPb.isVisible = true
    }

    private fun showMessage(message: String, isError: Boolean) {
        if (isError) {
            SnackbarMessage.showMessageError(binding.cl, message, requireContext())
        } else {
            SnackbarMessage.showMessageAction(binding.cl, message, requireContext())
        }
    }

    private fun checkRequest(): Boolean {
        if (vinCodeEt.text.length < 17) {
            showMessage(getString(R.string.vin_is_incorrect), true)
            return false
        } else {
            return true
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(vinCodeEt.windowToken, 0)
    }

    private fun clearText() {
        vinCodeEt.setText("")
    }
}
