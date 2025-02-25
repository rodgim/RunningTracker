package com.rodgim.runningtracker.ui.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
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
import com.rodgim.runningtracker.utils.Constants.FILE_PROVIDER
import com.rodgim.runningtracker.utils.PictureUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

@AndroidEntryPoint
class SetupFragment : Fragment() {

    private lateinit var binding: FragmentSetupBinding
    private val viewModel: SetupViewModel by viewModels()

    private var photoName: String = ""
    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto ->
        if (didTakePhoto && photoName.isNotEmpty()) {
            updatePhoto(photoName)
        }
    }

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

        binding.btnProfilePhoto.setOnClickListener {
            photoName = "IMG_${Date()}.JPG"
            val photoFile = File(requireContext().applicationContext.filesDir, photoName)
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                FILE_PROVIDER,
                photoFile
            )
            takePhoto.launch(photoUri)
        }

        binding.tvContinue.setOnClickListener {
            val success = savePersonalData()
            if (success) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            } else {
                Snackbar.make(requireView(), "Please enter all the fields", Snackbar.LENGTH_SHORT).show()
            }
        }

        checkIfCameraIntentCanBeResolved()
    }

    private fun savePersonalData(): Boolean {
        val name = binding.etName.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }
        viewModel.saveSetup(name, weight.toFloat(), false, photoName)
        return true
    }

    private fun updatePhoto(photoFileName: String?) {
        if (binding.ivProfilePhoto.tag != photoFileName) {
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }

            if (photoFile?.exists() == true) {
                binding.ivProfilePhoto.doOnLayout { measureView ->
                    val scaleBitmap = PictureUtils.getScaleBitmap(
                        photoFile.path,
                        measureView.width,
                        measureView.height
                    )
                    binding.ivProfilePhoto.setImageBitmap(scaleBitmap)
                    binding.ivProfilePhoto.tag = photoFileName
                }
            } else {
                binding.ivProfilePhoto.setImageBitmap(null)
                binding.ivProfilePhoto.tag = null
            }
        }
    }

    private fun checkIfCameraIntentCanBeResolved() {
        val photoname = "IMG_${Date()}.JPG"
        val photoFile = File(requireContext().applicationContext.filesDir, photoname)
        val photoUriExample = FileProvider.getUriForFile(
            requireContext(),
            FILE_PROVIDER,
            photoFile
        )

        val captureImageIntent = takePhoto.contract.createIntent(
            requireContext(),
            photoUriExample
        )
        binding.btnProfilePhoto.isEnabled = canResolveIntent(captureImageIntent)
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val resolveActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolveActivity != null
    }
}