package com.logomann.datascanner20.ui.container

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.logomann.datascanner20.ui.container.fragment.ContainerArrivalFragment
import com.logomann.datascanner20.ui.container.fragment.ContainerInCarriageFragment

class ContainerOperationsAdapter(hostFragment: Fragment) : FragmentStateAdapter(hostFragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ContainerArrivalFragment()
            1 -> ContainerInCarriageFragment()
            else -> Fragment()
        }
    }
}