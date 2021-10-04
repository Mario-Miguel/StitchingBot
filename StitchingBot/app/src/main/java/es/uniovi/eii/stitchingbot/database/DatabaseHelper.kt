package es.uniovi.eii.stitchingbot.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import es.uniovi.eii.stitchingbot.model.Logo
import es.uniovi.eii.stitchingbot.model.SewingMachine

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "stitchingBot"
        const val TABLE_SEWING_MACHINES = "SewingMachines"
        const val TABLE_LOGOS = "Logos"
    }

    private object SewingMachineTable {
        const val ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_IMG = "imgUrl"
        const val COLUMN_MOTOR_SPEED = "motorSpeed"

        fun creation(): String {
            return "CREATE TABLE  $TABLE_SEWING_MACHINES ($ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT NOT NULL, $COLUMN_IMG TEXT, $COLUMN_MOTOR_SPEED INTEGER NOT NULL)"
        }
    }

    private object LogoTable {
        const val ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_IMG = "imgUrl"
        const val COLUMN_CATEGORY = "category"

        fun creation(): String {
            return "CREATE TABLE $TABLE_LOGOS ($ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT NOT NULL, $COLUMN_IMG TEXT NULLABLE, $COLUMN_CATEGORY TEXT NOT NULL)"
        }
    }

    private fun getTablesCreationStrings(): ArrayList<String> {
        return arrayListOf(SewingMachineTable.creation(), LogoTable.creation())
    }

    override fun onCreate(db: SQLiteDatabase?) {
        for (table in getTablesCreationStrings())
            db!!.execSQL(table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun getInsertParamsSewingMachinesTable(sewingMachine: SewingMachine): ContentValues{
        val values = ContentValues()
        values.put(SewingMachineTable.COLUMN_NAME, sewingMachine.name)
        values.put(SewingMachineTable.COLUMN_IMG, sewingMachine.imgUrl)
        values.put(SewingMachineTable.COLUMN_MOTOR_SPEED, sewingMachine.motorSteps)
        return values
    }



    fun getAllSewingMachinesColumns(): Array<String>{
        return arrayOf(SewingMachineTable.ID, SewingMachineTable.COLUMN_NAME, SewingMachineTable.COLUMN_IMG, SewingMachineTable.COLUMN_MOTOR_SPEED)
    }


    fun getInsertParamsLogosTable(logo: Logo): ContentValues{
        val values = ContentValues()
        values.put(LogoTable.COLUMN_NAME, logo.title)
        values.put(LogoTable.COLUMN_IMG, logo.imgUrl)
        values.put(LogoTable.COLUMN_CATEGORY, logo.category)
        return values
    }

    fun getAllLogosColumns(): Array<String>{
        return arrayOf(LogoTable.ID, LogoTable.COLUMN_NAME, LogoTable.COLUMN_IMG, LogoTable.COLUMN_CATEGORY)
    }

}