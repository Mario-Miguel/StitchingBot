package es.uniovi.eii.stitchingbot

import es.uniovi.eii.stitchingbot.arduinoCommunication.ArduinoCommands
import es.uniovi.eii.stitchingbot.arduinoCommunication.BluetoothService
import es.uniovi.eii.stitchingbot.arduinoCommunication.Translator
import io.mockk.every
import io.mockk.mockkObject

class MockingHelper {

    fun mockArduinoCommands(){
        mockkObject(ArduinoCommands)
        every { ArduinoCommands.startExecution(any()) } answers {
            ArduinoCommands.startExecutionTesting()
        }
    }

    fun mockTranslator() {
        mockkObject(Translator)
        val translation =
            arrayOfNulls<Pair<Int, Int>>(10000).mapIndexed { index, _ -> Pair(index, index) }
        every { Translator.translation } returns translation.toMutableList()
    }

    fun mockBluetoothService() {
        //Se necesita simular el objeto para aislarlo de la comunicación por bluetooth y el emparejamiento con dispositivos
        mockkObject(BluetoothService)
        //Si se intenta conectar, ejecutar otro bloque de código en cambio de tryToConnect
        every { BluetoothService.tryToConnect(any(), any()) } answers {
            val callback = secondArg<() -> Unit>()
            BluetoothService.tryToConnectTesting(callback)
            //Ahora simulamos que se ha conectado correctamente
            every { BluetoothService.isConnected() } returns true

            //Si se desconecta del dispositivo bluetooth, también lo hay que simular
            every { BluetoothService.closeConnectionSocket() } answers {
                //Ahora que está desconectado, deshacemos el mock del método isConnected
                every { BluetoothService.isConnected() } returns false
            }
        }

        every { BluetoothService.write(any()) } answers {}
    }
}