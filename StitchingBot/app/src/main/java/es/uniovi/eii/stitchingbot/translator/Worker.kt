package es.uniovi.eii.stitchingbot.translator

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import kotlin.experimental.and


class Worker(
    val data: ByteArray,
    private val fromIndex: Int,
    private val toIndex: Int,
    val matrixHeight: Int,
    private val matrixWidth: Int
) : Runnable {

    var result = mutableListOf<Int>()
    val coordsArray = mutableListOf<Pair<Int, Int>>()

    @RequiresApi(Build.VERSION_CODES.O)
    fun compute() {
        for (i in fromIndex..toIndex step 4) {
            val A = data[i].toFloat()
            val R = data[i + 1].toFloat()
            val G = data[i + 2].toFloat()
            val B = data[i + 3].toFloat()

            val color = Color.argb(A, R, G, B)

            if (color == Color.BLACK) {
                result.add(1)
            } else {
                result.add(0)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun run() {
        var actualRow = fromIndex
        for (i in fromIndex..toIndex step 4) {
            val r = data[i]
            val g = data[i + 1]
            val b = data[i + 2]
            val a = data[i + 3]


            val color = Color.argb(
                (a and 0xFF.toByte()).toInt(),
                (r and 0xFF.toByte()).toInt(),
                (g and 0xFF.toByte()).toInt(),
                (b and 0xFF.toByte()).toInt()
            )

            val color2 = Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())

            if (color == Color.BLACK) {
                result.add(1)
                coordsArray.add(Pair((i - matrixWidth * actualRow) / 4, actualRow))
            } else {
                result.add(0)
            }

            if (i.toDouble() / (matrixWidth*(actualRow+1)).toDouble() == 1.0) {
                actualRow++
            }

        }

        var oleOle: Int = 0


    }

}