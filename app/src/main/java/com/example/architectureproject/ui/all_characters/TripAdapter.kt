package com.example.architectureproject.ui.all_characters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.architectureproject.data.models.Trip
import com.example.architectureproject.databinding.ItemLayoutBinding

class TripAdapter(val trips:List<Trip>, val callBack: TripListener)
    : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    interface TripListener {
        fun onTripClick(index: Int)
        fun onTripLongClick(index: Int)
        fun onEditButtonClick(trip: Trip)
        fun onDeleteButtonClick(trip: Trip)
    }

    inner class TripViewHolder(private val binding: ItemLayoutBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)

            binding.editBtn.setOnClickListener {
                callBack.onEditButtonClick(trips[adapterPosition])
            }

            binding.deleteBtn.setOnClickListener {
                callBack.onDeleteButtonClick(trips[adapterPosition])
            }
        }

        override fun onClick(v: View?) {
            callBack.onTripClick(adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            callBack.onTripLongClick(adapterPosition)
            return true
        }

        fun bind(trip: Trip) {
            binding.tripTitle.text = trip.title
            binding.tripDescription.text = trip.description
            Glide.with(binding.root).load(trip.photo).circleCrop().into(binding.itemImage)
            binding.tripLocation.text = trip.location
        }
    }

    fun tripAt(position: Int) = trips[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TripViewHolder(ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) =
        holder.bind(trips[position])

    override fun getItemCount() = trips.size

}