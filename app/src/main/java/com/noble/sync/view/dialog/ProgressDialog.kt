package com.noble.sync.view.dialog

import android.app.Activity
import android.app.AlertDialog
import android.widget.TextView
import com.noble.sync.R

class ProgressDialog(private val activity: Activity) {
    private var initialized = false
    private lateinit var dialog: AlertDialog
    private lateinit var title: TextView

    fun show(title: String? = null) {
        initializer()
        if(title != null) updateTitle(title)
        dialog.show()
    }

    fun hidden() {
        if (this::dialog.isInitialized) dialog.dismiss()
    }

    fun updateTitle(title: String) {
        if(initialized) this.title.text = title
    }

    private fun initializer() {
        if(!this::dialog.isInitialized) {
            initialized = true
            val builder = AlertDialog.Builder(activity)
            val view = activity.layoutInflater.inflate(R.layout.dialog_progress_indicator, null)
            title = view.findViewById(R.id.dialog_title)
            builder.setView(view)
            builder.setCancelable(false)
            dialog = builder.create()
            dialog.window?.setBackgroundDrawableResource(R.color.secondary)
        }
    }
}