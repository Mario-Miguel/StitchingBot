package es.uniovi.eii.stitchingbot.canvas.editor

import es.uniovi.eii.stitchingbot.canvas.tools.SelectionTool
import es.uniovi.eii.stitchingbot.canvas.tools.Tool

class Editor(dibujo: Drawing?) {
    fun setDibujo(dibujo: Drawing?) {
        this.dibujo = dibujo
    }

    fun getDibujo(): Drawing? {
        return dibujo
    }

    fun dibujar() {
        println("Herramienta: $actual")
        dibujo?.draw()
    }

    // Herramientas -------------------
    protected fun createDefaultTool(): Tool {
        return SelectionTool(this)
    }

    val defaultTool: Tool
        get() = principal

    fun setHerramienta(herramienta: Tool?) {
        if (herramienta != null) actual = herramienta
    }

    fun pinchar(x: Int, y: Int) {
        actual.pinchar(x, y)
    }

    fun mover(x: Int, y: Int) {
        actual.mover(x, y)
    }

    fun soltar(x: Int, y: Int) {
        actual.soltar(x, y)
    }

    fun finHerramienta() {
        actual = principal
    }

    private var dibujo: Drawing? = null
    private var actual: Tool
    private val principal: Tool

    init {
        setDibujo(dibujo)
        principal = createDefaultTool()
        actual = principal
    }
}