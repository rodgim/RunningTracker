package com.rodgim.runningtracker.data.datasources

import com.rodgim.runningtracker.domain.models.Run
import kotlinx.coroutines.flow.Flow

interface LocalMainDataSource {
    suspend fun insertRun(run: Run)

    suspend fun deleteRun(run: Run)

    fun getAllRunsSortedByDate(): Flow<List<Run>>

    fun getAllRunsSortedByAvgSpeed(): Flow<List<Run>>

    fun getAllRunsSortedByDistance(): Flow<List<Run>>

    fun getAllRunsSortedByTimeInMillis(): Flow<List<Run>>

    fun getAllRunsSortedByCaloriesBurned(): Flow<List<Run>>

    fun getTotalDistance(): Flow<Int>

    fun getTotalTimeInMillis(): Flow<Long>

    fun getTotalCaloriesBurned(): Flow<Int>

    fun getTotalAvgSpeed(): Flow<Float>

}
