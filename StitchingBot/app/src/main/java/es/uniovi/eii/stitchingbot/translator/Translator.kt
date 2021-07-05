package es.uniovi.eii.stitchingbot.translator

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log

const val TAG: String = "TranslateOrders"
const val POINT: Int = 1
const val HEIGHT: Int = 375
const val WIDTH: Int = 375
const val FACTOR_AJUSTE: Int = 6

//1875

class Translator(private val image: Bitmap) {

    private lateinit var pointsMatrix: Array<IntArray>

    fun run(): MutableList<Triple<Int, Int, Boolean>> {

        Log.i(TAG, "${image.width} - ${image.height}")
        //La relacion es de 50 pixeles <-> 1mm


        val scaledBitmap = Bitmap.createScaledBitmap(image, WIDTH, HEIGHT, false)
        Log.i(TAG, "Scaled: ${scaledBitmap.height}-${scaledBitmap.width}")


        //Coordenada X, coordenada Y, ¿tiene que levantar el pedal para la siguiente?
        val coordsV2 = mutableListOf<Triple<Int, Int, Boolean>>()

        pointsMatrix = Array(scaledBitmap.height) {
            IntArray(scaledBitmap.width)
        }


        for (y in 0 until scaledBitmap.height) {
            for (x in 0 until scaledBitmap.width) {
                val pixel = scaledBitmap.getPixel(x, y)

                if (pixel == Color.BLACK) {
                    //coords.add(Pair(x, y))
                    coordsV2.add(Triple(x, y, false))
                    pointsMatrix[y][x] = POINT
                }

            }
        }


        var orderedArrayV2 = completeArray(coordsV2)
        orderedArrayV2 = orderedArrayV2.map {
            Triple(
                it.first * FACTOR_AJUSTE,
                it.second * FACTOR_AJUSTE,
                it.third
            )
        }.filterIndexed { i, _ -> i % 2 == 0 }.toMutableList()


        Log.i(TAG, "End of processing")

        return orderedArrayV2
    }

    private fun completeArray(coordsV2: MutableList<Triple<Int, Int, Boolean>>): MutableList<Triple<Int, Int, Boolean>> {

        for (i in 1 until coordsV2.size) {
            //Si las coordenadas de x NO son contiguas
            if (coordsV2[i - 1].first != coordsV2[i].first - 1) {
                coordsV2[i] = Triple(coordsV2[i].first, coordsV2[i].second, true)
            }
        }
        return coordsV2

    }

}
