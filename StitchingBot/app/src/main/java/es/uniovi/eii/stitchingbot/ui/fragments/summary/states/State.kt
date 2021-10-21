package es.uniovi.eii.stitchingbot.ui.fragments.summary.states

import es.uniovi.eii.stitchingbot.ui.fragments.summary.SummaryFragment

abstract class State {

    abstract fun showInterface(summaryFragment: SummaryFragment)

}