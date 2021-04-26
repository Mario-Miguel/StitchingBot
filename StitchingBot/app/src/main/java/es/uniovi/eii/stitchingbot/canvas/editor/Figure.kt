package es.uniovi.eii.stitchingbot.canvas.editor

interface Figure {
    fun draw()
    fun move(dx: Int, dy: Int)
    fun contain(x: Int, y: Int): Boolean
}