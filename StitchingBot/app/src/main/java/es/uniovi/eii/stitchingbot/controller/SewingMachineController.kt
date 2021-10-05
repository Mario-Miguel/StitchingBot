package es.uniovi.eii.stitchingbot.controller

import android.app.Activity
import android.content.Context
import android.net.Uri
import es.uniovi.eii.stitchingbot.database.SewingMachineDatabaseConnection
import es.uniovi.eii.stitchingbot.model.SewingMachine
import es.uniovi.eii.stitchingbot.util.ImageManager
import java.io.File

class SewingMachineController {

    private var sewingMachine = SewingMachine()
    private var imageManager = ImageManager
    private lateinit var currentPhotoUri: Uri

    fun updateSewingMachineParams(name:String, motorSteps: Int){
        sewingMachine.name=name
        sewingMachine.motorSteps=motorSteps
    }

    fun setSewingMachine(sewingMachine:SewingMachine){
        this.sewingMachine = sewingMachine
        currentPhotoUri = Uri.parse(sewingMachine.imgUrl)
    }

    fun getSewingMachine(): SewingMachine{
        return sewingMachine
    }

    fun setCurrentPhotoUri(uri:Uri){
        currentPhotoUri=uri
        sewingMachine.imgUrl=uri.toString()
    }

    fun getCurrentPhotoUri():Uri{
        return currentPhotoUri
    }

    fun updateSewingMachineUrl(selectedImage: Uri, activity: Activity): Uri{
        val currentPhotoFile: File

        if (sewingMachine.imgUrl.isNullOrBlank()) {
            currentPhotoFile = imageManager.createImageFile(activity)
            currentPhotoUri = Uri.fromFile(currentPhotoFile)
        } else {
            currentPhotoUri = Uri.parse(sewingMachine.imgUrl)
            currentPhotoFile = File(currentPhotoUri.path!!)
        }

        imageManager.copyImage(
            imageManager.getImageFromUri(
                selectedImage,
                activity
            ), currentPhotoFile
        )

        sewingMachine.imgUrl = currentPhotoUri.toString()
        return currentPhotoUri
    }

    fun addSewingMachine(context:Context){
        val databaseConnection = SewingMachineDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.insert(sewingMachine)
        databaseConnection.close()
    }

    fun updateSewingMachine(context:Context){
        val databaseConnection = SewingMachineDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.update(sewingMachine)
        databaseConnection.close()
    }

    fun deleteSewingMachine(context:Context){
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
}