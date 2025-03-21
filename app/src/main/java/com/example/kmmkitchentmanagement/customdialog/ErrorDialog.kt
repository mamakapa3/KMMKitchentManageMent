package com.example.kmmkitchentmanagement.customdialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.kmmkitchentmanagement.R

class ErrorDialog(
        private val log: String,
        private val shouldFinishActivity: Boolean
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())

        // Inflate layout custom
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_error, null)

        // Set the custom view to the dialog
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set the error message in the TextView
        val errorMessage: TextView = dialogView.findViewById(R.id.dialog_message)
        errorMessage.text = log

        val okButton: Button = dialogView.findViewById(R.id.dialog_button_ok)
        okButton.setOnClickListener {
            dismiss()
        }

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("ErrorDialog", "onDismiss triggered")
        if (shouldFinishActivity) {
            Log.d("ErrorDialog", "Dialog dismissed and shouldFinishActivity is true")
            requireActivity().finish()
        }
    }
}
