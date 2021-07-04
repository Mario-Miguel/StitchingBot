package es.uniovi.eii.stitchingbot.translator

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import androidx.annotation.RequiresApi
import java.nio.ByteBuffer
import kotlin.experimental.and
import kotlin.math.pow
import kotlin.math.sqrt

const val TAG: String = "TranslateOrders"
const val POINT: Int = 1
const val HEIGHT: Int = 750
const val WIDTH: Int = 750

class Translator(private val image: Bitmap) {

    private lateinit var pointsMatrix: Array<IntArray>

    fun run(): MutableList<Pair<Int, Int>> {

        Log.i(TAG, "${image.width} - ${image.height}")
        //La relacion es de 50 pixeles <-> 1mm


        val scaledBitmap = Bitmap.createScaledBitmap(image, WIDTH, HEIGHT, false)
        Log.i(TAG, "Scaled: ${scaledBitmap.height}-${scaledBitmap.width}")


        var coords = mutableListOf<Pair<Int, Int>>()

        //Coordenada X, coordenada Y, ¿tiene que levantar el pedal para la siguiente?
        val coordsV2 = mutableListOf<Triple<Int, Int, Boolean>>()

        pointsMatrix = Array(scaledBitmap.height) {
            IntArray(scaledBitmap.width)
        }




            for (y in 0 until scaledBitmap.height) {
                for (x in 0 until scaledBitmap.width) {
                    val pixel = scaledBitmap.getPixel(x, y)

                    if (pixel == Color.BLACK) {
                        coords.add(Pair(x, y))
                        coordsV2.add(Triple(x, y, false))
                        pointsMatrix[y][x] = 1
                    }

                }
            }


        var orderedArray = order(coords)
        var orderedArrayV2 = completeArray(coordsV2)
        val orderedReducedArray = orderReduce(coords)

        orderedArray = orderedArray.map{ Pair(it.first*5, it.second*5) }.toMutableList()


        Log.i(TAG, "End of processing")

        return orderedArray
    }

    private fun completeArray(coordsV2: MutableList<Triple<Int, Int, Boolean>>): MutableList<Triple<Int, Int, Boolean>> {

        return mutableListOf()
    }


//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun processImage(scaledBitmap: Bitmap): MutableList<Pair<Int, Int>> {
//        val coords = mutableListOf<Pair<Int, Int>>()
//
//        //FORMA 1
//        val size = scaledBitmap.byteCount
//        val byteBuffer = ByteBuffer.allocate(size)
//        scaledBitmap.copyPixelsToBuffer(byteBuffer)
//        val byteArray = byteBuffer.array()
//
//        val master = Master(4, byteArray, HEIGHT, WIDTH)
//        master.compute()
//
//        //master.compute2()
//
//
//        coords.addAll(master.coordsArray)
//
//        return coords
//
//    }


