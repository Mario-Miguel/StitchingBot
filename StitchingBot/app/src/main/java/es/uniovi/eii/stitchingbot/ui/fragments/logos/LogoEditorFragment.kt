package es.uniovi.eii.stitchingbot.ui.fragments.logos

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.controller.LogoController
import es.uniovi.eii.stitchingbot.ui.canvas.toolsButtons.*
import es.uniovi.eii.stitchingbot.util.Constants.CREATION_MODE
import es.uniovi.eii.stitchingbot.util.Constants.LOGO
import kotlinx.android.synthetic.main.fragment_logo_editor.*


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class LogoEditorFragment : Fragment() {

    private var isCreation: Boolean = true
    private val logoController = LogoController()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_logo_editor, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeButtons()
        loadButtonToolbar()
        if (arguments != null) {
            isCreation = requireArguments().getBoolean(CREATION_MODE)
            logoController.setLogo(requireArguments().getParcelable(LOGO)!!)
            if(logoController.getLogo().id>=0)
                showLogoImage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if(!isCreation){
            menu.clear()
            inflater.inflate(R.menu.delete_button, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete){
            deleteLogo()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadButtonToolbar(){
        buttonToolbar.addView(FreeDrawingToolButton(requireContext(), canvasView))
        buttonToolbar.addView(CircleToolButton(requireContext(), canvasView))
        buttonToolbar.addView(RectangleToolButton(requireContext(), canvasView))
        buttonToolbar.addView(LineToolButton(requireContext(), canvasView))
        buttonToolbar.addView(EraseToolButton(requireContext(), canvasView))
    }

    private fun initializeButtons(){
        btnDone.setOnClickListener { if(isCreation) saveLogo() else modifyLogo() }
        btnSew.setOnClickListener { loadSummaryScreen() }
    }

    private fun showLogoImage(){
        val image = logoController.getImage(activity=requireActivity())
        canvasView.setImage(image)
    }

    private fun modifyLogo(){
        val bitmap = canvasView.getBitmapToSave()
        logoController.copyImage(bitmap)
        logoController.updateLogo(requireContext())

        Toast.makeText(requireContext(), "Logo modificado", Toast.LENGTH_LONG).show()
    }

    private fun saveLogo() {
        val bitmap = canvasView.getBitmapToSave()
        logoController.saveImage(bitmap, requireActivity())
        logoController.addLogo(requireContext())
        logoController.setLogo(logoController.getLastLogoAdded(requireContext()))

        Toast.makeText(requireContext(), "Logo creado", Toast.LENGTH_LONG).show()
    }

    private fun deleteLogo(){
        logoController.deleteLogo(requireContext())

        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.popBackStack()
    }

    private fun loadSummaryScreen(){
        val bundle = bundleOf("logo" to logoController.getLogo())
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_summary, bundle)
    }
}