package es.uniovi.eii.stitchingbot.canvas.tools

import android.graphics.Point
import es.uniovi.eii.stitchingbot.canvas.editor.Editor
import es.uniovi.eii.stitchingbot.canvas.editor.Figure

abstract class CreationTool(editor: Editor) : Tool {

    override fun pinchar(x: Int, y: Int) {
        inicio = Point(x, y)
    }

    override fun mover(x: Int, y: Int) {}
    override fun soltar(x: Int, y: Int) {
        fin = Point(x, y)
        val figura: Figure = doCreaFigura(inicio, fin)
        editor.getDibujo()?.addFigure(figura)
        editor.finHerramienta()
    }

    protected abstract fun doCreaFigura(inicio: Point, fin: Point): Figure
    protected var editor: Editor = editor
    private lateinit var inicio: Point
    private lateinit var fin: Point

}