package com.logomann.datascanner20.ui.container.fragment

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
import com.logomann.datascanner20.databinding.FragmentContainerArrivalBinding
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.ui.container.view_model.ContainerArrivalViewModel
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContainerArrivalFragment : Fragment() {

    private var _binding: FragmentContainerArrivalBinding? = null
    private val binding get() = _binding!!
    private lateinit var containerNumber: EditText
    private lateinit var fieldEt: EditText
    private lateinit var rowEt: EditText
    private lateinit var cellEt: EditText
    private val viewModel by viewModel<ContainerArrivalViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContainerArrivalBinding.inflate(inflater, container, false)
        containerNumber = binding.containerNumEt
        fieldEt = binding.containerFieldEt
        rowEt = binding.containerRowEt
        cellEt = binding.containerCellEt
        binding.containerBtnClear.setOnClickListener {
            clearText()
        }
        return binding.root
    }
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun showGroup() {
        binding.containerArrivalPb.isVisible = false
        binding.containerArrivalGroup.isVisible = true
    }

    private fun hideGroup() {
        binding.containerArrivalGroup.isVisible = false
        binding.containerArrivalPb.isVisible = true

    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(containerNumber.windowToken, 0)
    }

    private fun clearText() {
        containerNumber.setText(getString(R.string.empty))
        fieldEt.setText(getString(R.string.empty))
        rowEt.setText(getString(R.string.empty))
        cellEt.setText(getString(R.string.empty))
    }

    private fun showMessage(message: String, isError: Boolean) {
        if (isError) {
            SnackbarMessage.showMessageError(binding.cl, message, requireContext())
        } else {
            SnackbarMessage.showMessageOk(binding.cl, message, requireContext())
        }
    }

    private fun checkRequest(): Boolean {
        if (!containerNumber.text.isNullOrEmpty() && !fieldEt.text.isNullOrEmpty() && !rowEt.text.isNullOrEmpty()
            && !cellEt.text.isNullOrEmpty()
        ) {
            return true
        } else {
            showMessage(getString(R.string.not_all_cells_are_filled), true)
            return false
        }
    }
}