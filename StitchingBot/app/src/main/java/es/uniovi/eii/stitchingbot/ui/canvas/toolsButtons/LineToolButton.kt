package es.uniovi.eii.stitchingbot.ui.canvas.toolsButtons

import android.content.Context
import android.util.AttributeSet
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.ui.canvas.CanvasView
import es.uniovi.eii.stitchingbot.ui.canvas.tools.LineTool

class LineToolButton : ToolButton {

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

    override fun setup(canvas: CanvasView) {
        setImageResource(R.drawable.ic_method_draw_image)
        setOnClickListener {
            canvas.tool = LineTool()
        }
        super.setup(canvas)
        contentDescription = "Botón de la herramienta Línea"
    }
}