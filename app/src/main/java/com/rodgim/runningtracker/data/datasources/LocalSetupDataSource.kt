package com.rodgim.runningtracker.data.datasources

interface LocalSetupDataSource {
    suspend fun saveSetup(name: String, weight: Float, firstTimeToggle: Boolean)
    suspend fun updateSetup(name: String, weight: Float)
    suspend fun getName(): String
    suspend fun getWeight(): Float
    suspend fun getFirstTimeToggle(): Boolean
}