package es.uniovi.eii.stitchingbot.canvas.figures

import android.graphics.Point
import es.uniovi.eii.stitchingbot.canvas.editor.Figure

class Rectangle(esquina: Point, ancho: Int, alto: Int) : Figure {

    constructor(inicio: Point, fin : Point) : this(inicio, fin.x-inicio.x, fin.y-inicio.y)


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