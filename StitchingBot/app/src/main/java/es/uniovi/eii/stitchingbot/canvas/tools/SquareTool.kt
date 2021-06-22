package es.uniovi.eii.stitchingbot.canvas.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import es.uniovi.eii.stitchingbot.translator.TAG

class SquareTool() : Tool {

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
        extraCanvas: Canvas
    ) {
        Log.i(TAG, "Cuadrado")

        this.motionTouchEventX=motionTouchEventX
        this.motionTouchEventY=motionTouchEventY

//        extraCanvas.drawRect(
//            currentX, currentY, motionTouchEventX ,
//            motionTouchEventY, paint
//        )

    }


    override fun touchUp(path: Path, extraCanvas: Canvas) {
        extraCanvas.drawPath(path, paint)
        drawRectangle(extraCanvas, paint)
        path.reset()
    }



    private fun drawRectangle(canvas: Canvas, paint: Paint) {
        val right: Float = if (currentX > motionTouchEventX) currentX else motionTouchEventX
        val left: Float = if (currentX > motionTouchEventX) motionTouchEventX else currentX
        val bottom: Float = if (currentY > motionTouchEventY) currentY else motionTouchEventY
        val top: Float = if (currentY > motionTouchEventY) motionTouchEventY else currentY
        canvas.drawRect(left, top, right, bottom, paint)
    }

}