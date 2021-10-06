package es.uniovi.eii.stitchingbot.database

import android.content.ContentValues
import android.content.Context
import android.util.Log
import es.uniovi.eii.stitchingbot.model.SewingMachine

class SewingMachineDatabaseConnection(context: Context) : DatabaseConnection<SewingMachine>(context){


    override fun insert(element: SewingMachine) {
        val values = ContentValues()
        values.putAll(dbHelper!!.getInsertParamsSewingMachinesTable(element))

        database!!.insert(DatabaseHelper.TABLE_SEWING_MACHINES, null, values)
    }

    override fun update(element: SewingMachine){
        val values = ContentValues()
        values.putAll(dbHelper!!.getInsertParamsSewingMachinesTable(element))
        val where = "id=?"
        val whereArgs = arrayOf("${element.id}")

        database!!.update(DatabaseHelper.TABLE_SEWING_MACHINES, values, where, whereArgs)
    }

    override fun delete(element: SewingMachine){
        val where = "id=?"
        val whereArgs = arrayOf("${element.id}")

        database!!.delete(DatabaseHelper.TABLE_SEWING_MACHINES, where, whereArgs)

    }

    override fun getAllData(): ArrayList<SewingMachine> {
        val machines = ArrayList<SewingMachine>()

        val cursor = database!!.query(
            DatabaseHelper.TABLE_SEWING_MACHINES,
            dbHelper!!.getAllSewingMachinesColumns(),
            null,
            null,
            null,
            null,
            null
        )
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val machine = SewingMachine(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3)
            )

            machines.add(machine)
            cursor.moveToNext()
        }

        cursor.close()

        return machines
    }
}