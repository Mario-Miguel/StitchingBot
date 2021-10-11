package es.uniovi.eii.stitchingbot.util.translator

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

object Translator {

    var image: Bitmap = Bitmap.createBitmap(HEIGHT, WIDTH, Bitmap.Config.ARGB_8888)
    var translation: MutableList<Pair<Int, Int>> = mutableListOf()
    var translationDone: Boolean = false
    lateinit var weightMatrix: Array<IntArray>

    fun run() {

        Log.i(TAG, "${image.width} - ${image.height}")
        //La relacion es de 50 pixeles <-> 1mm


        val scaledBitmap = Bitmap.createScaledBitmap(image, WIDTH, HEIGHT, false)
        Log.i(TAG, "Scaled: ${scaledBitmap.height}-${scaledBitmap.width}")

        var coords: MutableList<Pair<Int, Int>> = mutableListOf()

        //Matriz que va a tener los pesos de los distintos nodos
        weightMatrix = Array(scaledBitmap.height) {
            IntArray(scaledBitmap.width)
        }

        coords = processData(scaledBitmap, coords)
        Log.i(TAG, "End of processing")

        translation = coords
        translationDone = true
    }


    private fun processData(
        scaledBitmap: Bitmap,
        coords: MutableList<Pair<Int, Int>>
    ): MutableList<Pair<Int, Int>> {
        //Se añaden los puntos en las respectivas matrices
        for (y in 0 until scaledBitmap.height) {
            for (x in 0 until scaledBitmap.width) {
                val pixel = scaledBitmap.getPixel(x, y)

                weightMatrix[y][x] = Int.MAX_VALUE

                if (pixel == Color.BLACK) {
                    coords.add(Pair(x, y))

                    weightMatrix[y][x] = POINT

                }
            }
        }

        val orderedCoords = createCoordArray(coords).map {
            Pair(
                (it.first * FACTOR_AJUSTE) + 16,
                (it.second * FACTOR_AJUSTE) + 16
            )
        }.filterIndexed { i, _ -> i % 2 == 0 }.toMutableList()
        return orderedCoords
    }


    private fun createCoordArray(coords: MutableList<Pair<Int, Int>>): MutableList<Pair<Int, Int>> {
        val orderedCoordenates = mutableListOf<Pair<Int, Int>>()
        var actualCoord = coords[0]
        orderedCoordenates.add(actualCoord)
        var checkCoordInHorizontal = true
        var checkCoordInVertical = false

        var counter = 0

        while (counter < coords.size) {
            if (checkCoordInHorizontal) {
                if (checkPoint(actualCoord.first + 1, actualCoord.second)) {
                    actualCoord = addCoordToOrderedArray(
                        actualCoord.first + 1,
                        actualCoord.second,
                        orderedCoordenates
                    )
                } else if (checkPoint(actualCoord.first - 1, actualCoord.second)) {
                    actualCoord = addCoordToOrderedArray(
                        actualCoord.first - 1,
                        actualCoord.second,
                        orderedCoordenates
                    )
                } else {
                    checkCoordInHorizontal = false
                    checkCoordInVertical = true
                }
            }

            if (checkCoordInVertical) {
                if (checkPoint(actualCoord.first, actualCoord.second + 1)) {
                    actualCoord = addCoordToOrderedArray(
                        actualCoord.first,
                        actualCoord.second + 1,
                        orderedCoordenates
                    )

                } else if (checkPoint(actualCoord.first, actualCoord.second - 1)) {
                    actualCoord = addCoordToOrderedArray(
                        actualCoord.first,
                        actualCoord.second - 1,
                        orderedCoordenates
                    )

                } else {
                    actualCoord = findNearestCoord(actualCoord, coords)
                    actualCoord = addCoordToOrderedArray(
                        actualCoord.first,
                        actualCoord.second,
                        orderedCoordenates
                    )
                }
                checkCoordInHorizontal = true
                checkCoordInVertical = false
            }

            if(counter>2 && actualCoord.first == orderedCoordenates[counter-1].first && actualCoord.second == orderedCoordenates[counter-1].second){
                Log.i(TAG, "Coordenada repetida")
            }
            counter++
        }
        return orderedCoordenates
    }

    private fun checkPoint(x: Int, y: Int): Boolean {
        return y + 1 < weightMatrix.size && y - 1 >= 0 && x + 1 < weightMatrix[0].size && x - 1 >= 0 && weightMatrix[y][x] == POINT
    }

    private fun addCoordToOrderedArray(
        x: Int,
        y: Int,
        orderedCoordenates: MutableList<Pair<Int, Int>>
    ): Pair<Int, Int> {
        weightMatrix[y][x] = Int.MAX_VALUE
        val actualCoord = Pair(x, y)
        orderedCoordenates.add(actualCoord)
        return actualCoord
    }

    private fun findNearestCoord(
        initialCoord: Pair<Int, Int>,
        coords: MutableList<Pair<Int, Int>>
    ): Pair<Int, Int> {
        var minDistance = Int.MAX_VALUE
        var coordToReturn = initialCoord

        for (i in coords.indices) {
            if (weightMatrix[coords[i].second][coords[i].first] == POINT) {
                val distance = hypot(
                    (initialCoord.first - coords[i].first).toDouble(),
                    (initialCoord.second - coords[i].second).toDouble()
                ).toInt()
                if (distance < minDistance) {
                    minDistance = distance
                    coordToReturn = coords[i]
                }
            }
        }
        return coordToReturn
    }


}
