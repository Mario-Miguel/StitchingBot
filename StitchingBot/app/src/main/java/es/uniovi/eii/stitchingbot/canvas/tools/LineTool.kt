package es.uniovi.eii.stitchingbot.canvas.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

class LineTool:Tool {
    private var currentX: Float = 0F
    private var currentY: Float = 0F
    lateinit var paint: Paint
    private var motionTouchEventX: Float =0F
    private var motionTouchEventY: Float =0F

    override fun touchStart(
        currentX: Float,
        currentY: Float,
        paint: Paint,
        path: Path
    ) {
        this.currentX = currentX
        this.currentY = currentY
        this.paint = paint


    }

    override fun touchMove(
        motionTouchEventX: Float,
        motionTouchEventY: Float,
        path: Path,
        canvas: Canvas
    ) {

        this.motionTouchEventX=motionTouchEventX
        this.motionTouchEventY=motionTouchEventY


        path.quadTo(
            currentX,
            currentY,
            (motionTouchEventX + currentX) / 2,
            (motionTouchEventY + currentY) / 2
        )
        currentX = motionTouchEventX
        currentY = motionTouchEventY

        // Draw the path in the extra bitmap to save it.
        canvas.drawPath(path, paint)

    }


    override fun touchUp(path: Path, extraCanvas: Canvas) {
        extraCanvas.drawPath(path, paint)
        drawRectangle(extraCanvas, paint)
        path.reset()
    }



    private fun drawRectangle(canvas: Canvas, paint: Paint) {

        canvas.drawLine(currentX, currentY, motionTouchEventX, motionTouchEventY, paint)
    }
}