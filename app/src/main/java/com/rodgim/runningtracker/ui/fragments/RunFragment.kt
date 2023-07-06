package com.rodgim.runningtracker.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rodgim.runningtracker.R
import com.rodgim.runningtracker.databinding.FragmentRunBinding
import com.rodgim.runningtracker.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment() {

    private lateinit var binding: FragmentRunBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private var fineLocationPermissionGranted = false
    private var coarseLocationPermissionGranted = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }

        setupLocationPermissions()
    }

    private fun setupLocationPermissions() {
        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            fineLocationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: fineLocationPermissionGranted
            coarseLocationPermissionGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: coarseLocationPermissionGranted

            if (fineLocationPermissionGranted && coarseLocationPermissionGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val hasBackgroundLocationPermission =
                        permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION]
                    if (hasBackgroundLocationPermission == false) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            processBackgroundLocationDenied(false)
                        } else {
                            processBackgroundLocationDenied(true)
                        }
                    } else if (hasBackgroundLocationPermission == true) {
                        // All permissions granted
                    } else {
                        requestLocationPermissions()
                    }
                } else {
                    // All permissions granted
                }
            }else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                processNormalLocationDenied(false)
            } else {
                processNormalLocationDenied(true)
            }
        }
        requestLocationPermissions()
    }

    private fun requestLocationPermissions() {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permissionToRequest = mutableListOf<String>()
        if (!hasFineLocationPermission) {
            permissionToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (!hasCoarseLocationPermission) {
            permissionToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (fineLocationPermissionGranted) {
                val hasBackgroundLocationPermission = ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                if (!hasBackgroundLocationPermission) {
                    permissionToRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
            }
        }

        fineLocationPermissionGranted = hasFineLocationPermission
        coarseLocationPermissionGranted = hasCoarseLocationPermission

        if (permissionToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionToRequest.toTypedArray())
        } else {
            // All permissions granted
        }
    }

    private fun processNormalLocationDenied(isPermanentlyDenied: Boolean) {
        if (isPermanentlyDenied) {
            if (coarseLocationPermissionGranted) {
                showPermissionPermanentlyDeniedDialog("For a better experience is necessary to grant the precise location permission")
            } else {
                showPermissionPermanentlyDeniedDialog("Since location has not been granted, the application will have functionality limited")
            }
        } else {
            showRequestPermissionRationaleDialog( "This location permission is necessary for the correct functioning of the app.")
        }
    }

    private fun processBackgroundLocationDenied(isPermanentlyDenied: Boolean) {
        if (isPermanentlyDenied) {
            showPermissionPermanentlyDeniedDialog("Since background location has not been granted, the application will have functionality limited")
        } else {
            showRequestPermissionRationaleDialog( "This background location permission is necessary for the correct functioning of the app.")
        }
    }

    private fun showRequestPermissionRationaleDialog(description: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission request")
            .setMessage(description)
            .setPositiveButton("Ok", null)
            .setOnDismissListener {
                requestLocationPermissions()
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
}