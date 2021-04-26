package es.uniovi.eii.stitchingbot.canvas.figures

import android.graphics.Point
import es.uniovi.eii.stitchingbot.canvas.editor.Figure

class Line(esquina: Point, ancho: Int, alto: Int) : Figure {

    //TODO Adecuar la clase pa que sea una línea
    override fun draw() {
        println("Cuadrado: x = " + esquina.x.toString() + ", y = " + esquina.y.toString() + ", ancho = " + ancho.toString() + ", alto = " + alto)
    }

    override fun move(dx: Int, dy: Int) {
        //esquina.translate(dx, dy)
    }

    override fun contain(x: Int, y: Int): Boolean {
        return esquina.x <= x && x <= esquina.x + ancho && esquina.y <= y && y <= esquina.y + alto
    }

    private val esquina: Point = Point(esquina)
    private val ancho: Int = ancho
    private val alto: Int = alto

}