package es.uniovi.eii.stitchingbot.util

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.uniovi.eii.stitchingbot.util.Constants.TAG_TRANSLATE
import kotlin.math.hypot

object Translator {
    private const val POINT: Int = 1
    private const val HEIGHT: Int = 375
    private const val WIDTH: Int = 375
    private const val FACTOR_AJUSTE: Int = 6

    var image: Bitmap = Bitmap.createBitmap(HEIGHT, WIDTH, Bitmap.Config.ARGB_8888)
    var translation: MutableList<Pair<Int, Int>> = mutableListOf()
    var translationDone: Boolean = false
    var isInExecution = false
    private lateinit var weightMatrix: Array<IntArray>

    private val _actualProgress = MutableLiveData<Int>()
    val actualProgress: LiveData<Int>
        get() = _actualProgress

    fun run() {
        isInExecution = true
        _actualProgress.postValue(0)

        Log.i(TAG_TRANSLATE, "${image.width} - ${image.height}")
        //La relacion es de 50 pixeles <-> 1mm


        val scaledBitmap = Bitmap.createScaledBitmap(image, WIDTH, HEIGHT, false)
        Log.i(TAG_TRANSLATE, "Scaled: ${scaledBitmap.height}-${scaledBitmap.width}")

        //Matriz que va a tener los pesos de los distintos nodos
        weightMatrix = Array(scaledBitmap.height) {
            IntArray(scaledBitmap.width)
        }

        translation = processData(scaledBitmap)
        isInExecution = false
        translationDone = true
        _actualProgress.postValue(0)
        Log.i(TAG_TRANSLATE, "End of processing")
    }


    private fun processData(
        scaledBitmap: Bitmap,
    ): MutableList<Pair<Int, Int>> {
        val coords = mutableListOf<Pair<Int, Int>>()

        //Se añaden los puntos
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

            counter++
            _actualProgress.postValue(((counter.toDouble() / coords.size.toDouble()) * 100).toInt())
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
