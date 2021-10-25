package es.uniovi.eii.stitchingbot.util

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

object ShowDialog {

    fun showDialogOK(context:Context, message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel") { _, _ -> }
            .create()
            .show()
    }

    fun showInfoDialog(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showNotGrantedPermissionsMessage(nonGrantedPermissions: MutableList<String>, context: Context) {
        val permissionsString = nonGrantedPermissions.reduce { acc, str -> "$acc - $str" }

        showDialogOK(
            context,
            "Se necesitan los permisos: $permissionsString"
        ) { _, _ -> }
    }
}