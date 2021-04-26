package es.uniovi.eii.stitchingbot.canvas.tools

interface Tool {
    fun pinchar(x: Int, y: Int)
    fun mover(x: Int, y: Int)
    fun soltar(x: Int, y: Int)
}