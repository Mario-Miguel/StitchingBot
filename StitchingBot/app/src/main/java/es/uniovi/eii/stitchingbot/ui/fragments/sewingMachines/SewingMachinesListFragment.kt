package es.uniovi.eii.stitchingbot.ui.fragments.sewingMachines

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.controller.SewingMachineController
import es.uniovi.eii.stitchingbot.model.SewingMachine
import es.uniovi.eii.stitchingbot.ui.adapter.SewingMachinesListAdapter
import kotlinx.android.synthetic.main.fragment_sewing_machines.*


class SewingMachinesListFragment : Fragment() {


    private var comesFromSummary: Boolean = false
    private var sewingMachineController = SewingMachineController()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sewing_machines, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments!= null){
            comesFromSummary = requireArguments().getBoolean("summary")
        }

        rvMachinesList.layoutManager = GridLayoutManager(context, 2)
        rvMachinesList.adapter = SewingMachinesListAdapter(sewingMachineController.getAllSewingMachines(requireContext())) {machine -> navigateToCreation(machine)}
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.add_button, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            navigateToCreation(SewingMachine(id=-1))
        }
        return super.onOptionsItemSelected(item)
    }


    private fun navigateToCreation(machine: SewingMachine) {
        if(comesFromSummary && machine.id>=0){
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.previousBackStackEntry?.savedStateHandle?.set("machine", machine)
            navController.popBackStack()
        }
        else {
            val isCreationMode = (machine.id < 0)
            val bundle = bundleOf("creation" to isCreationMode, "machine" to machine)
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.nav_sewing_machine_details, bundle)
        }

    }



}