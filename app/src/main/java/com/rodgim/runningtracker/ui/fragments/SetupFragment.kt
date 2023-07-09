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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rodgim.runningtracker.R
import com.rodgim.runningtracker.databinding.FragmentSetupBinding
import com.rodgim.runningtracker.ui.viewmodels.SetupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SetupFragment : Fragment() {

    private lateinit var binding: FragmentSetupBinding
    private val viewModel: SetupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isFirstTimeToggle.collect { isFirstAppOpen ->
                    if (!isFirstAppOpen) {
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.setupFragment, true)
                            .build()
                        findNavController().navigate(
                            R.id.action_setupFragment_to_runFragment,
                            savedInstanceState,
                            navOptions
                        )
                    }
                }
            }
        }

        binding.tvContinue.setOnClickListener {
            val success = savePersonalData()
            if (success) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            } else {
                Snackbar.make(requireView(), "Please enter all the fields", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun savePersonalData(): Boolean {
        val name = binding.etName.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }
        viewModel.saveSetup(name, weight.toFloat(), false)
        return true
    }
}