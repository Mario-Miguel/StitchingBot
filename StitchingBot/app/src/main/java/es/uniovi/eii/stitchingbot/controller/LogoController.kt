package es.uniovi.eii.stitchingbot.controller

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import es.uniovi.eii.stitchingbot.database.LogoDatabaseConnection
import es.uniovi.eii.stitchingbot.model.Logo

class LogoController {

    private var logo = Logo()
    private val imageManager = ImageManager()

    /**
     * Método que cambia los parámetros del logotipo guardado
     *
     * @param title nombre del logotipo
     * @param url dirección donde se guarda la imagen del logotipo
     */
    private fun setLogo(title: String? = logo.title, url: String? = logo.imgUrl) {
        val auxLogo = Logo(logo.id, title, url, logo.category)
        this.logo = auxLogo
    }

    /**
     * Método que cambia los parámetros del logotipo guardado
     *
     * @param logo, logotipo que se desea guardar
     */
    fun setLogo(logo: Logo) {
        this.logo = logo
    }

    /**
     * Devuelve el logotipo guardado por el controlador
     *
     * @return Logotipo guardado
     */
    fun getLogo(): Logo {
        return this.logo
    }

    /**
     * Se comunica con la base de datos para obtener toda la lista de logotipos guardados en ella
     *
     * @param context Contexto desde el que se llama a la función
     * @return ArrayList<Logo>, lista con los logotipos guardados en la base de datos
     */
    fun getSavedLogos(context: Context): ArrayList<Logo> {
        val databaseConnection = LogoDatabaseConnection(context)
        databaseConnection.open()
        val list = databaseConnection.getAllData()
        databaseConnection.close()

        return list
    }

    /**
     * Se comunica con la base de datos para añadir un logotipo, ya guardado en el controller
     *
     * @param context Contexto desde el que se llama a la función
     */
    fun addLogo(context: Context) {
        val databaseConnection = LogoDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.insert(logo)
        databaseConnection.close()
    }

    /**
     * Se comunica con la base de datos para actualizar el logotipo
     *
     * @param context Contexto desde el que se llama a la función
     */
    fun updateLogo(context: Context) {
        setLogo()
        val databaseConnection = LogoDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.update(logo)
        databaseConnection.close()
    }

    /**
     * Se comunica con la base de datos para eliminar un logotipo
     *
     * @param context Contexto desde el que se llama a la función
     */
    fun deleteLogo(context: Context) {
        imageManager.deleteImageFile(getLogo().imgUrl)
        val databaseConnection = LogoDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.delete(logo)
        databaseConnection.close()
    }

    /**
     * Se comunica con la base de datos para obtener el último logotipo añadido a ella
     *
     * @param context Contexto desde el que se llama a la función
     * @return Logo, último logotipo añadido a la base de datos
     */
    fun getLastLogoAdded(context: Context): Logo {
        val databaseConnection = LogoDatabaseConnection(context)
        databaseConnection.open()
        val auxLogo = databaseConnection.getLastElement()
        databaseConnection.close()

        return auxLogo
    }

    /**
     * Comprueba si el logotipo ha sido seleccionado o no
     *
     * @return true si hay un logotipo  seleccionado, false si no se ha seleccionado
     */
    fun isLogoSelected(): Boolean {
        return getLogo().id != -1
    }

    /**
     * Devuelve la imagen, como Bitmap, del logotipo seleccionado.
     *
     * @param activity actividad desde la que se llama al método
     * @return Bitmap con el logotipo
     */
    fun getImage(activity: Activity): Bitmap {
        return imageManager.getImageFromUri(
            getLogo().imgUrl,
            activity
        )!!
    }

    /**
     * Copia una imagen en la dirección del logotipo
     *
     * @param bitmap Bitmap con el logotipo
     */
    fun copyImage(bitmap: Bitmap) {
        imageManager.copyImage(bitmap, getLogo().imgUrl)
    }

    /**
     * Guarda la imagen del logotipo
     *
     * @param bitmap Bitmap con el logotipo
     * @param activity Actividad desde la que se llama a la función
     */
    fun saveImage(bitmap: Bitmap, activity: Activity) {
        val uri = imageManager.saveImageReturningUri(bitmap, activity)
        setLogo(url = uri.toString())
    }

}