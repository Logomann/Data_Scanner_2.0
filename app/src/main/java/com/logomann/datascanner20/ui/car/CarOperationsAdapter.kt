package com.logomann.datascanner20.ui.car

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.logomann.datascanner20.ui.car.fragment.CarLottingFragment

class CarOperationsAdapter(hostFragment: Fragment) : FragmentStateAdapter(hostFragment) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            4 -> CarLottingFragment()
           // 5 -> CarLoadingSheetFragment()
            else -> CarLottingFragment()
        }

    }
}