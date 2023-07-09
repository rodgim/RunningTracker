package com.rodgim.runningtracker.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rodgim.runningtracker.R

class CancelTrackingDialog() : DialogFragment() {

    private var listener: (() -> Unit)? = null
    fun setOnButtonClickListener(listener: () -> Unit) {
        this.listener = listener
    }

    companion object {
        const val TAG = "CancelTrackingDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure to cancel the current run and delete all its data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes") { _, _ ->
                listener?.let { click ->
                    click()
                }
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
    }
}