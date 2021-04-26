package es.uniovi.eii.stitchingbot.canvas.figures

import android.graphics.Point
import es.uniovi.eii.stitchingbot.canvas.editor.Editor
import es.uniovi.eii.stitchingbot.canvas.editor.Figure
import es.uniovi.eii.stitchingbot.canvas.tools.CreationTool

class CircleTool(editor: Editor) : CreationTool(editor) {

    override fun doCreaFigura(inicio: Point, fin: Point): Figure {
        val centro = Point((inicio.x + fin.x) / 2, (inicio.y + fin.y) / 2)
        val radio: Int = (fin.x - inicio.x).coerceAtLeast(fin.y - inicio.y) / 2
        return Circle(centro, radio)
    }
}