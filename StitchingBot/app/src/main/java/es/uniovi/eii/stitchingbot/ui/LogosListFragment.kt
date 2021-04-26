package es.uniovi.eii.stitchingbot.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.adapter.LogoListAdapter
import es.uniovi.eii.stitchingbot.model.Logo
import kotlinx.android.synthetic.main.fragment_logos_list.*

class LogosListFragment : Fragment() {

    lateinit var logosListHardcoded: List<Logo>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        logosListHardcoded = listOf(
            Logo(1, "Logo 1"), Logo(2, "Logo 2"), Logo(3, "Logo 3")
        )

        return inflater.inflate(R.layout.fragment_logos_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvLogoList.layoutManager = GridLayoutManager(context, 2)
        rvLogoList.adapter = LogoListAdapter(logosListHardcoded) {}
    }

}