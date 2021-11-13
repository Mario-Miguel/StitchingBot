package es.uniovi.eii.stitchingbot.arduinoCommunication

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.uniovi.eii.stitchingbot.ui.fragments.summary.states.StateManager
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

    /**
     * Inicia el proceso de traducción del logotipo en órdenes a enviar al robot.
     */
    fun run() {
        isInExecution = true
        _actualProgress.postValue(0)
        translation = processData(image)
        isInExecution = false
        translationDone = true
        _actualProgress.postValue(0)
        StateManager.changeToInitial()
    }

    /**
     * Procesa la imagen del logotipo para crear una lista de coordenadas que enviar al robot.
     *
     * @param image imagen del logotipo
     * @return MutableList<Pair<Int, Int>>, lista con las coordenadas que enviar al robot.
     */
    private fun processData(
        image: Bitmap,
    ): MutableList<Pair<Int, Int>> {
        val scaledBitmap = Bitmap.createScaledBitmap(image, WIDTH, HEIGHT, false)

        //Matriz que va a tener los pesos de los distintos nodos
        weightMatrix = Array(scaledBitmap.height) {
            IntArray(scaledBitmap.width)
        }

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

    /**
     * Método que ordena una lista de coordenadas de tal forma que se puedan ejecutar poor el robot.
     *
     * @param coords, lista con las coordenadas desordenadas
     * @return MutableList<Pair<Int, Int>>, lista con las coordenadas ya ordenadas
     */
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

    /**
     * Comprueba si dos puntos se pueden utilizar como coordenada válida
     *
     * @param x valor x de la coordenada
     * @param y valor y de la coordenada
     */
    private fun checkPoint(x: Int, y: Int): Boolean {
        return y + 1 < weightMatrix.size
                && y - 1 >= 0
                && x + 1 < weightMatrix[0].size
                && x - 1 >= 0
                && weightMatrix[y][x] == POINT
    }

    /**
     * Método que añade una coordenada a la lista de coordenadas ya ordenadas.
     *
     * También actualiza el valor de la matriz de pesos en la coordenada en cuestion para que sea
     * Int.MAX_VALUE
     *
     * @param x valor x de la coordenada
     * @param y valor y de la coordenada
     * @param orderedCoordenates, lista de coordenadas ya ordenadas
     */
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

    /**
     * Método que encuentra la coordenada más cercana a otra.
     *
     * @param initialCoord coordenada de la cual se parte para encontrar la más cercana
     * @param coords, lista de coordenadas totales
     * @return Pair<Int, Int>, coordenada más cercana a la que se provee como parámetro
     */
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
