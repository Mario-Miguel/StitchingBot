package es.uniovi.eii.stitchingbot.ui.canvas.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF

class SquareTool : Tool() {

    override fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawRect(
            beginCoordinate.x,
            beginCoordinate.y,
            endCoordinate.x,
            endCoordinate.y,
            paint
        )
    }

    override fun touchUp(endCoordinate: PointF, path: Path, canvas: Canvas) {
        canvas.drawRect(
            beginCoordinate.x,
            beginCoordinate.y,
            endCoordinate.x,
            endCoordinate.y,
            paint
        )
    }

}