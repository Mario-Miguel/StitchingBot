package es.uniovi.eii.stitchingbot.ui.canvas.tools

import android.graphics.*

class EraseTool : Tool() {

    override var paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 22f
    }

    override fun touchStart(
        beginCoordinate: PointF,
        paint: Paint,
        path: Path
    ) {
        this.beginCoordinate.x = beginCoordinate.x
        this.beginCoordinate.y = beginCoordinate.y

        path.reset()
        path.moveTo(beginCoordinate.x, beginCoordinate.y)
    }

    override fun touchMove(endCoordinate: PointF, path: Path, canvas: Canvas) {
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

    override fun touchUp(endCoordinate: PointF, path: Path, canvas: Canvas) {
        path.reset()
    }

    override fun draw(canvas: Canvas, paint: Paint) {

    }
}