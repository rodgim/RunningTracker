package com.rodgim.runningtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodgim.runningtracker.data.repositories.MainRepository
import com.rodgim.runningtracker.domain.models.Run
import com.rodgim.runningtracker.utils.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    /*private val runsSortedByDate: StateFlow<List<Run>> = mainRepository.getAllRunsSortedByDate()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private val runsSortedByDistance = mainRepository.getAllRunsSortedByDistance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private val runsSortedByCaloriesBurned = mainRepository.getAllRunsSortedByCaloriesBurned()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private val runsSortedByTimeInMillis = mainRepository.getAllRunsSortedByTimeInMillis()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private val runsSortedByAvgSpeed = mainRepository.getAllRunsSortedByAvgSpeed()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var sortType = SortType.DATE
    val runsCombine: StateFlow<List<Run>> = combine(
        runsSortedByDate,
        runsSortedByDistance,
        runsSortedByCaloriesBurned,
        runsSortedByTimeInMillis,
        runsSortedByAvgSpeed
    ) { listDate, listDistance, listCaloriesBurned, listTimeInMillis, listAvgSpeed ->
        when(sortType) {
            SortType.DATE -> listDate
            SortType.RUNNING_TIME -> listTimeInMillis
            SortType.AVG_SPEED -> listAvgSpeed
            SortType.DISTANCE -> listDistance
            SortType.CALORIES_BURNED -> listCaloriesBurned
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = mutableListOf()
    )*/
    private val runsSortedByDate = MutableStateFlow<List<Run>>(emptyList())
    private val runsSortedByDistance = MutableStateFlow<List<Run>>(emptyList())
    private val runsSortedByCaloriesBurned = MutableStateFlow<List<Run>>(emptyList())
    private val runsSortedByTimeInMillis = MutableStateFlow<List<Run>>(emptyList())
    private val runsSortedByAvgSpeed = MutableStateFlow<List<Run>>(emptyList())

    private val runsCombine = combine(
        mainRepository.getAllRunsSortedByDate(),
        mainRepository.getAllRunsSortedByDistance(),
        mainRepository.getAllRunsSortedByCaloriesBurned(),
        mainRepository.getAllRunsSortedByTimeInMillis(),
        mainRepository.getAllRunsSortedByAvgSpeed()
    ) { listDate, listDistance, listCaloriesBurned, listTimeInMillis, listAvgSpeed ->
        runsSortedByDate.value = listDate
        runsSortedByTimeInMillis.value = listTimeInMillis
        runsSortedByAvgSpeed.value = listAvgSpeed
        runsSortedByDistance.value = listDistance
        runsSortedByCaloriesBurned.value = listCaloriesBurned

        when(sortType) {
            SortType.DATE -> listDate
            SortType.RUNNING_TIME -> listTimeInMillis
            SortType.AVG_SPEED -> listAvgSpeed
            SortType.DISTANCE -> listDistance
            SortType.CALORIES_BURNED -> listCaloriesBurned
        }
    }

    var sortType = SortType.DATE

    private val _runs = MutableStateFlow<List<Run>>(emptyList())
    val runs = _runs.asStateFlow()


    init {
        viewModelScope.launch {
            runsCombine.collect {
                _runs.value = it
            }
        }
    }

    fun sortRuns(sortType: SortType) {
        this.sortType = sortType
        _runs.value = when(sortType) {
            SortType.DATE -> runsSortedByDate.value
            SortType.RUNNING_TIME -> runsSortedByTimeInMillis.value
            SortType.AVG_SPEED -> runsSortedByAvgSpeed.value
            SortType.DISTANCE -> runsSortedByDistance.value
            SortType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value
        }
    }

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }
}
