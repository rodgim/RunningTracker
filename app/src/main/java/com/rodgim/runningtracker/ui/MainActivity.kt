package com.rodgim.runningtracker.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.rodgim.runningtracker.R
import com.rodgim.runningtracker.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
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
}