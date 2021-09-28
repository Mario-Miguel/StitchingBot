package es.uniovi.eii.stitchingbot.translator

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import kotlin.math.*

const val TAG: String = "TranslateOrders"
const val POINT: Int = 1
const val HEIGHT: Int = 375
const val WIDTH: Int = 375
const val FACTOR_AJUSTE: Int = 6

//1875

class Translator(private val image: Bitmap) {

    lateinit var weightMatrix: Array<IntArray>
    lateinit var hasPassedMatrix: Array<IntArray>

    fun run(): MutableList<Pair<Int, Int>> {

        Log.i(TAG, "${image.width} - ${image.height}")
        //La relacion es de 50 pixeles <-> 1mm


        val scaledBitmap = Bitmap.createScaledBitmap(image, WIDTH, HEIGHT, false)
        Log.i(TAG, "Scaled: ${scaledBitmap.height}-${scaledBitmap.width}")

        var coords: MutableList<Pair<Int, Int>> = mutableListOf()

        //Matriz que va a tener los pesos de los distintos nodos
        weightMatrix = Array(scaledBitmap.height) {
            IntArray(scaledBitmap.width)
        }

        //Matriz que va a tener los nodos por los que puedo pasar
        hasPassedMatrix = Array(scaledBitmap.height) {
            IntArray(scaledBitmap.width)
        }


        //Se añaden los puntos en las respectivas matrices
        for (y in 0 until scaledBitmap.height) {
            for (x in 0 until scaledBitmap.width) {
                val pixel = scaledBitmap.getPixel(x, y)

                weightMatrix[y][x] = Int.MAX_VALUE

                if (pixel == Color.BLACK) {
                    //coords.add(Triple(x, y, false))
                    coords.add(Pair(x, y))

                    //weightMatrix[y][x] = hypot((x-coords[0].first).toDouble(), (y-coords[0].second).toDouble()).toInt()
                    weightMatrix[y][x] = POINT
                    hasPassedMatrix[y][x] = POINT

                }

            }
        }

        //Ahora tengo en la matriz de los pesos todos las distancias desde el punto inicial a los puntos restantes y en los que no hay punto, el valor máximo de enteros

        coords = createCoordArray(coords).map {
            Pair(
                (it.first * FACTOR_AJUSTE)+16,
                (it.second * FACTOR_AJUSTE)+16
            )
        }.filterIndexed { i, _ -> i % 2 == 0 }.toMutableList()


        Log.i(TAG, "End of processing")

        return coords
    }


    private fun createCoordArray(coords:MutableList<Pair<Int, Int>>): MutableList<Pair<Int, Int>> {
        var orderedCoordenates = mutableListOf<Pair<Int, Int>>()
        var actualCoord = coords[0]
        orderedCoordenates.add(actualCoord)
        var horizontal = true
        var vertical = false

        var counter = 0

        while (counter<coords.size) {
            if(horizontal){
                if(weightMatrix[actualCoord.second][actualCoord.first+1]==POINT){
                    orderedCoordenates.add(Pair(actualCoord.first+1, actualCoord.second))
                    weightMatrix[actualCoord.second][actualCoord.first+1]=Int.MAX_VALUE
                    actualCoord = Pair(actualCoord.first+1, actualCoord.second)
                }
                else if(weightMatrix[actualCoord.second][actualCoord.first-1]==POINT){
                    orderedCoordenates.add(Pair(actualCoord.first-1, actualCoord.second))
                    weightMatrix[actualCoord.second][actualCoord.first-1]=Int.MAX_VALUE
                    actualCoord = Pair(actualCoord.first-1, actualCoord.second)
                }
                else{
                    horizontal = false
                    vertical=true
                }
            }

            if (vertical){
                if(weightMatrix[actualCoord.second+1][actualCoord.first]==POINT){
                    weightMatrix[actualCoord.second][actualCoord.first+1]=Int.MAX_VALUE
                    actualCoord = Pair(actualCoord.first, actualCoord.second+1)
                    orderedCoordenates.add(Pair(actualCoord.first, actualCoord.second))
                }
                else if(weightMatrix[actualCoord.second-1][actualCoord.first]==POINT){
                    weightMatrix[actualCoord.second-1][actualCoord.first]=Int.MAX_VALUE
                    actualCoord = Pair(actualCoord.first, actualCoord.second-1)
                    orderedCoordenates.add(Pair(actualCoord.first, actualCoord.second))
                }
                else{
                    actualCoord = findNearestCoord(actualCoord, coords)
                    weightMatrix[actualCoord.second][actualCoord.first]=Int.MAX_VALUE
                    orderedCoordenates.add(Pair(actualCoord.first, actualCoord.second))
                    counter--
                }
                horizontal = true
                vertical=false
            }
            Log.i(TAG, "Añadido $actualCoord")
            counter++
        }

        return orderedCoordenates
    }

    private fun findNearestCoord(initialCoord: Pair<Int, Int>, coords:MutableList<Pair<Int, Int>>):Pair<Int, Int>{
        var minDistance = Int.MAX_VALUE
        var coordToReturn = initialCoord

        for(i in coords.indices){
            if(weightMatrix[coords[i].second][coords[i].first]==POINT){
                val distance = hypot((initialCoord.first - coords[i].first).toDouble(), (initialCoord.second-coords[i].second).toDouble()).toInt()
                if (distance < minDistance){
                    minDistance=distance
                    coordToReturn=coords[i]
                }
            }
        }

        return coordToReturn
    }

}
