package com.rodgim.runningtracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rodgim.runningtracker.databinding.FragmentStatisticsBinding
import com.rodgim.runningtracker.ui.viewmodels.StatisticsViewModel
import com.rodgim.runningtracker.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.totalTimeRun.collect {
                    binding.tvTotalTime.text = TrackingUtility.getFormattedStopWatchTime(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.totalDistance.collect {
                    binding.tvTotalDistance.text = "${round(((it / 1000f) * 10f) / 10f)}km"
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.totalAvgSpeed.collect {
                    binding.tvAverageSpeed.text = "${round(it * 10f) / 10f}km/h"
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.totalCaloriesBurned.collect {
                    binding.tvTotalCalories.text = "${it}kcal"
                }
            }
        }
    }
}