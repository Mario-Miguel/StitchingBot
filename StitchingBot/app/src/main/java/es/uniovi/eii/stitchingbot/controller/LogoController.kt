package es.uniovi.eii.stitchingbot.controller

import android.content.Context
import es.uniovi.eii.stitchingbot.database.LogoDatabaseConnection
import es.uniovi.eii.stitchingbot.model.Logo

class LogoController {

    private var logo = Logo()

    fun setLogo(title: String = "", url: String = ""){
        val auxLogo = Logo(title = title, imgUrl = url)
        this.logo = auxLogo
    }

    fun setLogo(logo: Logo){
        this.logo=logo
    }

    fun getLogo():Logo{
        return this.logo
    }

    fun getSavedLogos(context: Context): ArrayList<Logo> {
        val databaseConnection = LogoDatabaseConnection(context)
        databaseConnection.open()
        val list = databaseConnection.getAllData()
        databaseConnection.close()

        return list
    }

    fun addLogo(context:Context){
        val databaseConnection = LogoDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.insert(logo)
        databaseConnection.close()
    }

    fun updateLogo(context: Context){
        val databaseConnection = LogoDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.update(logo)
        databaseConnection.close()
    }

    fun deleteLogo(context: Context){
        val databaseConnection = LogoDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.delete(logo)
        databaseConnection.close()
    }
}