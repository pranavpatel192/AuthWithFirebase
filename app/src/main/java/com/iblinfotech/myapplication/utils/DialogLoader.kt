package com.dev.practical.extra

import android.view.LayoutInflater
import android.widget.ProgressBar
import android.graphics.drawable.ColorDrawable
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.iblinfotech.myapplication.R

class DialogLoader(private val context: Context) {
    private var alertDialog: AlertDialog? = null
    private val layoutInflater: LayoutInflater
    private fun initDialog() {
        val dialog = AlertDialog.Builder(
            context
        )
        val dialogView = layoutInflater.inflate(R.layout.layout_progress_dialog, null)
        dialog.setView(dialogView)
        dialog.setCancelable(false)
        val dialog_progressBar = dialogView.findViewById<ProgressBar>(R.id.dialog_progressBar)
        dialog_progressBar.isIndeterminate = true
        alertDialog = dialog.create()
        alertDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (alertDialog!!.window != null) alertDialog!!.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
    }

    fun showProgressDialog() {
        (context as Activity).runOnUiThread { alertDialog!!.show() }
    }

    fun hideProgressDialog() {
        alertDialog!!.dismiss()
    }

    init {
        layoutInflater = LayoutInflater.from(context)
        initDialog()
    }
}