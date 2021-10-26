package es.uniovi.eii.stitchingbot.ui.canvas.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF

abstract class Tool {

    protected var beginCoordinate = PointF()
    protected var endCoordinate = PointF()
    open lateinit var paint: Paint

    open fun touchStart(beginCoordinate:PointF, paint: Paint, path: Path){
        this.beginCoordinate.x = beginCoordinate.x
        this.beginCoordinate.y = beginCoordinate.y
        endCoordinate.x = beginCoordinate.x
        endCoordinate.y = beginCoordinate.y
        this.paint = paint
    }
    open fun touchMove(endCoordinate:PointF, path: Path, canvas: Canvas ){
        this.endCoordinate.x = endCoordinate.x
        this.endCoordinate.y = endCoordinate.y
    }

    abstract fun touchUp(endCoordinate:PointF, path: Path, canvas: Canvas)
    open fun draw(canvas:Canvas, paint: Paint){

    }
}