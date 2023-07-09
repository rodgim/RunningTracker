package com.rodgim.runningtracker.ui.views

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.rodgim.runningtracker.R
import com.rodgim.runningtracker.domain.models.Run
import com.rodgim.runningtracker.utils.TrackingUtility
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CustomMarkerView(
    val runs: List<Run>,
    c: Context,
    layoutId: Int
) : MarkerView(c, layoutId){

    private val tvDate: TextView = findViewById(R.id.tvDate)
    private val tvAvgSpeed: TextView = findViewById(R.id.tvAvgSpeed)
    private val tvDistance: TextView = findViewById(R.id.tvDistance)
    private val tvDuration: TextView = findViewById(R.id.tvDuration)
    private val tvCaloriesBurned: TextView = findViewById(R.id.tvCaloriesBurned)

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        e?.let { entry ->
            val curRunId = entry.x.toInt()
            val item = runs[curRunId]
            val calendar = Calendar.getInstance().apply {
                timeInMillis = item.timestamp
            }

            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)
            tvAvgSpeed.text = "${item.avgSpeedInKMH}km/h"
            tvDistance.text = "${item.distanceInMeters / 1000f}km"
            tvDuration.text = TrackingUtility.getFormattedStopWatchTime(item.timeInMillis)
            tvCaloriesBurned.text = "${item.caloriesBurned}kcal"
        }
    }
}