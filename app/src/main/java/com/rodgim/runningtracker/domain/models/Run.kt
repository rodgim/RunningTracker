package com.rodgim.runningtracker.domain.models

import android.graphics.Bitmap

data class Run(
    var id: Int? = 0,
    var img: Bitmap? = null,
    var timestamp: Long = 0L,
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var timeInMillis: Long = 0,
    var caloriesBurned: Int = 0
)
