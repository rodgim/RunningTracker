package com.rodgim.runningtracker.data.wrappers

import com.rodgim.runningtracker.domain.models.Run
import com.rodgim.runningtracker.data.db.models.Run as RunEntity

fun Run.toRunEntity(): RunEntity = RunEntity(
    id = id ?: 0,
    img = img,
    timestamp = timestamp,
    avgSpeedInKMH = avgSpeedInKMH,
    distanceInMeters = distanceInMeters,
    timeInMillis = timeInMillis,
    caloriesBurned = caloriesBurned
)

fun RunEntity.toRunDomain(): Run = Run(
    id = id,
    img = img,
    timestamp = timestamp,
    avgSpeedInKMH = avgSpeedInKMH,
    distanceInMeters = distanceInMeters,
    timeInMillis = timeInMillis,
    caloriesBurned = caloriesBurned
)
