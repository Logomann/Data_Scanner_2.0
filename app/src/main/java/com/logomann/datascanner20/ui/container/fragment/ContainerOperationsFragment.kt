package com.logomann.datascanner20.ui.container.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.logomann.datascanner20.R
import com.logomann.datascanner20.databinding.FragmentContainerOperationsBinding
import com.logomann.datascanner20.ui.menu.SelectOperation
import com.logomann.datascanner20.ui.container.ContainerOperationsAdapter
import com.google.android.material.tabs.TabLayoutMediator

class ContainerOperationsFragment : Fragment(), SelectOperation {
    private var _binding: FragmentContainerOperationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContainerOperationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ContainerOperationsAdapter(hostFragment = this)
        binding.fragContainerVp.adapter = adapter
        tabMediator = TabLayoutMediator(binding.fragContainerTl,binding.fragContainerVp) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.container_arrival)
                1 -> tab.text = getString(R.string.container_in_carriage)
            }
        }
        tabMediator.attach()
    }

    override fun navigateTo(operation: Int) {
        binding.fragContainerVp.currentItem = operation
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }


}