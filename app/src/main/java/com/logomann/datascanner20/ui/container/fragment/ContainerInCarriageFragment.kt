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
import com.logomann.datascanner20.databinding.FragmentContainerInCarriageBinding
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.ui.container.view_model.ContainerInCarriageViewModel
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContainerInCarriageFragment : Fragment() {

    private var _binding: FragmentContainerInCarriageBinding? = null
    private val binding get() = _binding!!
    private lateinit var containerNumber: EditText
    private lateinit var wagonNumber: EditText
    private val viewModel by viewModel<ContainerInCarriageViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContainerInCarriageBinding.inflate(inflater, container, false)
        containerNumber = binding.containerInCarriageNumEt
        wagonNumber = binding.containerCarriageEt
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
        binding.containerCarriagePb.isVisible = false
        binding.containerInCarriageGroup.isVisible = true
    }

    private fun hideGroup() {
        binding.containerInCarriageGroup.isVisible = false
        binding.containerCarriagePb.isVisible = true

    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(containerNumber.windowToken, 0)
    }

    private fun clearText() {
        containerNumber.setText(getString(R.string.empty))
        wagonNumber.setText(getString(R.string.empty))
    }

    private fun showMessage(message: String, isError: Boolean) {
        if (isError) {
            SnackbarMessage.showMessageError(binding.cl, message, requireContext())
        } else {
            SnackbarMessage.showMessageOk(binding.cl, message, requireContext())
        }
    }

    private fun checkRequest(): Boolean {
        if (!containerNumber.text.isNullOrEmpty() && !wagonNumber.text.isNullOrEmpty()) {
            return true
        } else {
            showMessage(getString(R.string.not_all_cells_are_filled), true)
            return false
        }
    }
}