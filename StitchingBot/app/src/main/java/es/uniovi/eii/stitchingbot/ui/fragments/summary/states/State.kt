package es.uniovi.eii.stitchingbot.ui.fragments.summary.states

import es.uniovi.eii.stitchingbot.ui.fragments.summary.SummaryFragment

abstract class State {

    /**
     *  Muestra la interfaz del estado
     *
     *  @param summaryFragment Fragmento desde el que se llama al método
     */
    abstract fun showInterface(summaryFragment: SummaryFragment)
}