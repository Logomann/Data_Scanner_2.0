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
import com.logomann.datascanner20.databinding.FragmentCarRelocationBinding
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.ui.camera.fragment.CameraFragment
import com.logomann.datascanner20.ui.car.view_model.CarRelocationViewModel
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import com.logomann.datascanner20.util.CAMERA_REQUEST_KEY
import com.logomann.datascanner20.util.CAMERA_RESULT
import org.koin.androidx.viewmodel.ext.android.viewModel

class CarRelocationFragment : Fragment() {

    private var _binding: FragmentCarRelocationBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<CarRelocationViewModel>()
    private lateinit var vinCodeEt: EditText
    private lateinit var fieldEt: EditText
    private lateinit var rowEt: EditText
    private lateinit var cellEt: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarRelocationBinding.inflate(inflater, container, false)
        vinCodeEt = binding.carOpVinCodeEt
        fieldEt = binding.carOpFieldEt
        rowEt = binding.carOpRowEt
        cellEt = binding.carOpCellEt

        binding.carOpBtnOk.setOnClickListener {
            if (checkRequest()) {
                hideKeyboard()
                viewModel.request(
                    vinCodeEt.text.toString(),
                    fieldEt.text.toString().toInt(),
                    rowEt.text.toString().toInt(),
                    cellEt.text.toString().toInt()
                )
            }
        }
        binding.carOpBtnClear.setOnClickListener {
            clearText()
        }
        binding.carOpCamera.setOnClickListener {
            startCamera()
        }
        binding.carOpBtnCell.setOnClickListener {
            if (checkCellClearRequest()) {
                hideKeyboard()
                viewModel.clearCell(
                    fieldEt.text.toString().toInt(),
                    rowEt.text.toString().toInt(),
                    cellEt.text.toString().toInt()
                )
            }
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
                is ScreenState.AddressCleared -> {
                    showGroup()
                    showMessage(screenState.message.toString(), false)
                    clearCells()
                }
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
    }
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun startCamera() {
        requireActivity().supportFragmentManager.commit {
            add(R.id.fragment_container_view, CameraFragment())
            addToBackStack(null)
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(vinCodeEt.windowToken, 0)
    }

    private fun clearText() {
        vinCodeEt.setText(getString(R.string.empty))
        clearCells()
    }
    private fun clearCells() {
        fieldEt.setText(getString(R.string.empty))
        rowEt.setText(getString(R.string.empty))
        cellEt.setText(getString(R.string.empty))
    }

    private fun hideGroup() {
        binding.carRelocationGroup.isVisible = false
        binding.carOpRelocationPb.isVisible = true
    }

    private fun showGroup() {
        binding.carOpRelocationPb.isVisible = false
        binding.carRelocationGroup.isVisible = true

    }

    private fun checkRequest(): Boolean {
        if (vinCodeEt.text.length < 17) {
            showMessage(getString(R.string.vin_is_incorrect), true)
            return false
        } else if (!fieldEt.text.isNullOrEmpty() && !rowEt.text.isNullOrEmpty()
            && !cellEt.text.isNullOrEmpty()
        ) {
            return true
        } else {
            showMessage(getString(R.string.not_all_cells_are_filled), true)
            return false
        }
    }

    private fun showMessage(message: String, isError: Boolean) {
        if (isError) {
            SnackbarMessage.showMessageError(binding.cl, message, requireContext())
        } else {
            SnackbarMessage.showMessageOk(binding.cl, message, requireContext())
        }
    }

    private fun checkCellClearRequest(): Boolean {
        if (!fieldEt.text.isNullOrEmpty() && !rowEt.text.isNullOrEmpty()
            && !cellEt.text.isNullOrEmpty()
        ) {
            return true
        } else {
            showMessage(getString(R.string.not_all_cells_are_filled), true)
            return false
        }
    }
}