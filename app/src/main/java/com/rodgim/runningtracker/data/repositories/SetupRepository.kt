package com.rodgim.runningtracker.data.repositories

import com.rodgim.runningtracker.data.datasources.LocalSetupDataSource
import javax.inject.Inject

class SetupRepository @Inject constructor(
    private val localSetupDataSource: LocalSetupDataSource
) {
    suspend fun saveSetup(name: String, weight: Float, firstTimeToggle: Boolean) =
        localSetupDataSource.saveSetup(name, weight, firstTimeToggle)

    suspend fun updateSetup(name: String, weight: Float) = localSetupDataSource.updateSetup(name, weight)

    suspend fun getName() = localSetupDataSource.getName()

    suspend fun getWeight() = localSetupDataSource.getWeight()

    suspend fun getFirstTimeToggle() = localSetupDataSource.getFirstTimeToggle()
}