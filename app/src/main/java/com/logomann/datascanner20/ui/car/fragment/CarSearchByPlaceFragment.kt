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
import com.logomann.datascanner20.R
import com.logomann.datascanner20.databinding.FragmentCarSearchByPlaceBinding
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.ui.car.view_model.CarSearchByPlaceViewModel
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import org.koin.androidx.viewmodel.ext.android.viewModel

class CarSearchByPlaceFragment : Fragment() {


    private var _binding: FragmentCarSearchByPlaceBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldEt: EditText
    private lateinit var rowEt: EditText
    private lateinit var cellEt: EditText
    private val viewModel by viewModel<CarSearchByPlaceViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarSearchByPlaceBinding.inflate(inflater, container, false)
        fieldEt = binding.carOpFieldEt
        rowEt = binding.carOpRowEt
        cellEt = binding.carOpCellEt
        binding.carOpBtnOk.setOnClickListener {
            if (checkRequest()) {
                hideKeyboard()
                viewModel.request(
                    fieldEt.text.toString().toInt(),
                    rowEt.text.toString().toInt(),
                    cellEt.text.toString().toInt()
                )
            }
        }
        binding.carOpBtnClear.setOnClickListener {
            clearText()
        }
        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is ScreenState.CameraResult -> {}
                is ScreenState.Content -> {
                    clearText()
                    showGroup()
                    showMessage(screenState.message.toString(), false)
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

    private fun showMessage(message: String, isError: Boolean) {
        if (isError) {
            SnackbarMessage.showMessageError(binding.cl, message, requireContext())
        } else {
            SnackbarMessage.showMessageAction(binding.cl, message, requireContext())
        }
    }

    private fun showGroup() {
        binding.carOpPlaceSearchPb.isVisible = false
        binding.searchPlaceGroup.isVisible = true
    }

    private fun hideGroup() {
        binding.searchPlaceGroup.isVisible = false
        binding.carOpPlaceSearchPb.isVisible = true
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(fieldEt.windowToken, 0)
    }

    private fun clearText() {
        fieldEt.setText("")
        rowEt.setText("")
        cellEt.setText("")
    }

    private fun checkRequest(): Boolean {
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