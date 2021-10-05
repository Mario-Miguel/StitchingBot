package es.uniovi.eii.stitchingbot.ui.fragments.logos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.ui.adapter.LogoListAdapter
import es.uniovi.eii.stitchingbot.database.LogoDatabaseConnection
import es.uniovi.eii.stitchingbot.model.Logo
import es.uniovi.eii.stitchingbot.util.translator.TAG
import kotlinx.android.synthetic.main.fragment_logos_list.*

class LogosListFragment : Fragment() {

    lateinit var logosList: List<Logo>
    lateinit var databaseConnection: LogoDatabaseConnection

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_logos_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseConnection = LogoDatabaseConnection(this.requireContext())


        logosList = getSavedLogos()

        rvLogoList.layoutManager = GridLayoutManager(context, 2)
        rvLogoList.adapter = LogoListAdapter(logosList) { logo -> navigateToCreation(logo) }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.add_button, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            navigateToCreation(Logo(id=-1))
        }
        return super.onOptionsItemSelected(item)
    }


    private fun navigateToCreation(logo: Logo) {
        Log.i(TAG, "Logo: ${logo.title}")
        //TODO eliminar cuando termine las pruebas de parsear

        val isCreationMode = (logo.id < 0)
        val bundle = bundleOf("creation" to isCreationMode, "logo" to logo)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_create_logo, bundle)

    }


    private fun getSavedLogos(): ArrayList<Logo> {
        databaseConnection.open()
        val list = databaseConnection.getAllData()
        databaseConnection.close()

        return list
    }

    //TODO eliminar cuando termine las pruebas de parsear
    private fun getImageFromUri(imageUri: Uri?): Bitmap? {
        var image: Bitmap
        if (imageUri != null) {
            requireActivity().contentResolver.openFileDescriptor(imageUri, "r")
                .use { pfd ->
                    if (pfd != null) {
                        val matrix = Matrix()
                        matrix.postRotate(180F)
                        image = BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
                        //Rotate bitmap
                        val ei = requireActivity().contentResolver.openInputStream(imageUri)?.let {
                            ExifInterface(
                                it
                            )
                        }
                        val orientation: Int = ei!!.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                        )

                        return when (orientation) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(image, 90F)
                            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(image, 180F)
                            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(image, 270F)
                            ExifInterface.ORIENTATION_NORMAL -> image
                            else -> image
                        }
                    }
                }

        }
        return null
    }


    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

}