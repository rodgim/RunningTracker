package com.rodgim.runningtracker.ui.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.rodgim.runningtracker.R
import com.rodgim.runningtracker.ui.MainActivity
import com.rodgim.runningtracker.utils.Constants.ACTION_PAUSE_SERVICE
import com.rodgim.runningtracker.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.rodgim.runningtracker.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.rodgim.runningtracker.utils.Constants.ACTION_STOP_SERVICE
import com.rodgim.runningtracker.utils.Constants.LOCATION_UPDATE_INTERVAL
import com.rodgim.runningtracker.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.rodgim.runningtracker.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.rodgim.runningtracker.utils.Constants.NOTIFICATION_ID
import com.rodgim.runningtracker.utils.Constants.TIMER_UPDATE_INTERVAL
import com.rodgim.runningtracker.utils.TrackingUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

typealias Polyline = List<LatLng>
typealias Polylines = List<Polyline>

class TrackingService : LifecycleService() {

    private var isFirstRun = true

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val timeRunInSeconds = MutableStateFlow(0L)
    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    companion object {
        val timeRunInMillis = MutableStateFlow(0L)
        val isTracking = MutableStateFlow(false)
        val pathPoints = MutableStateFlow<Polylines>(listOf())
    }

    private fun postInitialValues() {
        isTracking.value = false
        pathPoints.value = listOf()
        timeRunInSeconds.value = 0L
        timeRunInMillis.value = 0L
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        lifecycleScope.launch {
            isTracking.collect {
                updateLocationTracking(it)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                        Timber.d("Tracking service start")
                    } else {
                        startTimer()
                        Timber.d("Tracking service resumed")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Tracking service paused")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Tracking service stopped")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer() {
        addEmptyPolyline()
        isTracking.value = true
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        lifecycleScope.launch(Dispatchers.Main) {
            while (isTracking.value) {
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.value = timeRun + lapTime
                if (timeRunInMillis.value >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.value = timeRunInSeconds.value + 1
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private fun pauseService() {
        isTracking.value = false
        isTimerEnabled = false
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermissions(this)) {
                val request = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL)
                    .build()
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value) {
                for (location in result.locations) {
                    addPathPoint(location)
                    Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
                }
            }
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(it.latitude, it.longitude)
            pathPoints.value.apply {
                val polylines = this.toMutableList()
                val polyline = polylines.last().toMutableList()
                polyline.add(pos)
                polylines.removeLast()
                polylines.add(polyline)
                pathPoints.value = polylines.toList()
            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value.apply {
        val polylines = this.toMutableList()
        polylines.add(mutableListOf())
        pathPoints.value = polylines.toList()
    }

    private fun startForegroundService() {
        startTimer()
        isTracking.value = true

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_IMMUTABLE
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}