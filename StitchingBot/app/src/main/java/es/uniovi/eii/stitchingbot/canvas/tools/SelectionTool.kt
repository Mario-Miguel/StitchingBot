package es.uniovi.eii.stitchingbot.canvas.tools

import android.graphics.Point
import es.uniovi.eii.stitchingbot.canvas.editor.Editor
import es.uniovi.eii.stitchingbot.canvas.editor.Figure

class SelectionTool(editor: Editor) : Tool {

    private var posicion: Point? = null
    override fun pinchar(x: Int, y: Int) {
        seleccionada = editor.getDibujo()?.getFigure(x, y)
        posicion = Point(x, y)
    }

    override fun mover(x: Int, y: Int) {
        mueveIncremento(x, y)
    }

    override fun soltar(x: Int, y: Int) {
        mueveIncremento(x, y)
    }

    private fun mueveIncremento(x: Int, y: Int) {
        if (seleccionada != null) {
            seleccionada!!.move(x - (posicion?.x ?: 0), y - (posicion?.y ?: 0))
            posicion = Point(x, y)
        }
    }

    private val editor = editor
    private var seleccionada: Figure? = null

}