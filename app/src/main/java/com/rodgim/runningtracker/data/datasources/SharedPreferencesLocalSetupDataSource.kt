package com.rodgim.runningtracker.data.datasources

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val KEY_FIRST_TIME_TOGGLE = "KEY_FIRST_TIME_TOGGLE"
const val KEY_NAME = "KEY_NAME"
const val KEY_WEIGHT = "KEY_WEIGHT"
const val KEY_PHOTO = "KEY_PHOTO"
const val DEFAULT_WEIGHT = 80f

class SharedPreferencesLocalSetupDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : LocalSetupDataSource {
    override suspend fun saveSetup(name: String, weight: Float, firstTimeToggle: Boolean, photo: String) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit()
                .putString(KEY_NAME, name)
                .putFloat(KEY_WEIGHT, weight)
                .putString(KEY_PHOTO, photo)
                .putBoolean(KEY_FIRST_TIME_TOGGLE, firstTimeToggle)
                .apply()
        }
    }

    override suspend fun updateSetup(name: String, weight: Float, photo: String) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit()
                .putString(KEY_NAME, name)
                .putFloat(KEY_WEIGHT, weight)
                .putString(KEY_PHOTO, photo)
                .apply()
        }
    }

    override suspend fun getName(): String {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(KEY_NAME, "") ?: ""
        }
    }

    override suspend fun getWeight(): Float {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getFloat(KEY_WEIGHT, DEFAULT_WEIGHT)
        }
    }

    override suspend fun getPhoto(): String {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(KEY_PHOTO, "") ?: ""
        }
    }

    override suspend fun getFirstTimeToggle(): Boolean {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE, true)
        }
    }
}