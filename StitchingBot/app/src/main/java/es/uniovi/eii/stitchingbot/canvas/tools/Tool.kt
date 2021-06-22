package es.uniovi.eii.stitchingbot.canvas.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

interface Tool {
    fun touchStart(currentX: Float, currentY: Float, paint: Paint, path: Path)
    fun touchMove(motionTouchEventX:Float, motionTouchEventY: Float, path: Path, extraCanvas: Canvas )
    fun touchUp(path: Path, extraCanvas: Canvas)
}