package es.uniovi.eii.stitchingbot.controller

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import es.uniovi.eii.stitchingbot.database.LogoDatabaseConnection
import es.uniovi.eii.stitchingbot.model.Logo

class LogoController {

    private var logo = Logo()
    private val imageManager = ImageManager()

    fun setLogo(title: String? = logo.title, url: String? = logo.imgUrl) {
        val auxLogo = Logo(logo.id, title, url, logo.category)
        this.logo = auxLogo
    }

    fun setLogo(logo: Logo) {
        this.logo = logo
    }

    fun getLogo(): Logo {
        return this.logo
    }

    fun getSavedLogos(context: Context): ArrayList<Logo> {
        val databaseConnection = LogoDatabaseConnection(context)
        databaseConnection.open()
        val list = databaseConnection.getAllData()
        databaseConnection.close()

        return list
    }

    fun addLogo(context: Context) {
        val databaseConnection = LogoDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.insert(logo)
        databaseConnection.close()
    }

    fun updateLogo(context: Context) {
        setLogo()
        val databaseConnection = LogoDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.update(logo)
        databaseConnection.close()
    }

    fun deleteLogo(context: Context) {
        imageManager.deleteImageFile(getLogo().imgUrl)
        val databaseConnection = LogoDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.delete(logo)
        databaseConnection.close()
    }

    fun getLastLogoAdded(context: Context): Logo {
        val databaseConnection = LogoDatabaseConnection(context)
        databaseConnection.open()
        val auxLogo = databaseConnection.getLastElement()
        databaseConnection.close()

        return auxLogo
    }

    fun isLogoSelected(): Boolean {
        return getLogo().id != -1
    }

    fun getImage(activity: Activity): Bitmap {
        return imageManager.getImageFromUri(
            getLogo().imgUrl,
            activity
        )!!
    }

    fun copyImage(bitmap: Bitmap) {
        imageManager.copyImage(bitmap, getLogo().imgUrl)
    }

    fun saveImage(bitmap: Bitmap, activity: Activity) {
        val uri = imageManager.saveImageReturningUri(bitmap, activity)
        setLogo(url=uri.toString())
    }

}