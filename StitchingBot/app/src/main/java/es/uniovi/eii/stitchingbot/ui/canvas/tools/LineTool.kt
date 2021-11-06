package es.uniovi.eii.stitchingbot.ui.canvas.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF

class LineTool : Tool() {

    override fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawLine(
            beginCoordinate.x,
            beginCoordinate.y,
            endCoordinate.x,
            endCoordinate.y,
            paint
        )
    }

    override fun touchUp(endCoordinate: PointF, path: Path, canvas: Canvas) {
        canvas.drawLine(
            beginCoordinate.x,
            beginCoordinate.y,
            endCoordinate.x,
            endCoordinate.y,
            paint
        )
    }
}