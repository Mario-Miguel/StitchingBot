package es.uniovi.eii.stitchingbot.ui.canvas.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

interface Tool {
    fun touchStart(currentX: Float, currentY: Float, paint: Paint, path: Path)
    fun touchMove(motionTouchEventX:Float, motionTouchEventY: Float, path: Path, canvas: Canvas )
    fun touchUp(path: Path, canvas: Canvas)
}