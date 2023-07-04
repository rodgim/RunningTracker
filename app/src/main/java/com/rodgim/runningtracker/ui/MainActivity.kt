package com.rodgim.runningtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.rodgim.runningtracker.R
import com.rodgim.runningtracker.databinding.ActivityMainBinding
import com.rodgim.runningtracker.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            navigateToTrackingFragmentIfNeeded(intent)
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment)
            navHostFragment?.let { navHost ->
                bottomNavigationView.setupWithNavController(navHost.findNavController())
                navHost.findNavController().addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.runFragment, R.id.statisticsFragment, R.id.settingsFragment -> {
                            bottomNavigationView.visibility = View.VISIBLE
                        }
                        else -> {
                            bottomNavigationView.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment)
            navHostFragment?.let { navHost ->
                navHost.findNavController().navigate(R.id.action_global_trackingFragment)
            }
        }
    }
}