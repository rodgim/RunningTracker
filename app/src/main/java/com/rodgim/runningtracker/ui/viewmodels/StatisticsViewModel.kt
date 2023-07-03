package com.rodgim.runningtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.rodgim.runningtracker.data.repositories.MainRepository
import javax.inject.Inject

class StatisticsViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

}
