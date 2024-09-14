package com.logomann.datascanner20.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.logomann.datascanner20.databinding.FragmentSettingsBinding
import com.logomann.datascanner20.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val btn = binding.settingsSwitch
        btn.isChecked = viewModel.getTheme()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.settingsArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.settingsSwitch.setOnClickListener {
            viewModel.switchTheme()
        }
    }
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }




}