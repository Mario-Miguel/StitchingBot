package es.uniovi.eii.stitchingbot.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import es.uniovi.eii.stitchingbot.model.SewingMachine

class SewingMachinedatabaseConnection(context: Context) {

    private var dbHelper: DatabaseHelper? = null
    private var database: SQLiteDatabase? = null

    init {
        dbHelper = DatabaseHelper(context)
    }

    fun open() {
        database = dbHelper!!.writableDatabase
    }

    fun close() {
        dbHelper!!.close()
    }


    fun insert(machine: SewingMachine) {
        val values = ContentValues()
        values.putAll(dbHelper!!.getInsertParamsSewingMachinesTable(machine))

        database!!.insert(DatabaseHelper.TABLE_SEWING_MACHINES, null, values)
    }

    fun getAllData(): ArrayList<SewingMachine> {
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
            var machine = SewingMachine(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3) > 0
            )

            machines.add(machine)
            cursor.moveToNext()
        }

        cursor.close()

        return machines
    }
}