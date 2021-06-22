package es.uniovi.eii.stitchingbot.ui.logos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.adapter.LogoListAdapter
import es.uniovi.eii.stitchingbot.adapter.SewingMachinesListAdapter
import es.uniovi.eii.stitchingbot.database.LogoDatabaseConnection
import es.uniovi.eii.stitchingbot.database.SewingMachinedatabaseConnection
import es.uniovi.eii.stitchingbot.model.Logo
import es.uniovi.eii.stitchingbot.model.SewingMachine
import es.uniovi.eii.stitchingbot.translator.TAG
import kotlinx.android.synthetic.main.fragment_logos_list.*
import kotlinx.android.synthetic.main.fragment_sewing_machines.*

class LogosListFragment : Fragment() {

    lateinit var logosList: List<Logo>
    lateinit var databaseConnection: LogoDatabaseConnection

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        logosListHardcoded = listOf(
//            Logo(1, "Logo 1"), Logo(2, "Logo 2"), Logo(3, "Logo 3")
//        )

        return inflater.inflate(R.layout.fragment_logos_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseConnection = LogoDatabaseConnection(this.requireContext())

        //TODO crear lista de maquinas de coser
        logosList = getSavedSewingMachines()


        rvLogoList.layoutManager = GridLayoutManager(context, 2)
        rvLogoList.adapter = LogoListAdapter(logosList) {logo -> createListener(logo)}


    }

    private fun createListener(logo: Logo) {
       Log.i(TAG, "Logo: ${logo.title}")
//        databaseConnection.open()
//        databaseConnection.delete(logo)
//        databaseConnection.close()
    }


    private fun getSavedSewingMachines(): ArrayList<Logo>{
        databaseConnection.open()
        val list = databaseConnection.getAllData()
        databaseConnection.close()

        return list
    }

}