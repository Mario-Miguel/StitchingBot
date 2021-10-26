package es.uniovi.eii.stitchingbot.ui.canvas.toolsButtons

import android.content.Context
import android.util.AttributeSet
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.ui.canvas.CanvasView
import es.uniovi.eii.stitchingbot.ui.canvas.tools.EraseTool

class EraseToolButton : ToolButton {

    override fun setup(canvas: CanvasView) {
        setImageResource(R.drawable.ic_eraser)
        setOnClickListener {
            canvas.tool = EraseTool()
        }

        super.setup(canvas)
    }

    constructor(context: Context, canvas: CanvasView) : super(context) {
        setup(canvas)
    }


    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)
}