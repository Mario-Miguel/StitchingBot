package es.uniovi.eii.stitchingbot.ui.canvas.toolsButtons

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.res.ResourcesCompat
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.ui.canvas.CanvasView

abstract class ToolButton: AppCompatImageButton {

    open fun setup(canvas: CanvasView){
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        setBackgroundResource(outValue.resourceId)

        val width = resources.getDimension(R.dimen.editor_buttons_width).toInt()
        val height = resources.getDimension(R.dimen.editor_buttons_width).toInt()
        layoutParams = LinearLayout.LayoutParams(width, height, 1F)
    }

    /**
     * Copy Constructor
     * @param context
     * @param attrs
     * @param defStyle
     */
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    /**
     * Copy Constructor
     * @param context
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * Copy Constructor
     * @param context
     */
    constructor(context: Context) : super(context)
}