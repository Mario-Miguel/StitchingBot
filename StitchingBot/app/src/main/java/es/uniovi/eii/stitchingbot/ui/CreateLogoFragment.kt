package es.uniovi.eii.stitchingbot.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.canvas.CanvasView
import kotlinx.android.synthetic.main.fragment_create_logo.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateLogoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateLogoFragment : Fragment() {

    lateinit var canvas: CanvasView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_logo, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.canvas = canvasView


        btnCircleDrawing.setOnClickListener{
            canvas.mode = CanvasView.Mode.DRAW
            canvas.drawer= CanvasView.Drawer.CIRCLE
        }
        btnSquareDrawing.setOnClickListener{
            canvas.mode = CanvasView.Mode.DRAW
            canvas.drawer= CanvasView.Drawer.RECTANGLE
        }
        btnFreeDrawing.setOnClickListener{
            canvas.mode = CanvasView.Mode.DRAW
            canvas.drawer= CanvasView.Drawer.PEN
        }
        btnLineDrawing.setOnClickListener{
            canvas.mode = CanvasView.Mode.DRAW
            canvas.drawer= CanvasView.Drawer.LINE
        }

        btnTextOption.setOnClickListener {
            canvas.mode = CanvasView.Mode.TEXT
            canvas.drawer = CanvasView.Drawer.QUADRATIC_BEZIER
        }
        btnEraseOption.setOnClickListener {
            canvas.mode = CanvasView.Mode.ERASER
        }
    }

}