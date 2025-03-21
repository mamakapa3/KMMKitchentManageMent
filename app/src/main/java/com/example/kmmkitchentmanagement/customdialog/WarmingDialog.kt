package com.example.kmmkitchentmanagement.customdialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.kmmkitchentmanagement.R

class WarmingDialog(
    private val logHTML: String,
    private val leftBtnText: String,
    private val rightBtnText: String
) : DialogFragment() {

    private lateinit var dialogMessage: TextView
    private lateinit var okButton: Button
    private lateinit var skipButton: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_warming, null)

        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogMessage = dialogView.findViewById(R.id.dialog_message)
        dialogMessage.text = Html.fromHtml(logHTML, Html.FROM_HTML_MODE_COMPACT)

        okButton = dialogView.findViewById(R.id.dialog_button_sent)
        okButton.text = rightBtnText
        okButton.setOnClickListener {
            val result = Bundle().apply { putBoolean("result", true) }
            parentFragmentManager.setFragmentResult(tag ?: "", result)
            dismiss()
        }

        skipButton = dialogView.findViewById(R.id.dialog_button_skip)
        skipButton.text = leftBtnText
        skipButton.setOnClickListener {
            val result = Bundle().apply { putBoolean("result", false) }
            parentFragmentManager.setFragmentResult(tag ?: "", result)
            dismiss()
        }

        return dialog
    }
}
