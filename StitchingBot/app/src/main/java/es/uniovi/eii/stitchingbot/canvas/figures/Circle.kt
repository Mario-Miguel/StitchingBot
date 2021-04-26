package es.uniovi.eii.stitchingbot.canvas.figures

import android.graphics.Point
import es.uniovi.eii.stitchingbot.canvas.editor.Figure
import kotlin.math.pow
import kotlin.math.sqrt

class Circle(centro: Point, radio: Int) : Figure {

    override fun draw() {
        println("C�rculo: centro = $centro, radio = $radio")
    }

    override fun move(dx: Int, dy: Int) {
        //centro.translate(dx, dy)
    }

    override fun contain(x: Int, y: Int): Boolean {
        val distancia = sqrt((x - centro.x).toDouble().pow(2.0) + (y - centro.y).toDouble().pow(2.0))
        return distancia < radio
    }

    private val centro: Point = centro
    private val radio: Int = radio

}