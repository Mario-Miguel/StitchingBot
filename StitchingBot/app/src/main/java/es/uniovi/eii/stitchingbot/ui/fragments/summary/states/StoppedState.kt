package es.uniovi.eii.stitchingbot.ui.fragments.summary.states

import android.view.View
import es.uniovi.eii.stitchingbot.controller.LogoController
import es.uniovi.eii.stitchingbot.controller.SewingMachineController
import es.uniovi.eii.stitchingbot.ui.fragments.summary.SummaryFragment
import es.uniovi.eii.stitchingbot.arduinoCommunication.Translator
import es.uniovi.eii.stitchingbot.arduinoCommunication.BluetoothService
import kotlinx.android.synthetic.main.fragment_summary.*

class StoppedState: State() {
    override fun showInterface(summaryFragment: SummaryFragment) {
        //Mostrar los botones de traducir e iniciar ejecucion
        summaryFragment.btnPauseExecution.visibility = View.GONE
        summaryFragment.btnStopExecution.visibility = View.GONE
        summaryFragment.btnResumeExecution.visibility = View.GONE

        summaryFragment.btnStartTranslate.visibility= View.VISIBLE
        summaryFragment.btnStartExecution.visibility= View.VISIBLE

        summaryFragment.btnStartExecution.isEnabled = checkStartExecutionAvailability(summaryFragment.translator, summaryFragment.logoController, summaryFragment.sewingMachineController)
        summaryFragment.btnStartTranslate.isEnabled = checkStartTranslationAvailability(summaryFragment.translator)

        summaryFragment.pbExecution.visibility = View.GONE
    }

    private fun checkStartExecutionAvailability(
        translator: Translator,
        logoController: LogoController,
        sewingMachineController: SewingMachineController
    ): Boolean {
        return logoController.isLogoSelected()
                && sewingMachineController.isSewingMachineSelected()
                && translator.translationDone
        && BluetoothService.isConnected()
    }

    private fun checkStartTranslationAvailability(translator: Translator): Boolean {
        return !translator.isInExecution && !translator.translationDone
    }
}