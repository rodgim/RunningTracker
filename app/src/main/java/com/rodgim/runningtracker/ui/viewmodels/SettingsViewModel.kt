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
class SettingsViewModel @Inject constructor(
    private val setupRepository: SetupRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _weight = MutableStateFlow(0f)
    val weight = _weight.asStateFlow()

    private val _photo = MutableStateFlow("")
    val photo = _photo.asStateFlow()

    init {
        viewModelScope.launch {
            val name = setupRepository.getName()
            _name.value = name
        }
        viewModelScope.launch {
            val weight = setupRepository.getWeight()
            _weight.value = weight
        }
        viewModelScope.launch {
            val photo = setupRepository.getPhoto()
            _photo.value = photo
        }
    }

    fun saveSetup(name: String, weight: Float, photo: String) {
        viewModelScope.launch {
            setupRepository.updateSetup(name, weight, photo)
        }
    }
}