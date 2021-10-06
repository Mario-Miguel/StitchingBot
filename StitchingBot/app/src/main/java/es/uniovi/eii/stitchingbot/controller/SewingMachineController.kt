package es.uniovi.eii.stitchingbot.controller

import android.content.Context
import android.util.Log
import es.uniovi.eii.stitchingbot.database.SewingMachineDatabaseConnection
import es.uniovi.eii.stitchingbot.model.SewingMachine

class SewingMachineController {

    private var sewingMachine = SewingMachine()


    fun setSewingMachine(name: String? = sewingMachine.name, imgUrl : String? = sewingMachine.imgUrl, motorSteps: Int=sewingMachine.motorSteps){
        val auxSewingMachine = SewingMachine(sewingMachine.id, name,imgUrl, motorSteps)
        this.sewingMachine = auxSewingMachine
    }

    fun setSewingMachine(sewingMachine: SewingMachine){
        this.sewingMachine=sewingMachine
    }

    fun getSewingMachine(): SewingMachine{
        return sewingMachine
    }

    fun addSewingMachine(context:Context){
        val databaseConnection = SewingMachineDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.insert(sewingMachine)
        databaseConnection.close()
    }

    fun updateSewingMachine(context:Context){
        Log.d("SMDETAILS2", "SewingMachine to update: $sewingMachine")
        val databaseConnection = SewingMachineDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.update(sewingMachine)
        databaseConnection.close()
    }

    fun deleteSewingMachine(context:Context){
        val databaseConnection = SewingMachineDatabaseConnection(context)
        databaseConnection.open()
        databaseConnection.delete(sewingMachine)
        databaseConnection.close()
    }


    fun getAllSewingMachines(context:Context): ArrayList<SewingMachine>{
        val databaseConnection =SewingMachineDatabaseConnection(context)
        databaseConnection.open()
        val list = databaseConnection.getAllData()
        databaseConnection.close()

        return list
    }
}