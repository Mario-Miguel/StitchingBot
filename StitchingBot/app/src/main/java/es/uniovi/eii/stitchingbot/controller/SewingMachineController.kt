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

    /**
     * Método que cambia los parámetros de la máquina de coser guardada
     *
     * @param name nombre de la máquina de coser
     * @param url dirección donde se guarda la imagen de la máquina de coser
     * @param motorSteps pasos del motor
     */
    fun setSewingMachine(
        name: String? = sewingMachine.name,
        url: String? = sewingMachine.imgUrl,
        motorSteps: Int = sewingMachine.motorSteps
    ) {
        val auxSewingMachine = SewingMachine(sewingMachine.id, name, url, motorSteps)
        this.sewingMachine = auxSewingMachine
    }

    /**
     * Método que cambia los parámetros del logotipo guardado
     *
     * @param sewingMachine, máquina de coser que se desea guardar
     */
    fun setSewingMachine(sewingMachine: SewingMachine) {
        this.sewingMachine = sewingMachine
    }

    /**
     * Devuelve la máquina de coser guardada por el controlador
     *
     * @return Máquina de coser guardada
     */
    fun getSewingMachine(): SewingMachine {
        return sewingMachine
    }

    /**
     * Se comunica con la base de datos para añadir una máquina de coser, ya guardado en el controller
     *
     * @param context Contexto desde el que se llama a la función
     */
    fun addSewingMachine(context: Context) {
        val databaseConnection = SewingMachineDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.insert(sewingMachine)
        databaseConnection.close()
    }

    /**
     * Se comunica con la base de datos para actualizar la máquina de coser
     *
     * @param context Contexto desde el que se llama a la función
     */
    fun updateSewingMachine(context: Context) {
        val databaseConnection = SewingMachineDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.update(sewingMachine)
        databaseConnection.close()
    }

    /**
     * Se comunica con la base de datos para eliminar una máquina de coser
     *
     * @param context Contexto desde el que se llama a la función
     */
    fun deleteSewingMachine(context: Context) {
        imageManager.deleteImageFile(getSewingMachine().imgUrl)
        val databaseConnection = SewingMachineDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.delete(sewingMachine)
        databaseConnection.close()
    }

    /**
     * Se comunica con la base de datos para obtener toda la lista de máquinas de coser guardadas
     *
     * @param context Contexto desde el que se llama a la función
     * @return ArrayList<SewingMachine>, lista con las máquinas de coser guardadas
     */
    fun getAllSewingMachines(context: Context): ArrayList<SewingMachine> {
        val databaseConnection = SewingMachineDatabaseConnection(context)
        databaseConnection.open()
        val list = databaseConnection.getAllData()
        databaseConnection.close()

        return list
    }

    /**
     * Comprueba si la máquina de coser ha sido seleccionado o no
     *
     * @return true si hay una máquina de coser seleccionada, false si no se ha seleccionado
     */
    fun isSewingMachineSelected(): Boolean {
        return getSewingMachine().id != -1
    }

    /**
     * Devuelve la imagen, como Bitmap, del logotipo seleccionado.
     *
     * @param activity actividad desde la que se llama al método
     * @return Bitmap con el logotipo
     */
    fun getImage(activity: Activity, imgUrl: String?): Bitmap? {
        return imageManager.getImageFromUri(
            imgUrl,
            activity
        )
    }

    /**
     * Crea un archivo para guardar una imagen y despues ejecuta una acción
     *
     * @param activity actividad desde la que se llama a la función
     * @param action acción que se desea ejecutar tras crear el archivo
     */
    fun createImageFileAndDispatchAction(activity: Activity, action: (Uri, File) -> Unit) {
        imageManager.createImageFile(activity)?.also {
            Log.i(Constants.TAG_SEWINGMACHINE, it.absolutePath)
            val photoURI: Uri = FileProvider.getUriForFile(
                activity.applicationContext,
                "es.uniovi.eii.stitchingbot",
                it
            )
            imageManager.deleteImageIfSaved(getSewingMachine().imgUrl)
            setSewingMachine(url = photoURI.toString())
            action(photoURI, it)
        }
    }

    /**
     * Devuelve la imagen, como Bitmap, del logotipo seleccionado.
     *
     * @param activity actividad desde la que se llama al método
     * @return Bitmap con el logotipo
     */
    fun getImage(activity: Activity): Bitmap {
        return imageManager.getImageFromUri(
            getSewingMachine().imgUrl,
            activity
        )!!
    }

    /**
     * Copia una imagen a un archivo
     *
     * @param selectedImage url de la imagen seleccionada
     * @param file archivo en el que se desea copiar la imagen
     * @param activity Actividad desde la que se llama a la función
     */
    fun copyImageToFile(selectedImage: String, file: File, activity: Activity) {
        imageManager.copyImageFromGallery(selectedImage, file, activity)
    }

}