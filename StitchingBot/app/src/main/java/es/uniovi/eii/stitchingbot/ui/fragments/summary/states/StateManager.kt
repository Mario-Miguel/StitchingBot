package es.uniovi.eii.stitchingbot.ui.fragments.summary.states

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.uniovi.eii.stitchingbot.ui.fragments.summary.SummaryFragment

object StateManager {

    private var _actualState: MutableLiveData<State> = MutableLiveData(StoppedState())
    val actualState: LiveData<State>
        get() = _actualState

    fun changeTo(state: State) {
        _actualState.postValue(state)
    }

    fun showInterface(summaryFragment: SummaryFragment){
        _actualState.value!!.showInterface(summaryFragment)
    }

    fun changeToInitial() {
        changeTo(StoppedState())
    }

    fun startTranslate() {
        changeTo(TranslatingState())
    }
}