package es.uniovi.eii.stitchingbot.ui.canvas.tools

import android.graphics.*

class EraseTool: Tool() {

    //TODO Terminar de hacer esto
    override var paint = Paint().apply {
        color = Color.WHITE
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = 22f // default: Hairline-width (really thin)
    }

    override fun touchStart(
        beginCoordinate:PointF,
        paint: Paint,
        path: Path
    ) {
        this.beginCoordinate.x = beginCoordinate.x
        this.beginCoordinate.y = beginCoordinate.y

        path.reset()
        path.moveTo(beginCoordinate.x, beginCoordinate.y)
    }

    override fun touchMove(endCoordinate:PointF, path: Path, canvas: Canvas) {
        path.quadTo(
            beginCoordinate.x,
            beginCoordinate.y,
            (endCoordinate.x + beginCoordinate.x) / 2,
            (endCoordinate.y + beginCoordinate.y) / 2
        )
        beginCoordinate.x = endCoordinate.x
        beginCoordinate.y = endCoordinate.y

        // Draw the path in the extra bitmap to save it.
        canvas.drawPath(path, paint)
    }

    override fun touchUp(endCoordinate:PointF, path: Path, canvas:Canvas) {
        path.reset()
    }
}