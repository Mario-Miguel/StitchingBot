package es.uniovi.eii.stitchingbot.translator

import android.graphics.Bitmap
import android.util.Log

const val TAG : String = "TranslateOrders"

class Main (val image: Bitmap){

    fun run(){
        //Primero hacerlo con un solo color
        //Despues ya si eso meter más colores => hacerlo por capas

        //TODO Separar la imagen en lineas de X número de pixeles.
        Log.i(TAG, "${image.width} - ${image.height}")

        //TODO Recorrer las líneas buscando colores distintos del blanco.

        //TODO Apuntar la posición de esos colores.

        //TODO traducir la posicion de los colores en coordenadas del robot.

    }

}