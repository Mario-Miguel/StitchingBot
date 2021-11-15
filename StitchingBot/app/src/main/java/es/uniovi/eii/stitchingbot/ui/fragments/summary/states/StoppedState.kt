package es.uniovi.eii.stitchingbot.ui.fragments.summary.states

import android.view.View
import es.uniovi.eii.stitchingbot.controller.LogoController
import es.uniovi.eii.stitchingbot.controller.SewingMachineController
import es.uniovi.eii.stitchingbot.ui.fragments.summary.SummaryFragment
import es.uniovi.eii.stitchingbot.arduinoCommunication.Translator
import es.uniovi.eii.stitchingbot.arduinoCommunication.BluetoothService
import kotlinx.android.synthetic.main.fragment_summary.*

class StoppedState : State() {
    override fun showInterface(summaryFragment: SummaryFragment) {
        //Mostrar los botones de traducir e iniciar ejecucion
        summaryFragment.btnPauseExecution.visibility = View.GONE
        summaryFragment.btnStopExecution.visibility = View.GONE
        summaryFragment.btnResumeExecution.visibility = View.GONE
        summaryFragment.btnStartTranslate.visibility = View.VISIBLE
        summaryFragment.btnStartExecution.visibility = View.VISIBLE
        summaryFragment.btnStartExecution.isEnabled = checkStartExecutionAvailability(
            summaryFragment.translator,
            summaryFragment.logoController,
            summaryFragment.sewingMachineController
        )
        summaryFragment.btnStartTranslate.isEnabled =
            checkStartTranslationAvailability(summaryFragment.translator)
        summaryFragment.pbExecution.visibility = View.GONE
    }

    override fun toString(): String {
        return "StoppedState"
    }

    /**
     *  Comprueba si se puede comenzar la ejecución
     *
     *  Se puede comenzar la ejecución si se ha realizado la traducción del logotipo, se ha escogido
     *  una máquina de coser y se ha establecido una conexión con el robot
     *
     *  @param translator Traductor de logotipos
     *  @param logoController controlador de los logotipos
     *  @param sewingMachineController controlador de las máquinas de coser
     *  @return true en caso de poder comenzar la ejecución, false en caso contrario
     */
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

    /**
     *  Comprueba si se ha de activar el botón btnStartTranslate
     *
     *  Está desactivado si el proceso de traducción está en proceso o si ya se ha completado
     *
     *  @param translator Traductor de logotipos
     *  @return true si el botón  btnStartTranslate ha de estar activo, false en caso contrario
     */
    private fun checkStartTranslationAvailability(translator: Translator): Boolean {
        return !translator.isInExecution && !translator.translationDone
    }
}