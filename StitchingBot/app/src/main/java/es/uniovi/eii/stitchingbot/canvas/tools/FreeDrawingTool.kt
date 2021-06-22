package es.uniovi.eii.stitchingbot.canvas.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import es.uniovi.eii.stitchingbot.canvas.MyCanvasView

class FreeDrawingTool() : Tool {
    var currentX: Float = 0F
    var currentY: Float = 0F
    lateinit var paint: Paint

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

    override fun touchMove(motionTouchEventX: Float, motionTouchEventY: Float, path: Path, extraCanvas: Canvas) {

        path.quadTo(
            currentX,
            currentY,
            (motionTouchEventX + currentX) / 2,
            (motionTouchEventY + currentY) / 2
        )
        currentX = motionTouchEventX
        currentY = motionTouchEventY

        // Draw the path in the extra bitmap to save it.
        extraCanvas.drawPath(path, paint)

    }


    override fun touchUp(path: Path, extraCanvas:Canvas) {
        path.reset()
    }


}