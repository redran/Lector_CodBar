package es.leocaudete.lectorcodbar.utils

import android.app.AlertDialog
import android.content.Context

class ShowMessages {

    fun showAlert(title:String, message:String, context: Context, func:()->Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            func.invoke()
        }
        builder.setNeutralButton(android.R.string.cancel, null)
        builder.show()
    }
}