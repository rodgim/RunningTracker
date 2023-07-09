package com.rodgim.runningtracker.data.datasources

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val KEY_FIRST_TIME_TOGGLE = "KEY_FIRST_TIME_TOGGLE"
const val KEY_NAME = "KEY_NAME"
const val KEY_WEIGHT = "KEY_WEIGHT"
const val DEFAULT_WEIGHT = 80f

class SharedPreferencesLocalSetupDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : LocalSetupDataSource {
    override suspend fun saveSetup(name: String, weight: Float, firstTimeToggle: Boolean) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit()
                .putString(KEY_NAME, name)
                .putFloat(KEY_WEIGHT, weight)
                .putBoolean(KEY_FIRST_TIME_TOGGLE, firstTimeToggle)
                .apply()
        }
    }

    override suspend fun updateSetup(name: String, weight: Float) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit()
                .putString(KEY_NAME, name)
                .putFloat(KEY_WEIGHT, weight)
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

    override suspend fun getFirstTimeToggle(): Boolean {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE, true)
        }
    }
}