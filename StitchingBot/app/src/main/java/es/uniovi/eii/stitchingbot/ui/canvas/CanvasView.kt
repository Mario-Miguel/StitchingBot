package es.uniovi.eii.stitchingbot.ui.canvas

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.ui.canvas.tools.FreeDrawingTool
import es.uniovi.eii.stitchingbot.ui.canvas.tools.Tool

class CanvasView : View {

    private var isDrawing: Boolean = false
    var tool: Tool = FreeDrawingTool()

    private var path = Path()

    // Stroke width for the the paint.
    private val stroke = 12f
    private val drawColor = ResourcesCompat.getColor(resources, R.color.black, null)
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.white, null)
    private lateinit var canvas: Canvas
    private var logoImage: Bitmap? = null
    private lateinit var actualDrawing: Bitmap
    private lateinit var frame: Rect

    private val paint = Paint().apply {
        color = drawColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = stroke
    }

    private val framePaint = Paint().apply {
        color = Color.DKGRAY
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = stroke
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup()
    }

    constructor(context: Context) : super(context) {
        setup()
    }

    /**
     * Inicialización del Canvas
     *
     */
    private fun setup() {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        if (::actualDrawing.isInitialized) actualDrawing.recycle()
        actualDrawing = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(actualDrawing)
        canvas.drawColor(backgroundColor)

        // Rectangulo alrededor del Canvas
        val inset = 5
        frame = Rect(inset, inset, width - inset, height - inset)

        if (logoImage != null) {
            canvas.drawBitmap(logoImage!!, inset.toFloat(), inset.toFloat(), null)
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawBitmap(actualDrawing, 0f, 0f, null)
        canvas.drawRect(frame, framePaint)
        if (isDrawing) {
            tool.draw(canvas, paint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val length =
            if (widthMeasureSpec < heightMeasureSpec) widthMeasureSpec else heightMeasureSpec
        super.onMeasure(length, length)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart(event.x, event.y)
            MotionEvent.ACTION_MOVE -> touchMove(event.x, event.y)
            MotionEvent.ACTION_UP -> touchUp(event.x, event.y)
        }
        return true
    }

    /**
     * Función que se ejecuta cuando el usuario pulsa el Canvas
     *
     * @param x coordenada x del canvas donde ha pulsado el usuario
     * @param y coordenada y del canvas donde ha pulsado el usuario
     */
    private fun touchStart(x: Float, y: Float) {
        isDrawing = true
        tool.touchStart(PointF(x, y), paint, path)
        invalidate()
    }

    /**
     * Función que se ejecuta cuando el usuario se mueve mientras mantiene pulsado el Canvas
     *
     * @param x coordenada x del canvas donde ha pulsado el usuario
     * @param y coordenada y del canvas donde ha pulsado el usuario
     */
    private fun touchMove(x: Float, y: Float) {
        tool.touchMove(PointF(x, y), path, canvas)
        invalidate()
    }

    /**
     * Función que se ejecuta cuando el usuario deja de pulsar el Canvas
     *
     * @param x coordenada x del canvas donde ha pulsado el usuario
     * @param y coordenada y del canvas donde ha pulsado el usuario
     */
    private fun touchUp(x: Float, y: Float) {
        // Reset the path so it doesn't get drawn again.
        isDrawing = false
        tool.touchUp(PointF(x, y), path, canvas)
        invalidate()
    }

    /**
     * Devuelve el bitmap que será guardado como logotipo
     *
     * Como el Canvas tiene un marco alrededor que no se desea que se vea en el logotipo,
     * esta función lo recorta, devolviendo tan solo el bitmap con el logotipo creado
     *
     * @return Bitmap con el logotipo final
     */
    fun getBitmapToSave(): Bitmap {
        val cutBitmap = Bitmap.createBitmap(
            frame.width(),
            frame.height(),
            Bitmap.Config.ARGB_8888
        )

        val auxCanvas = Canvas(cutBitmap)
        val srcRect = Rect(frame.left + 5, frame.top + 5, frame.right - 5, frame.bottom - 5)
        val destRect = Rect(0, 0, 1000, 1000)

        auxCanvas.drawBitmap(actualDrawing, srcRect, destRect, null)
        return cutBitmap
    }

    /**
     * Cambia la imagen del canvas por la que se le pasa como parámetro
     *
     * @param bitmap Bitmap con una imagen
     */
    fun setImage(bitmap: Bitmap) {
        logoImage = bitmap
    }
}