package com.rodgim.runningtracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.rodgim.runningtracker.R
import com.rodgim.runningtracker.databinding.FragmentSettingsBinding
import com.rodgim.runningtracker.ui.MainActivity
import com.rodgim.runningtracker.ui.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnApplyChanges.setOnClickListener {
            val success = savePersonalData()
            if (success) {
                Snackbar.make(view, "Saved changes", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(view, "Please fill out all the fields", Snackbar.LENGTH_LONG).show()
            }
        }
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.name.collect {
                    binding.etName.setText(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.weight.collect {
                    binding.etWeight.setText(it.toString())
                }
            }
        }
    }

    private fun savePersonalData(): Boolean {
        val name = binding.etName.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }
        viewModel.saveSetup(name, weight.toFloat())
        val toolbarText = "Let's go, $name"
        (requireActivity() as MainActivity).findViewById<TextView>(R.id.tvToolbarTitle).text = toolbarText
        return true
    }
}