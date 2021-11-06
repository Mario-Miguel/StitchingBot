package es.uniovi.eii.stitchingbot.ui.canvas.tools

import android.graphics.*

class CircleTool : Tool() {

    override fun touchUp(endCoordinate: PointF, path: Path, canvas: Canvas) {
        canvas.drawOval(
            beginCoordinate.x,
            beginCoordinate.y,
            endCoordinate.x,
            endCoordinate.y,
            paint
        )
    }

    override fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawOval(
            beginCoordinate.x,
            beginCoordinate.y,
            endCoordinate.x,
            endCoordinate.y,
            paint
        )
    }
}