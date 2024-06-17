package com.logomann.datascanner20.ui.menu.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.logomann.datascanner20.R
import com.logomann.datascanner20.databinding.FragmentMenuBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        bottomNavigationView = binding.bottomNavigation
        navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragment_container_menu_view) as NavHostFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}