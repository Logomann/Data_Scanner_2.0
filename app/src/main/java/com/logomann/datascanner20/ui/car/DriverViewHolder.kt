package com.logomann.datascanner20.ui.car

import androidx.recyclerview.widget.RecyclerView
import com.logomann.datascanner20.databinding.CarLottingRecyclerItemBinding


class DriverViewHolder(
    val binding: CarLottingRecyclerItemBinding,
    private val carsList: List<String>
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(vin: String) {
        val count = carsList.indexOf(vin) + 1
        binding.carCount.text = count.toString()
        binding.vinCode.text = vin
    }
}