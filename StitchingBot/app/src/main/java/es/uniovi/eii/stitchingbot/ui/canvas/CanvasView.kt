/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.uniovi.eii.stitchingbot.ui.canvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.ui.canvas.tools.FreeDrawingTool
import es.uniovi.eii.stitchingbot.ui.canvas.tools.Tool


// Stroke width for the the paint.
private const val STROKE_WIDTH = 12f

/**
 * Custom view that follows touch events to draw on a canvas.
 */
class CanvasView : View {

    //####################################################
    var isDrawing: Boolean = false
    var tool: Tool = FreeDrawingTool()

    // Holds the path you are currently drawing.
    private var path = Path()

    private val drawColor = ResourcesCompat.getColor(resources, R.color.black, null)
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.white, null)
    private lateinit var canvas: Canvas
    private var logoImage: Bitmap? = null
    private lateinit var actualDrawing: Bitmap
    private lateinit var frame: Rect

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    private val framePaint = Paint().apply {
        color = Color.DKGRAY
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH
    }


    /**
     * Called whenever the view changes size.
     * Since the view starts out with no size, this is also called after
     * the view has been inflated and has a valid size.
     */
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        if (::actualDrawing.isInitialized) actualDrawing.recycle()
        actualDrawing = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(actualDrawing)
        canvas.drawColor(backgroundColor)

        // Calculate a rectangular frame around the picture.
        val inset = 5
        frame = Rect(inset, inset, width - inset, height - inset)

        if (logoImage != null) {
            canvas.drawBitmap(logoImage!!, inset.toFloat(), inset.toFloat(), null)
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawARGB(0, 0, 0, 0)
        // Draw the bitmap that has the saved path.
        canvas.drawBitmap(actualDrawing, 0f, 0f, null)

        // Draw a frame around the canvas.
        canvas.drawRect(frame, framePaint)

        if (isDrawing) {
            tool.draw(canvas, paint)
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val length = if (widthMeasureSpec < heightMeasureSpec)
            widthMeasureSpec
        else
            heightMeasureSpec

        super.onMeasure(length, length)
    }

    /**
     * No need to call and implement MyCanvasView#performClick, because MyCanvasView custom view
     * does not handle click actions.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart(event.x, event.y)
            MotionEvent.ACTION_MOVE -> touchMove(event.x, event.y)
            MotionEvent.ACTION_UP -> touchUp(event.x, event.y)
        }
        return true
    }

    /**
     * The following methods factor out what happens for different touch events,
     * as determined by the onTouchEvent() when statement.
     * This keeps the when conditional block
     * concise and makes it easier to change what happens for each event.
     * No need to call invalidate because we are not drawing anything.
     */
    private fun touchStart(x:Float, y:Float) {
        isDrawing = true
        tool.touchStart(PointF(x, y), paint, path)
        invalidate()
    }

    private fun touchMove(x:Float, y:Float) {
        tool.touchMove(PointF(x, y), path, canvas)
        invalidate()
    }

    private fun touchUp(x:Float, y:Float) {
        // Reset the path so it doesn't get drawn again.
        isDrawing = false
        tool.touchUp(PointF(x, y), path, canvas)
        invalidate()
    }


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

    fun setImage(bitmap: Bitmap) {
        logoImage = bitmap
    }

    /**
     * Copy Constructor
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        setup()
    }

    /**
     * Copy Constructor
     *
     * @param context
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup()
    }

    /**
     * Copy Constructor
     *
     * @param context
     */
    constructor(context: Context) : super(context) {
        setup()
    }

    /**
     * Common initialization.
     *
     */
    private fun setup() {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }
}