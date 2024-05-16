package com.logomann.datascanner20.ui.car

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.logomann.datascanner20.databinding.CarLottingRecyclerItemBinding


class DriverAdapter(
    private val cars: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<DriverViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DriverViewHolder(CarLottingRecyclerItemBinding.inflate(layoutInflater,parent,false), cars)
    }

    override fun getItemCount(): Int {
        return cars.size
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        holder.bind(cars[position])
        holder.binding.deleteBtn.setOnClickListener {
            onItemClick(cars[position])
        }
    }
}