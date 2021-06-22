package es.uniovi.eii.stitchingbot.ui.logos

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.bluetooth.TAG
import es.uniovi.eii.stitchingbot.canvas.MyCanvasView
import es.uniovi.eii.stitchingbot.canvas.tools.FreeDrawingTool
import es.uniovi.eii.stitchingbot.canvas.tools.SquareTool
import es.uniovi.eii.stitchingbot.database.LogoDatabaseConnection
import es.uniovi.eii.stitchingbot.model.Logo
import kotlinx.android.synthetic.main.fragment_create_logo.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class CreateLogoFragment : Fragment() {

    lateinit var canvas: MyCanvasView

    private lateinit var logo: Logo
    private lateinit var currentPhotoPath: String
    private lateinit var currentPhotoUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_create_logo, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.canvas = canvasView


//        btnCircleDrawing.setOnClickListener {
//            canvas.mode = CanvasView.Mode.DRAW
//            canvas.drawer = CanvasView.Drawer.CIRCLE
//        }
        btnSquareDrawing.setOnClickListener {
            canvas.tool = SquareTool()
        }
        btnFreeDrawing.setOnClickListener {
            canvas.tool = FreeDrawingTool()
        }

//        btnLineDrawing.setOnClickListener {
//            canvas.mode = CanvasView.Mode.DRAW
//            canvas.drawer = CanvasView.Drawer.LINE
//        }
//
//        btnTextOption.setOnClickListener {
//            canvas.mode = CanvasView.Mode.TEXT
//            canvas.drawer = CanvasView.Drawer.QUADRATIC_BEZIER
//        }
//        btnEraseOption.setOnClickListener {
//            canvas.mode = CanvasView.Mode.ERASER
//        }



        btnDone.setOnClickListener { saveLogo() }

    }

    private fun saveLogo() {

        val bitmap = canvas.getBitmapToSave()
        val file = createImageFile()

        copyImage(bitmap, file)

        currentPhotoUri = Uri.fromFile(file)

        logo = Logo(title = "Try", imgUrl = currentPhotoUri.toString())

        val databaseConnection = LogoDatabaseConnection(requireContext())
        databaseConnection.open()
        databaseConnection.insert(logo)
        databaseConnection.close()
        Toast.makeText(requireContext(), "Logo creado", Toast.LENGTH_LONG).show()
        Log.i(
            TAG,
            "Insertado logo: ${logo.title} - ${logo.category} - ${logo.imgUrl}"
        )

        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.popBackStack()

    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "LOGO_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun copyImage(bitmap: Bitmap?, createdImageFile: File) {
        val destination = FileOutputStream(createdImageFile)
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, destination)
            destination.flush()
        }

        destination.close()
    }

}