package es.uniovi.eii.stitchingbot.translator

import android.graphics.Color
import android.util.Log
import java.util.concurrent.Callable
import kotlin.experimental.and

class WorkerV2(
    val data: ByteArray,
    private val fromIndex: Int,
    private val toIndex: Int,
    val matrixHeight: Int,
    private val matrixWidth: Int
):Callable<MutableList<Pair<Int,Int>>> {

    var result = mutableListOf<Int>()
    private val coordsArray = mutableListOf<Pair<Int, Int>>()

    override fun call(): MutableList<Pair<Int, Int>> {
        var actualRow = 0
        for (i in fromIndex..toIndex step 4) {
            val a = data[i]
            val r = data[i + 1]
            val g = data[i + 2]
            val b = data[i + 3]


            val color = Color.argb(
                (a and 0xFF.toByte()).toInt(),
                (r and 0xFF.toByte()).toInt(),
                (g and 0xFF.toByte()).toInt(),
                (b and 0xFF.toByte()).toInt()
            )

            if (color == Color.BLACK) {
                result.add(1)
                coordsArray.add(Pair((i - matrixWidth * actualRow) / 4, actualRow))
                Log.i(TAG, "ATOPE UN NEGRU fromIndex: $fromIndex")
            } else {
                result.add(0)
            }

            if (i != 0 && matrixWidth % i != 0 ) {
                actualRow++
            }

        }

        return coordsArray
    }

}