package com.rodgim.runningtracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rodgim.runningtracker.databinding.ItemRunBinding
import com.rodgim.runningtracker.domain.models.Run
import com.rodgim.runningtracker.utils.TrackingUtility
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RunAdapter : ListAdapter<Run, RunAdapter.ViewHolder>(RunDiffUtil()) {
    inner class ViewHolder(private val itemRunView: ItemRunBinding) : RecyclerView.ViewHolder(itemRunView.root) {
        fun bind(item: Run) {
            itemRunView.apply {
                Glide.with(itemView.context).load(item.img).into(ivRunImage)
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = item.timestamp
                }
                val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
                tvDate.text = dateFormat.format(calendar.time)
                tvAvgSpeed.text = "${item.avgSpeedInKMH}km/h"
                tvDistance.text = "${item.distanceInMeters / 1000f}km"
                tvTime.text = TrackingUtility.getFormattedStopWatchTime(item.timeInMillis)
                tvCalories.text = "${item.caloriesBurned}kcal"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRunBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class RunDiffUtil : DiffUtil.ItemCallback<Run>() {
    override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
        return oldItem.id == newItem.id
                && oldItem.timeInMillis == newItem.timeInMillis
                && oldItem.timestamp == newItem.timestamp
    }
}
