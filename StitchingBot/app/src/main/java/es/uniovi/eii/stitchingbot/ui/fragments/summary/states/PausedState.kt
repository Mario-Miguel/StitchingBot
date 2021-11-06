package es.uniovi.eii.stitchingbot.ui.fragments.summary.states

import android.view.View
import es.uniovi.eii.stitchingbot.ui.fragments.summary.SummaryFragment
import kotlinx.android.synthetic.main.fragment_summary.*

class PausedState : State() {

    override fun showInterface(summaryFragment: SummaryFragment) {
        //Mostrar los botones de reanudar y parar
        summaryFragment.btnPauseExecution.visibility = View.GONE
        summaryFragment.btnStopExecution.visibility = View.VISIBLE
        summaryFragment.btnResumeExecution.visibility = View.VISIBLE
        summaryFragment.btnStartTranslate.visibility = View.GONE
        summaryFragment.btnStartExecution.visibility = View.GONE
    }
}