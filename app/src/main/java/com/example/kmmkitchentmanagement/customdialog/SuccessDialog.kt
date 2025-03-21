package com.example.kmmkitchentmanagement.customdialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.kmmkitchentmanagement.R

class SuccessDialog(private val log: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_success, null)

        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val errorMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        errorMessage.text = log

        val okButton = dialogView.findViewById<Button>(R.id.dialog_button_ok)
        okButton.setOnClickListener { dismiss() }

        return dialog
    }
}
