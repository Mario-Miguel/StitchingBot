package es.uniovi.eii.stitchingbot.ui.fragments.summary.states

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.uniovi.eii.stitchingbot.ui.fragments.summary.SummaryFragment

object StateManager {

    private var _actualState: MutableLiveData<State> = MutableLiveData(StoppedState())
    val actualState: LiveData<State>
        get() = _actualState

    /**
     * Cambia a un estado concreto
     *
     * @param state Estado al que se quiere cambiar
     */
    fun changeTo(state: State) {
        _actualState.postValue(state)
    }

    /**
     * Muestra la interfaz del estado actual
     *
     * @param summaryFragment Fragmento desde el que se llama a este método
     */
    fun showInterface(summaryFragment: SummaryFragment) {
        _actualState.value!!.showInterface(summaryFragment)
    }

    /**
     * Comprueba si el estado actual es el inicial
     *
     * @return true si el estado actual es el inicial o false en caso contrario
     */
    fun isInitial(): Boolean {
        return _actualState.value is StoppedState
    }

    /**
     * Cambia el estado actual al estado inicial
     */
    fun changeToInitial() {
        changeTo(StoppedState())
    }

    /**
     * Cambia el estado actual al estado Translating
     */
    fun startTranslate() {
        changeTo(TranslatingState())
    }
}