package es.uniovi.eii.stitchingbot.ui.fragments.summary.states

import android.view.View
import es.uniovi.eii.stitchingbot.ui.fragments.summary.SummaryFragment
import kotlinx.android.synthetic.main.fragment_summary.*

class TranslatingState: State() {

    override fun showInterface(summaryFragment: SummaryFragment) {

        summaryFragment.requireActivity().runOnUiThread {
            summaryFragment.pbExecution.visibility = View.VISIBLE
            summaryFragment.btnStartTranslate.isEnabled = false
            summaryFragment.btnStartExecution.isEnabled = false
        }
    }

}