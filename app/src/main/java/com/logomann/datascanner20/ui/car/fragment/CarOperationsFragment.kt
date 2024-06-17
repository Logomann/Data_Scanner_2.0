package com.logomann.datascanner20.ui.car.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.logomann.datascanner20.R
import com.logomann.datascanner20.databinding.FragmentCarOperationsBinding
import com.logomann.datascanner20.ui.car.CarOperationsAdapter
import com.logomann.datascanner20.ui.menu.SelectOperation
import com.google.android.material.tabs.TabLayoutMediator

class CarOperationsFragment : Fragment(), SelectOperation {
    private var _binding: FragmentCarOperationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarOperationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun navigateTo(operation: Int) {
        binding.fragCarVp.currentItem = operation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CarOperationsAdapter(hostFragment = this)
        binding.fragCarVp.adapter = adapter
        tabMediator = TabLayoutMediator(binding.fragCarTl, binding.fragCarVp) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.relocation)
                1 -> tab.text = getString(R.string.search_by_VIN)
                2 -> tab.text = getString(R.string.search_by_place)
                3 -> tab.text = getString(R.string.lot_in_ladung)
                4 -> tab.text = getString(R.string.car_lotting)
               // 5 -> tab.text = getString(R.string.loading_sheet)

            }
        }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
        _binding = null
    }

}