package es.uniovi.eii.stitchingbot.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object ShowDialog {

    fun showDialogOK(context:Context, message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }
}