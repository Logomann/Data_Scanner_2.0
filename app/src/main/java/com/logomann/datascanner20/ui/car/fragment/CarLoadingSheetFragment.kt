package com.logomann.datascanner20.ui.car.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.logomann.datascanner20.databinding.FragmentCarLoadingSheetBinding


class CarLoadingSheetFragment : Fragment() {

    private var _binding: FragmentCarLoadingSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarLoadingSheetBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}