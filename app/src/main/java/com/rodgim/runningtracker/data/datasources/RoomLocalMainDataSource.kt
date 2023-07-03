package com.rodgim.runningtracker.data.datasources

import com.rodgim.runningtracker.data.db.RunDao
import com.rodgim.runningtracker.data.wrappers.toRunDomain
import com.rodgim.runningtracker.data.wrappers.toRunEntity
import com.rodgim.runningtracker.domain.models.Run
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalMainDataSource(
    private val runDao: RunDao
): LocalMainDataSource {
    override suspend fun insertRun(run: Run) = runDao.insertRun(run.toRunEntity())

    override suspend fun deleteRun(run: Run) = runDao.deleteRun(run.toRunEntity())

    override fun getAllRunsSortedByDate(): Flow<List<Run>> {
        return runDao.getAllRunsSortedByDate().map { list -> list.map { it.toRunDomain() } }
    }

    override fun getAllRunsSortedByAvgSpeed(): Flow<List<Run>> {
        return runDao.getAllRunsSortedByAvgSpeed().map { list -> list.map { it.toRunDomain() } }
    }

    override fun getAllRunsSortedByDistance(): Flow<List<Run>> {
        return runDao.getAllRunsSortedByDistance().map { list -> list.map { it.toRunDomain() } }
    }

    override fun getAllRunsSortedByTimeInMillis(): Flow<List<Run>> {
        return runDao.getAllRunsSortedByTimeInMillis().map { list -> list.map { it.toRunDomain() } }
    }

    override fun getAllRunsSortedByCaloriesBurned(): Flow<List<Run>> {
        return runDao.getAllRunsSortedByCaloriesBurned().map { list -> list.map { it.toRunDomain() } }
    }

    override fun getTotalDistance(): Flow<Int> = runDao.getTotalDistance()

    override fun getTotalTimeInMillis(): Flow<Long> = runDao.getTotalTimeInMillis()

    override fun getTotalCaloriesBurned(): Flow<Int> = runDao.getTotalCaloriesBurned()

    override fun getTotalAvgSpeed(): Flow<Float> = runDao.getTotalAvgSpeed()
}