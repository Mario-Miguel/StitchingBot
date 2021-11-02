package es.uniovi.eii.stitchingbot.controller

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import es.uniovi.eii.stitchingbot.database.SewingMachineDatabaseConnection
import es.uniovi.eii.stitchingbot.model.SewingMachine
import es.uniovi.eii.stitchingbot.util.Constants
import java.io.File

class SewingMachineController {

    private var sewingMachine = SewingMachine()
    private val imageManager = ImageManager()

    fun setSewingMachine(name: String? = sewingMachine.name, imgUrl : String? = sewingMachine.imgUrl, motorSteps: Int=sewingMachine.motorSteps){
        val auxSewingMachine = SewingMachine(sewingMachine.id, name,imgUrl, motorSteps)
        this.sewingMachine = auxSewingMachine
    }

    fun setSewingMachine(sewingMachine: SewingMachine){
        this.sewingMachine=sewingMachine
    }

    fun getSewingMachine(): SewingMachine{
        return sewingMachine
    }

    fun addSewingMachine(context:Context){
        val databaseConnection = SewingMachineDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.insert(sewingMachine)
        databaseConnection.close()
    }

    fun updateSewingMachine(context:Context){
        Log.d("SMDETAILS2", "SewingMachine to update: $sewingMachine")
        val databaseConnection = SewingMachineDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.update(sewingMachine)
        databaseConnection.close()
    }

    fun deleteSewingMachine(context:Context){
        imageManager.deleteImageFile(getSewingMachine().imgUrl)
        val databaseConnection = SewingMachineDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.delete(sewingMachine)
        databaseConnection.close()
    }


    fun getAllSewingMachines(context:Context): ArrayList<SewingMachine>{
        val databaseConnection =SewingMachineDatabaseConnection(context)
        databaseConnection.open()
        val list = databaseConnection.getAllData()
        databaseConnection.close()

        return list
    }

    fun isSewingMachineSelected(): Boolean {
        return getSewingMachine().id != -1
    }

    fun getImageFromUrl(activity: Activity, imgUrl: String?): Bitmap? {
        return imageManager.getImageFromUri(
            imgUrl,
            activity
        )
    }

    fun createImageFile(activity: Activity, action: (Uri, File) -> Unit) {
        imageManager.createPhotoFile(activity)?.also {
            Log.i(Constants.TAG_SEWINGMACHINE, it.absolutePath)
            val photoURI: Uri = FileProvider.getUriForFile(
                activity.applicationContext,
                "es.uniovi.eii.stitchingbot",
                it
            )

            imageManager.deleteImageIfSaved(getSewingMachine().imgUrl)
            setSewingMachine(imgUrl = photoURI.toString())
            action(photoURI, it)
        }
    }

    fun getImage(activity: Activity): Bitmap {
        return imageManager.getImageFromUri(
            getSewingMachine().imgUrl,
            activity
        )!!
    }

    fun copyImageFromFile(selectedImage: String, file: File, activity:Activity) {
        imageManager.copyImageFromGallery(selectedImage, file, activity)
    }

}