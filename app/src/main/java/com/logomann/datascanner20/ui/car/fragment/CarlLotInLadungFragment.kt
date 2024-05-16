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
import com.logomann.datascanner20.databinding.FragmentCarLotInLadungBinding
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.ui.camera.fragment.CameraFragment
import com.logomann.datascanner20.ui.car.view_model.CarLotInLadungViewModel
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import com.logomann.datascanner20.util.CAMERA_REQUEST_KEY
import com.logomann.datascanner20.util.CAMERA_RESULT
import org.koin.androidx.viewmodel.ext.android.viewModel

class CarlLotInLadungFragment : Fragment() {

    private var _binding: FragmentCarLotInLadungBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<CarLotInLadungViewModel>()
    private lateinit var lotEt: EditText
    private lateinit var ladungEt: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarLotInLadungBinding.inflate(inflater, container, false)
        lotEt = binding.carOpLotEt
        ladungEt = binding.carOpLadungEt

        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is ScreenState.CameraResult -> {
                    lotEt.setText(screenState.result)
                }

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
                    hideKeyboard()
                    hideGroup()
                }

                ScreenState.NoInternet -> {
                    showGroup()
                    showMessage(getString(R.string.nointernet), true)
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
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CAMERA_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            viewModel.setCameraResult(bundle.getString(CAMERA_RESULT).toString())
        }
        binding.carOpCamera.setOnClickListener {
            startCamera()
        }
        binding.carOpBtnClear.setOnClickListener {
            clearText()
        }
        binding.carOpBtnOk.setOnClickListener {
            if (checkRequest()) {
                hideKeyboard()
                viewModel.request(lotEt.text.toString(), ladungEt.text.toString())
            }
        }
    }

    private fun showGroup() {
        binding.carOpLotInLadungPb.isVisible = false
        binding.lotInLadungGroup.isVisible = true
    }

    private fun hideGroup() {
        binding.lotInLadungGroup.isVisible = false
        binding.carOpLotInLadungPb.isVisible = true

    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(lotEt.windowToken, 0)
    }

    private fun clearText() {
        lotEt.setText("")
        ladungEt.setText("")
    }

    private fun showMessage(message: String, isError: Boolean) {
        if (isError) {
            SnackbarMessage.showMessageError(binding.cl, message, requireContext())
        } else {
            SnackbarMessage.showMessageOk(binding.cl, message, requireContext())
        }
    }

    private fun startCamera() {
        requireActivity().supportFragmentManager.commit {
            add(R.id.fragment_container_view, CameraFragment())
            addToBackStack(null)
        }
    }

    private fun checkRequest(): Boolean {
        if (!lotEt.text.isNullOrEmpty() && !ladungEt.text.isNullOrEmpty()) {
            return true
        } else {
            showMessage(getString(R.string.not_all_cells_are_filled), true)
            return false
        }
    }


}