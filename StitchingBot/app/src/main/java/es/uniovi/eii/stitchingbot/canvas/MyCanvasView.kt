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
        val inset = 60
        frame = Rect(inset + 3, inset + 3, width - inset, height - inset)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(backgroundColor)
        // Draw the bitmap that has the saved path.
        canvas.drawBitmap(extraBitmap, 0f, 0f, paint)
        // Draw a frame around the canvas.
        paint.color = ResourcesCompat.getColor(resources, R.color.editor_frame, null)
        extraCanvas.drawRect(frame, paint)
        paint.color = ResourcesCompat.getColor(resources, R.color.black, null)
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

    //TODO overridear esti metodo pa hacer les herramientes
//    private fun touchMove() {
//        val dx = abs(motionTouchEventX - currentX)
//        val dy = abs(motionTouchEventY - currentY)
//        if (dx >= touchTolerance || dy >= touchTolerance) {
//            // QuadTo() adds a quadratic bezier from the last point,
//            // approaching control point (x1,y1), and ending at (x2,y2).
//            path.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2, (motionTouchEventY + currentY) / 2)
//            currentX = motionTouchEventX
//            currentY = motionTouchEventY
//            // Draw the path in the extra bitmap to save it.
//            extraCanvas.drawPath(path, paint)
//        }
//        // Invalidate() is inside the touchMove() under ACTION_MOVE because there are many other
//        // types of motion events passed into this listener, and we don't want to invalidate the
//        // view for those.
//        invalidate()
//    }

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
        //path.reset()
        tool.touchUp(path, extraCanvas)
        invalidate()
    }

    fun getBitmapToSave(): Bitmap {
        val cutBitmap = Bitmap.createBitmap(
            extraBitmap.width - frame.left,
            extraBitmap.height - frame.top,
            Bitmap.Config.ARGB_8888
        )
        val auxCanvas = Canvas(cutBitmap)
        val srcRect = Rect(frame.left+5, frame.top+5, frame.right-5, frame.bottom-5)
        val destRect = Rect(frame.left, frame.top, frame.right, frame.bottom)

        auxCanvas.drawBitmap(extraBitmap, srcRect, destRect, null)

        return cutBitmap
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