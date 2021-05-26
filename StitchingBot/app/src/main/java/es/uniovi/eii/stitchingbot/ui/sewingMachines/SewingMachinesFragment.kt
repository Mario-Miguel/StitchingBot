package es.uniovi.eii.stitchingbot.ui.sewingMachines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.adapter.SewingMachinesListAdapter
import es.uniovi.eii.stitchingbot.database.SewingMachinedatabaseConnection
import es.uniovi.eii.stitchingbot.model.SewingMachine
import kotlinx.android.synthetic.main.fragment_sewing_machines.*


class SewingMachinesFragment : Fragment() {

    lateinit var machinesList: ArrayList<SewingMachine>
    lateinit var databaseConnection: SewingMachinedatabaseConnection



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sewing_machines, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseConnection = SewingMachinedatabaseConnection(this.requireContext())

        //TODO crear lista de maquinas de coser
        machinesList = getSavedSewingMachines()
        //TODO añadir card para crear maquina
        machinesList.add(SewingMachine(-1,"__ADD", "", false))


        rvMachinesList.layoutManager = GridLayoutManager(context, 2)
        rvMachinesList.adapter = SewingMachinesListAdapter(machinesList) {machine -> createListener(machine)}
    }

    private fun createListener(machine: SewingMachine) {
        val isCreationMode = (machine.id<0)
        val bundle = bundleOf("creation" to isCreationMode, "machine" to machine)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_sewing_machine_details, bundle)

    }


    private fun getSavedSewingMachines(): ArrayList<SewingMachine>{
        databaseConnection.open()
        val list = databaseConnection.getAllData()
        databaseConnection.close()

        return list
    }


}