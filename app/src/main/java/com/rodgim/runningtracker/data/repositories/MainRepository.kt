package com.rodgim.runningtracker.data.repositories

import com.rodgim.runningtracker.data.datasources.LocalMainDataSource
import com.rodgim.runningtracker.domain.models.Run
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val localMainDataSource: LocalMainDataSource
) {
    suspend fun insertRun(run: Run) = localMainDataSource.insertRun(run)

    suspend fun deleteRun(run: Run) = localMainDataSource.deleteRun(run)

    fun getAllRunsSortedByDate(): Flow<List<Run>> = localMainDataSource.getAllRunsSortedByDate()

    fun getAllRunsSortedByAvgSpeed(): Flow<List<Run>> = localMainDataSource.getAllRunsSortedByAvgSpeed()

    fun getAllRunsSortedByDistance(): Flow<List<Run>> = localMainDataSource.getAllRunsSortedByDistance()

    fun getAllRunsSortedByTimeInMillis(): Flow<List<Run>> = localMainDataSource.getAllRunsSortedByTimeInMillis()

    fun getAllRunsSortedByCaloriesBurned(): Flow<List<Run>> = localMainDataSource.getAllRunsSortedByCaloriesBurned()

    fun getTotalDistance(): Flow<Int> = localMainDataSource.getTotalDistance()

    fun getTotalTimeInMillis(): Flow<Long> = localMainDataSource.getTotalTimeInMillis()

    fun getTotalCaloriesBurned(): Flow<Int> = localMainDataSource.getTotalCaloriesBurned()

    fun getTotalAvgSpeed(): Flow<Float> = localMainDataSource.getTotalAvgSpeed()
}