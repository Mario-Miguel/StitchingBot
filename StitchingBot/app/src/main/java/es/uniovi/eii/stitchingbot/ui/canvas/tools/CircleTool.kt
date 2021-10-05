package es.uniovi.eii.stitchingbot.ui.canvas.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

class CircleTool: Tool {

    private var currentX: Float = 0F
    private var currentY: Float = 0F
    lateinit var paint: Paint
    private var motionTouchEventX: Float =0F
    private var motionTouchEventY: Float =0F

    override fun touchStart(currentX: Float, currentY: Float, paint: Paint, path: Path) {
        this.currentX = currentX
        this.currentY = currentY
        this.paint = paint
    }

    override fun touchMove(
        motionTouchEventX: Float,
        motionTouchEventY: Float,
        path: Path,
        extraCanvas: Canvas
    ) {
        this.motionTouchEventX=motionTouchEventX
        this.motionTouchEventY=motionTouchEventY
    }

    override fun touchUp(path: Path, canvas: Canvas) {
        val right: Float = if (currentX > motionTouchEventX) currentX else motionTouchEventX
        val left: Float = if (currentX > motionTouchEventX) motionTouchEventX else currentX
        val bottom: Float = if (currentY > motionTouchEventY) currentY else motionTouchEventY
        val top: Float = if (currentY > motionTouchEventY) motionTouchEventY else currentY
        canvas.drawOval(left, top, right, bottom, paint)
    }

}