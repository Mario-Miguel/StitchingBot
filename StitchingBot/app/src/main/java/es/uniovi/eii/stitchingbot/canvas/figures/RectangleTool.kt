package es.uniovi.eii.stitchingbot.canvas.figures

import android.graphics.Point
import es.uniovi.eii.stitchingbot.canvas.editor.Editor
import es.uniovi.eii.stitchingbot.canvas.editor.Figure
import es.uniovi.eii.stitchingbot.canvas.tools.CreationTool

class RectangleTool(editor: Editor) : CreationTool(editor) {
    override fun doCreaFigura(inicio: Point, fin: Point): Figure {
        return Rectangle(inicio, fin)
    }
}