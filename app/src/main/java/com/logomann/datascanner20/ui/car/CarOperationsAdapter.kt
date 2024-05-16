package com.logomann.datascanner20.ui.car

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.logomann.datascanner20.ui.car.fragment.CarLottingFragment
import com.logomann.datascanner20.ui.car.fragment.CarSearchByPlaceFragment
import com.logomann.datascanner20.ui.car.fragment.CarSearchByVinFragment
import com.logomann.datascanner20.ui.car.fragment.CarlLotInLadungFragment
import com.logomann.datascanner20.ui.car.fragment.CarRelocationFragment

class CarOperationsAdapter(hostFragment: Fragment) : FragmentStateAdapter(hostFragment) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CarRelocationFragment()
            1 -> CarSearchByVinFragment()
            2 -> CarSearchByPlaceFragment()
            3 -> CarlLotInLadungFragment()
            4 -> CarLottingFragment()
           // 5 -> CarLoadingSheetFragment()
            else -> CarRelocationFragment()
        }

    }
}