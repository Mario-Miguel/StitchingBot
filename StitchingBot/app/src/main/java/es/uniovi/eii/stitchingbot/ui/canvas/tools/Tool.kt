package es.uniovi.eii.stitchingbot.ui.canvas.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF

abstract class Tool {

    protected var beginCoordinate = PointF()
    protected var endCoordinate = PointF()
    open lateinit var paint: Paint

    /**
     * Función que se ejecuta cuando se toca por primera vez el Canvas
     *
     * Inicializa las coordenadas de inicio y fin y el objeto Paint que se va a utilizar
     *
     * @param beginCoordinate coordenada inicial en la que se ha dejado de pulsar
     * @param paint Paint que se va a utilizar para dibujar la figura
     * @param path Path usado en el Canvas
     */
    open fun touchStart(beginCoordinate: PointF, paint: Paint, path: Path) {
        this.beginCoordinate.x = beginCoordinate.x
        this.beginCoordinate.y = beginCoordinate.y
        endCoordinate.x = beginCoordinate.x
        endCoordinate.y = beginCoordinate.y
        this.paint = paint
    }

    /**
     * Función que se ejecuta mientras que se ha pulsado el Canvas y se está moviendo por él
     *
     * Actualiza la coordenada de finalización del dibujo de la figura actual
     *
     * @param endCoordinate coordenada final en la que se ha dejado de pulsar
     * @param path Path usado en el Canvas
     * @param canvas Canvas sobre el que se está dibujando
     */
    open fun touchMove(endCoordinate: PointF, path: Path, canvas: Canvas) {
        this.endCoordinate.x = endCoordinate.x
        this.endCoordinate.y = endCoordinate.y
    }

    /**
     * Función que se ejecuta cuando se deja de pulsar el Canvas
     *
     * @param endCoordinate coordenada final en la que se ha dejado de pulsar
     * @param path Path usado en el Canvas
     * @param canvas Canvas sobre el que se está dibujando
     */
    abstract fun touchUp(endCoordinate: PointF, path: Path, canvas: Canvas)

    /**
     * Dibuja una figura en un Canvas
     *
     * @param canvas Canvas en el que se va a dibujar la figura
     * @param paint Paint que se va a utilizar para dibujar la figura
     */
    abstract fun draw(canvas: Canvas, paint: Paint)
}