    private fun order(
        coords: MutableList<Pair<Int, Int>>
    ): MutableList<Pair<Int, Int>> {
        val orderedArray = mutableListOf<Pair<Int, Int>>()

        orderedArray.add(Pair(coords[0].first, coords[0].second))

        var counter = 0
        var xForward = true
        var change = false
        var yUp = false
        var triedToChange = false

        while (orderedArray.size != coords.size) {
            val xCoord = orderedArray[counter].first
            val yCoord = orderedArray[counter].second

            val rightStatus = if(xCoord+1<pointsMatrix[0].size)pointsMatrix[yCoord][xCoord + 1] else 0
            val leftStatus = if(xCoord-1>=0) pointsMatrix[yCoord][xCoord - 1] else 0
            val upStatus = if(yCoord-1>=0) pointsMatrix[yCoord - 1][xCoord] else 0
            val downStatus = if(yCoord+1<pointsMatrix.size) pointsMatrix[yCoord + 1][xCoord] else 0

            //var currentStatus = pointsMatrix[yCoord][xCoord]


            if (!change) {
                //Si va pa la derecha -> x++
                if (xForward) {
                    if (xCoord + 1 < pointsMatrix[0].size && rightStatus == POINT) {
                        orderedArray.add(Pair(xCoord + 1, yCoord))
                        counter++
                        triedToChange=false


                    } else {
                        //Cambiar la direccion en la que debe ir y cambiar a subir o bajar
                        xForward = !xForward
                        change = true
                    }
                }
                //Si va pa la izquierda -> x--
                else {
                    if (xCoord - 1 >= 0 && leftStatus == POINT) {
                        orderedArray.add(Pair(xCoord - 1, yCoord))
                        counter++
                        triedToChange=false

                    } else {
                        //Cambiar la direccion en la que debe ir y cambiar a subir o bajar
                        xForward = !xForward
                        change = true
                    }
                }
            } else {
                //Si va pa arriba -> y--
                if (yUp) {
                    //Si puede seguir subiendo
                    if (yCoord - 1 >= 0 && upStatus == POINT && !triedToChange) {
                        //Si encuentra un punto en el pixel superior
                        orderedArray.add(Pair(xCoord, yCoord - 1))
                        counter++
                        triedToChange=true

                    } else {
                        //Si no lo encuentra en la parte superior
                        yUp = !yUp
                        if (xCoord + 1 < pointsMatrix.size && downStatus == POINT && !triedToChange) {
                            //Si encuentra un punto en el pixel superior

                            orderedArray.add(Pair(xCoord, yCoord + 1))
                            counter++
                            triedToChange=true

                        } else {
                            //No encuentra ni arriba, ni abajo, ni a los lados => Saltar a otra coordenada
                            orderedArray.add(
                                searchForNearestCoord(
                                    xCoord,
                                    yCoord,
                                    coords,
                                    orderedArray
                                )
                            )
                            counter++
                            triedToChange=false
                        }
                    }
                }
                //Si va pa abajo -> y++
                else {
                    //Si puede seguir bajando
                    if (xCoord + 1 < pointsMatrix.size && downStatus == POINT&& !triedToChange) {
                        //Si encuentra un punto en el pixel de abajo
                        orderedArray.add(Pair(xCoord, yCoord + 1))
                        counter++
                        triedToChange=true

                    } else {
                        //Si no lo encuentra en la parte inferior
                        yUp = !yUp
                        if (yCoord - 1 >= 0 && upStatus == POINT&& !triedToChange) {
                            //Si encuentra un punto en el pixel superior

                            orderedArray.add(Pair(xCoord, yCoord - 1))
                            counter++
                            triedToChange=true

                        } else {
                            //No encuentra ni arriba, ni abajo, ni a los lados => Saltar a otra coordenada
                            orderedArray.add(
                                searchForNearestCoord(
                                    xCoord,
                                    yCoord,
                                    coords,
                                    orderedArray
                                )
                            )
                            counter++
                            triedToChange=false
                        }
                    }
                }
                change = false
            }


        }

        return orderedArray

    }

    private fun searchForNearestCoord(
        xCoord: Int,
        yCoord: Int,
        coords: MutableList<Pair<Int, Int>>,
        orderedArray: MutableList<Pair<Int, Int>>
    ): Pair<Int, Int> {
        Log.i(TAG, "ME CAGO EN MI PUTA VIDA")
        val minimumDistance = Double.MAX_VALUE
        var actualDistance: Double
        var nearestPair = Pair(0, 0)

        for (coordinate in coords) {
            if (!orderedArray.contains(coordinate)) {
                actualDistance = sqrt(
                    ((coordinate.second - yCoord).toDouble()).pow(2.0) + ((coordinate.first - xCoord).toDouble()).pow(
                        2.0
                    )
                )

                if (actualDistance < minimumDistance) {
                    nearestPair = coordinate
                }
            }
        }

        return nearestPair
    }

    private fun orderReduce(coords: MutableList<Pair<Int, Int>>): MutableList<Pair<Int, Int>> {
        val orderedArray = mutableListOf<Pair<Int, Int>>()
        val firstXCoord = coords[0].first
        val firstYCoord = coords[0].second
        var i = 0
        var j = 0
        while (i in firstYCoord..pointsMatrix.size) {
            while (j in firstXCoord..pointsMatrix[0].size) {
                if (pointsMatrix[i][j] == POINT) {
                    orderedArray.add(Pair(i, j))
                    j += 50
                }
                j++
            }
            i += 50
        }

        return orderedArray
    }

//x=190, y=656


}
