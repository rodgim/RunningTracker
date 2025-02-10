package com.rodgim.runningtracker.ui.fragments

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.rodgim.runningtracker.R
import com.rodgim.runningtracker.databinding.FragmentTrackingBinding
import com.rodgim.runningtracker.domain.models.Run
import com.rodgim.runningtracker.ui.dialogs.CancelTrackingDialog
import com.rodgim.runningtracker.ui.services.Polyline
import com.rodgim.runningtracker.ui.services.TrackingService
import com.rodgim.runningtracker.ui.viewmodels.MainViewModel
import com.rodgim.runningtracker.utils.Constants.ACTION_PAUSE_SERVICE
import com.rodgim.runningtracker.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.rodgim.runningtracker.utils.Constants.ACTION_STOP_SERVICE
import com.rodgim.runningtracker.utils.Constants.MAP_ZOOM
import com.rodgim.runningtracker.utils.Constants.POLYLINE_COLOR
import com.rodgim.runningtracker.utils.Constants.POLYLINE_WIDTH
import com.rodgim.runningtracker.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private lateinit var binding: FragmentTrackingBinding
    private val viewModel: MainViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = listOf<Polyline>()

    private var map: MapboxMap? = null

    private var curTimeInMillis = 0L
    private lateinit var permissionsLauncher: ActivityResultLauncher<String>

    private var menu: Menu? = null

    private var weight = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map = binding.mapView.mapboxMap
        map?.loadStyle(Style.MAPBOX_STREETS) {
            addAllPolylines()
        }

        binding.btnToggleRun.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (TrackingUtility.hasPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)) {
                    toggleRun()
                } else {
                    requestNotificationPermission()
                }
            } else {
                toggleRun()
            }
        }

        binding.btnFinishRun.setOnClickListener {
            zoomToSeeTheFullTrack()
            endRunAndSaveToDb()
        }

        savedInstanceState?.let { savedBundle->
            val cancelTrackingDialog = parentFragmentManager.findFragmentByTag(CancelTrackingDialog.TAG) as CancelTrackingDialog?
            cancelTrackingDialog?.setOnButtonClickListener {
                stopRun()
            }
        }

        subscribeToTrackingService()
        setupMenu()
    }

    private fun subscribeToTrackingService() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                TrackingService.isTracking.collect {
                    if (isTracking != it) {
                        updateTracking(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                TrackingService.pathPoints.collect {
                    pathPoints = it
                    addLatestPolyline()
                    moveCameraToUser()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                TrackingService.timeRunInMillis.collect {
                    curTimeInMillis = it
                    val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, true)
                    binding.tvTimer.text = formattedTime
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.weight.collect {
                    weight = it
                }
            }
        }
    }

    private fun toggleRun() {
        if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (isTracking) {
            binding.btnToggleRun.text = "Stop"
            binding.btnFinishRun.visibility = View.GONE
            menu?.getItem(0)?.isVisible = true
        } else {
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.visibility = View.VISIBLE
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            val point = pathPoints.last().last()
            map?.easeTo(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(point.longitude, point.latitude))
                    .zoom(MAP_ZOOM.toDouble())
                    .build(),
                MapAnimationOptions.mapAnimationOptions {
                    duration(1000)
                }
            )
        }
    }

    private fun zoomToSeeTheFullTrack() {
        val bounds = pathPoints.flatten().map {
            Point.fromLngLat(it.longitude, it.latitude)
        }
        val padding = (binding.mapView.height * 0.05f).toInt()
        map?.cameraForCoordinates(
            coordinates = bounds,
            camera = cameraOptions {  },
            coordinatesPadding = EdgeInsets(padding.toDouble(), 0.0, padding.toDouble(), 0.0),
            maxZoom = null,
            offset = null,
        ) {
            map?.easeTo(it, MapAnimationOptions.mapAnimationOptions { duration(1000) })
        }
    }

    private fun endRunAndSaveToDb() {
        binding.mapView.snapshot { bmp ->
            var distanceInMeters = 0
            for (polyline in pathPoints) {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed = round((distanceInMeters / 1000) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000) * weight).toInt()
            val run = Run(
                img = bmp,
                timestamp = dateTimestamp,
                avgSpeedInKMH = avgSpeed,
                distanceInMeters = distanceInMeters,
                timeInMillis = curTimeInMillis,
                caloriesBurned = caloriesBurned
            )
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Run saved successfully",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    private fun addAllPolylines() {
        val annotationManager = binding.mapView.annotations.createPolylineAnnotationManager()
        annotationManager.create(
            PolylineAnnotationOptions()
                .withPoints(pathPoints.flatten().map { Point.fromLngLat(it.longitude, it.latitude) })
                .withLineColor(POLYLINE_COLOR)
                .withLineWidth(POLYLINE_WIDTH.toDouble())
        )
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            Timber.d("LAST SIZE=${pathPoints.last().size}")
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val preLastPoint = Point.fromLngLat(preLastLatLng.longitude, preLastLatLng.latitude)
            val lastPoint = Point.fromLngLat(lastLatLng.longitude, lastLatLng.latitude)
            val annotationManager = binding.mapView.annotations.createPolylineAnnotationManager()
            annotationManager.create(
                PolylineAnnotationOptions()
                    .withPoints(listOf(preLastPoint, lastPoint))
                    .withLineColor(POLYLINE_COLOR)
                    .withLineWidth(POLYLINE_WIDTH.toDouble())
            )
        }
    }

    private fun sendCommandToService(action: String) {
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (!it) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    showRequestPermissionRationaleDialog("This notification permission is necessary for the correct functioning of the app.")
                } else {
                    showPermissionPermanentlyDeniedDialog("This notification permission is necessary for the correct functioning of the app.")
                }
            }
        }

        permissionsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showRequestPermissionRationaleDialog(description: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission request")
            .setMessage(description)
            .setPositiveButton("Ok", null)
            .setOnDismissListener {
                requestNotificationPermission()
            }
            .show()
    }

    private fun showPermissionPermanentlyDeniedDialog(description: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission request")
            .setMessage(description)
            .setPositiveButton("Go to Settings"
            ) { _, _ ->
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", activity?.packageName, null)
                    startActivity(this)
                }
            }
            .show()
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_tracking_menu, menu)
                this@TrackingFragment.menu = menu
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.cancelTracking -> showCancelTrackingDialog()
                }
                return true
            }

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                if (curTimeInMillis > 0L) {
                    menu.getItem(0)?.isVisible = true
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showCancelTrackingDialog() {
        CancelTrackingDialog().apply {
            setOnButtonClickListener {
                stopRun()
            }
        }.show(parentFragmentManager, CancelTrackingDialog.TAG)
    }

    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }
}