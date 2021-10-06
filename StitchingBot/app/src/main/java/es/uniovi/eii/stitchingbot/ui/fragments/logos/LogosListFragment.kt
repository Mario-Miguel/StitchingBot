package es.uniovi.eii.stitchingbot.ui.fragments.logos

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.controller.LogoController
import es.uniovi.eii.stitchingbot.model.Logo
import es.uniovi.eii.stitchingbot.ui.adapter.LogoListAdapter
import es.uniovi.eii.stitchingbot.util.translator.TAG
import kotlinx.android.synthetic.main.fragment_logos_list.*

class LogosListFragment : Fragment() {


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
        val logoController = LogoController()
        val logosList = logoController.getSavedLogos(requireContext())

        rvLogoList.layoutManager = GridLayoutManager(context, 2)
        rvLogoList.adapter =
            LogoListAdapter(logosList) { logo ->
                navigateToCreation(logo)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.add_button, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            navigateToCreation(Logo(id = -1))
        }
        return super.onOptionsItemSelected(item)
    }


    private fun navigateToCreation(logo: Logo) {
        val isCreationMode = (logo.id < 0)
        val bundle = bundleOf("creation" to isCreationMode, "logo" to logo)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_create_logo, bundle)

    }

}