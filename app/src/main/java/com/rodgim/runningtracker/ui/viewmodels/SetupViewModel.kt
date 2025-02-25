package com.rodgim.runningtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodgim.runningtracker.data.repositories.SetupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val setupRepository: SetupRepository
) : ViewModel() {

    private val _isFirstTimeToggle = MutableStateFlow(true)
    val isFirstTimeToggle = _isFirstTimeToggle.asStateFlow()

    init {
        viewModelScope.launch {
            val firstTimeToggle = setupRepository.getFirstTimeToggle()
            _isFirstTimeToggle.value = firstTimeToggle
        }
    }

    fun saveSetup(name: String, weight: Float, firstTimeToggle: Boolean, photo: String) {
        viewModelScope.launch {
            setupRepository.saveSetup(name, weight, firstTimeToggle, photo)
        }
    }
}