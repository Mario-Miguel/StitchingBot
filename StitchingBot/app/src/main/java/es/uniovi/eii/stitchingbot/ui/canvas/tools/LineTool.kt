package es.uniovi.eii.stitchingbot.ui.canvas.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

class LineTool: Tool {
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


    }


    override fun touchUp(path: Path, extraCanvas: Canvas) {
        drawLine(extraCanvas, paint)
    }



    private fun drawLine(canvas: Canvas, paint: Paint) {
        canvas.drawLine(currentX, currentY, motionTouchEventX, motionTouchEventY, paint)
    }
}