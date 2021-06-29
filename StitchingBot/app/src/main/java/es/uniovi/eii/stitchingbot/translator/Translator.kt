package es.uniovi.eii.stitchingbot.translator

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import es.uniovi.eii.stitchingbot.R

const val TAG : String = "TranslateOrders"

class Translator (private val image: Bitmap){

    fun run(): Boolean{
        //Primero hacerlo con un solo color
        //Despues ya si eso meter más colores => hacerlo por capas

        //TODO Separar la imagen en lineas de X número de pixeles.
        Log.i(TAG, "${image.width} - ${image.height}")
        //La relacion es de 10 pixeles <-> 1mm


        val scaledBitmap = Bitmap.createScaledBitmap(image, 1000,1000,false)
        Log.i(TAG, "Scaled: ${scaledBitmap.height}-${scaledBitmap.width}")


        //TODO Recorrer las líneas buscando colores distintos del blanco.

        //TODO Apuntar la posición de esos colores.

        //TODO traducir la posicion de los colores en coordenadas del robot.
        val coords = mutableListOf<Pair<Int,Int>>()

        for (y in 0 until image.height){
            for(x in 0 until image.width){
                val pixel = image.getPixel(x, y)

                if(pixel == Color.BLACK){
                    Log.i(TAG, "Pixel en x=$x, y=$y")
                    coords.add(Pair(x,y))
                }

            }
        }

        Log.i(TAG, "End of processing")
        return true
    }


    private fun getBitmapPixels(bitmap: Bitmap, x: Int, y: Int, width: Int, height: Int): IntArray {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, x, y,
            width, height);
        val subsetPixels = IntArray(width * height)
        for ( row in 0..height) {
            System.arraycopy(pixels, (row * bitmap.width),
                subsetPixels, row * width, width);
        }
        return subsetPixels;
    }
}
