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
import es.uniovi.eii.stitchingbot.ui.util.ShowDialog
import es.uniovi.eii.stitchingbot.util.Constants.CREATION_MODE
import es.uniovi.eii.stitchingbot.util.Constants.LOGO
import kotlinx.android.synthetic.main.fragment_logo_editor.*

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
            if (logoController.getLogo().id >= 0)
                showLogoImage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (!isCreation) {
            menu.clear()
            inflater.inflate(R.menu.delete_button, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            onDeleteLogoMenuClick()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Carga la barra de herramientas que ofrece el editor
     */
    private fun loadButtonToolbar() {
        getButtonsList().forEach { button -> buttonToolbar.addView(button) }
    }

    /**
     * Devuelve la lista de herramientas que ofrece el editor
     *
     * @return lista con los botones de las herramientas
     */
    private fun getButtonsList(): Array<ToolButton> {
        return arrayOf(
            FreeDrawingToolButton(requireContext(), canvasView),
            CircleToolButton(requireContext(), canvasView),
            RectangleToolButton(requireContext(), canvasView),
            LineToolButton(requireContext(), canvasView),
            EraseToolButton(requireContext(), canvasView)
        )
    }

    /**
     * Inicializa los listeners de los botones de la vista
     */
    private fun initializeButtons() {
        btnDone.setOnClickListener { onDoneButtonClick() }
        btnSew.setOnClickListener { onSewButtonClick() }
    }

    /**
     *  Muestra en el Canvas la imagen del logotipo seleccionado
     */
    private fun showLogoImage() {
        val image = logoController.getImage(activity = requireActivity())
        canvasView.setImage(image)
    }

    /**
     * Maneja el evento del click en el botón btnDone
     */
    private fun onDoneButtonClick() {
        if (isCreation)
            saveLogo()
        else
            modifyLogo()
    }

    /**
     * Se comunica con el controller para modificar el logotipo editado
     */
    private fun modifyLogo() {
        val bitmap = canvasView.getBitmapToSave()
        logoController.copyImage(bitmap)
        logoController.updateLogo(requireContext())
        Toast.makeText(requireContext(), "Logo modificado", Toast.LENGTH_LONG).show()
    }

    /**
     * Se comunica con el controller para guardar el logotipo
     */
    private fun saveLogo() {
        val bitmap = canvasView.getBitmapToSave()
        logoController.saveImage(bitmap, requireActivity())
        logoController.addLogo(requireContext())
        logoController.setLogo(logoController.getLastLogoAdded(requireContext()))
        Toast.makeText(requireContext(), "Logo creado", Toast.LENGTH_LONG).show()
    }

    /**
     *  Maneja el evento del click en la opción de menú [R.id.action_delete]
     */
    private fun onDeleteLogoMenuClick() {
        ShowDialog.showDialogOK(requireContext(), "¿Está seguro de eliminar el logotipo?")
        { _, _ ->
            logoController.deleteLogo(requireContext())
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.popBackStack()
        }
    }

    /**
     * Maneja el evento del click en el botón btnSew
     */
    private fun onSewButtonClick() {
        val bundle = bundleOf("logo" to logoController.getLogo())
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_summary, bundle)
    }

}