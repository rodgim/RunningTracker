package com.rodgim.runningtracker.data.datasources

interface LocalSetupDataSource {
    suspend fun saveSetup(name: String, weight: Float, firstTimeToggle: Boolean, photo: String)
    suspend fun updateSetup(name: String, weight: Float, photo: String)
    suspend fun getName(): String
    suspend fun getWeight(): Float
    suspend fun getPhoto(): String
    suspend fun getFirstTimeToggle(): Boolean
}