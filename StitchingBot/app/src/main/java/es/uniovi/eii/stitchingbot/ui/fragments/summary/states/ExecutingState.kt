package es.uniovi.eii.stitchingbot.ui.fragments.summary.states

import android.view.View
import es.uniovi.eii.stitchingbot.ui.fragments.summary.SummaryFragment
import kotlinx.android.synthetic.main.fragment_summary.*

class ExecutingState : State() {

    override fun showInterface(summaryFragment: SummaryFragment) {
        //Mostrar los botones de pausar y parar
        summaryFragment.btnPauseExecution.visibility = View.VISIBLE
        summaryFragment.btnStopExecution.visibility = View.VISIBLE
        summaryFragment.btnResumeExecution.visibility = View.GONE
        summaryFragment.btnStartTranslate.visibility = View.GONE
        summaryFragment.btnStartExecution.visibility = View.GONE
        summaryFragment.pbExecution.visibility = View.VISIBLE
    }

    override fun toString(): String {
        return "ExecutingState"
    }
}