package com.logomann.datascanner20.ui.authorization.fragment

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.logomann.datascanner20.R
import com.logomann.datascanner20.databinding.FragmentAuthorizationBinding
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.ui.authorization.view_model.AuthorizationViewModel
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import org.koin.androidx.viewmodel.ext.android.viewModel


class AuthorizationFragment : Fragment() {
    private lateinit var pinCodeField: EditText
    private var btn: ImageButton? = null
    private var _binding: FragmentAuthorizationBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<AuthorizationViewModel>()
    private var pinCode = ""
    private var settingsBtn: ImageButton? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthorizationBinding.inflate(inflater, container, false)

        pinCodeField = binding.pinCodeEt
        btn = binding.authorizationBtn
        btn?.setOnClickListener {
            authorize()
        }
        settingsBtn = binding.authorizationSettingsBtn
        settingsBtn!!.setOnClickListener {
            findNavController().navigate(R.id.action_authorizationFragment_to_settingsFragment)
        }

        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is ScreenState.Content -> {
                    binding.authorizationGroup.isVisible = true
                    binding.authorizationPb.isVisible = false
                    showMessage(screenState.message.toString(), false)
                    authorized()
                    viewModel.setScreenStateDefault()
                }

                is ScreenState.Default -> {}
                is ScreenState.Error -> {
                    showGroup()
                    showMessage(screenState.message.toString(), true)
                    viewModel.setScreenStateDefault()
                }

                is ScreenState.Loading -> {
                    hideGroup()
                }

                is ScreenState.NoInternet -> {
                    showGroup()
                    showMessage(getString(R.string.nointernet), true)
                    viewModel.setScreenStateDefault()
                }

                is ScreenState.CameraResult -> {}
                ScreenState.ServerError -> {
                    showGroup()
                    showMessage(getString(R.string.server_error), true)
                    viewModel.setScreenStateDefault()
                }

                is ScreenState.ListRefreshed -> {}
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                pinCode = s.toString()
                if (pinCode.length == 4 && !viewModel.isResumed()) {
                    val inputMethodManager =
                        requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
                    inputMethodManager?.hideSoftInputFromWindow(pinCodeField.windowToken, 0)
                    authorize()
                } else if (pinCode.length == 4) {
                    btn?.isVisible = true
                }
                if (pinCode.length < 4) {
                    btn?.isVisible = false
                    viewModel.setIsResumed(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }
        pinCodeField.addTextChangedListener(editTextWatcher)
    }

    override fun onPause() {
        super.onPause()
        if (pinCode.isNotEmpty()) {
            viewModel.setIsResumed(true)
        }
    }

    private fun authorize() {
        viewModel.setPinCode(pinCode)
    }

    private fun authorized() {
        findNavController().navigate(R.id.action_authorizationFragment_to_menuFragment)
    }

    private fun showMessage(message: String, isError: Boolean) {
        if (isError) {
            SnackbarMessage.showMessageError(
                requireActivity().findViewById(R.id.cl_main), message, requireContext()
            )
        } else {
            SnackbarMessage.showMessageOk(
                requireActivity().findViewById(R.id.cl_main), message, requireContext()
            )
        }
    }

    private fun showGroup() {
        binding.authorizationPb.isVisible = false
        btn?.isVisible = true
        binding.authorizationGroup.isVisible = true
    }

    private fun hideGroup() {
        binding.authorizationGroup.isVisible = false
        btn?.isVisible = false
        binding.authorizationPb.isVisible = true
    }

}