package es.uniovi.eii.stitchingbot.ui.sewingMachines

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.database.SewingMachinedatabaseConnection
import es.uniovi.eii.stitchingbot.model.SewingMachine
import kotlinx.android.synthetic.main.fragment_sewing_machine_details.*

private const val CREATION_MODE = "creation"

/**
 * A simple [Fragment] subclass.
 * Use the [SewingMachineDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SewingMachineDetailsFragment : Fragment() {
    private var isCreation: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isCreation = it.getBoolean(CREATION_MODE)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_sewing_machine_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createSpinner()

        btnSewingmachineAction.setOnClickListener { buttonAction() }

//        if(arguments?.getBoolean(CREATION_MODE) == true)
//            txtMachineDetails.text = "Creation"
//        else
//            txtMachineDetails.text = "No Creation"
    }

    private fun createSpinner(){
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinnerSewingMachineDetails,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerSewingMachineDetails.adapter = adapter
        }
    }

    private fun buttonAction(){
        if(arguments?.getBoolean(CREATION_MODE) == true) {
            val machineToAdd = SewingMachine(txtSewingMachineName.editText!!.text.toString(), "", spinnerSewingMachineDetails.selectedItemPosition>0)
            val databaseConnection = SewingMachinedatabaseConnection(requireContext())
            databaseConnection.open()
            databaseConnection.insert(machineToAdd)
            databaseConnection.close()

        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param isCreation Parameter 1.
         * @return A new instance of fragment SewingMachineDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(isCreation: Boolean) =
            SewingMachineDetailsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(CREATION_MODE, isCreation)
                }
            }
    }
}