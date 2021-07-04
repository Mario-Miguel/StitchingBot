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

package es.uniovi.eii.stitchingbot.canvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.canvas.tools.FreeDrawingTool
import es.uniovi.eii.stitchingbot.canvas.tools.Tool
import kotlin.math.abs


// Stroke width for the the paint.
private const val STROKE_WIDTH = 12f

/**
 * Custom view that follows touch events to draw on a canvas.
 */
class MyCanvasView : View {

    var tool: Tool = FreeDrawingTool()

    // Holds the path you are currently drawing.
    private var path = Path()

    private val drawColor = ResourcesCompat.getColor(resources, R.color.black, null)
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.white, null)
    private lateinit var extraCanvas: Canvas
    private var logoImage: Bitmap? = null
    private lateinit var extraBitmap: Bitmap
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

    /**
     * Don't draw every single pixel.
     * If the finger has has moved less than this distance, don't draw. scaledTouchSlop, returns
     * the distance in pixels a touch can wander before we think the user is scrolling.
     */
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private var currentX = 0f
    private var currentY = 0f

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    /**
     * Called whenever the view changes size.
     * Since the view starts out with no size, this is also called after
     * the view has been inflated and has a valid size.
     */
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)

        // Calculate a rectangular frame around the picture.
        val inset = 50
        frame = Rect(inset, inset, width - inset, width - inset)

        if (logoImage != null)
            extraCanvas.drawBitmap(logoImage!!, inset.toFloat(), inset.toFloat(), null)
    }

    override fun onDraw(canvas: Canvas) {
        //canvas.drawColor(backgroundColor)
        canvas.drawARGB(0,0,0,0)
        // Draw the bitmap that has the saved path.
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        // Draw a frame around the canvas.
        val auxPaint = Paint().apply {
            color = Color.DKGRAY
            isAntiAlias = true
            // Dithering affects how colors with higher-precision than the device are down-sampled.
            isDither = true
            style = Paint.Style.STROKE // default: FILL
            strokeJoin = Paint.Join.ROUND // default: MITER
            strokeCap = Paint.Cap.ROUND // default: BUTT
            strokeWidth = STROKE_WIDTH
        }
        extraCanvas.drawRect(frame, auxPaint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val length =
            if (widthMeasureSpec < heightMeasureSpec) widthMeasureSpec else heightMeasureSpec

        super.onMeasure(length, length)
    }

    /**
     * No need to call and implement MyCanvasView#performClick, because MyCanvasView custom view
     * does not handle click actions.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
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
//    private fun touchStart() {
//        path.reset()
//        path.moveTo(motionTouchEventX, motionTouchEventY)
//        currentX = motionTouchEventX
//        currentY = motionTouchEventY
//    }
    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
        path.reset()
        path.moveTo(currentX, currentY)

        tool.touchStart(currentX, currentY, paint, path)
        //invalidate()
    }


    private fun touchMove() {
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            tool.touchMove(motionTouchEventX, motionTouchEventY, path, extraCanvas)
            currentX = motionTouchEventX
            currentY = motionTouchEventY
        }
        invalidate()
    }


    private fun touchUp() {
        // Reset the path so it doesn't get drawn again.
        tool.touchUp(path, extraCanvas)
        invalidate()
    }


    fun getBitmapToSave(): Bitmap {
        val cutBitmap = Bitmap.createBitmap(
            frame.width(),
            frame.height(),
            Bitmap.Config.ARGB_8888
        )

        val auxCanvas = Canvas(cutBitmap)
        val srcRect = Rect(frame.left + 20, frame.top + 20, frame.right - 20, frame.bottom - 20)
        val destRect = Rect(0, 0, 1000, 1000)

        auxCanvas.drawBitmap(extraBitmap, srcRect, destRect, null)

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