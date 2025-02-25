package com.rodgim.runningtracker.data.repositories

import com.rodgim.runningtracker.data.datasources.LocalSetupDataSource
import javax.inject.Inject

class SetupRepository @Inject constructor(
    private val localSetupDataSource: LocalSetupDataSource
) {
    suspend fun saveSetup(name: String, weight: Float, firstTimeToggle: Boolean, photo: String) =
        localSetupDataSource.saveSetup(name, weight, firstTimeToggle, photo)

    suspend fun updateSetup(name: String, weight: Float, photo: String) = localSetupDataSource.updateSetup(name, weight, photo)

    suspend fun getName() = localSetupDataSource.getName()

    suspend fun getWeight() = localSetupDataSource.getWeight()

    suspend fun getPhoto() = localSetupDataSource.getPhoto()

    suspend fun getFirstTimeToggle() = localSetupDataSource.getFirstTimeToggle()
}