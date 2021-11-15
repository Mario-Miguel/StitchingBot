package es.uniovi.eii.stitchingbot.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import es.uniovi.eii.stitchingbot.model.Logo
import es.uniovi.eii.stitchingbot.model.SewingMachine

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        for (table in getTablesCreationStrings())
            db!!.execSQL(table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_SEWING_MACHINES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOGOS")
        onCreate(db)
    }

    /**
     * Devuelve los parámetros a insertar de una máquina de coser
     *
     * @param sewingMachine máquina de coser que se desea insertar
     * @return ContentValues con los datos necesarios para insertar una máquina de coser
     */
    fun getInsertParamsSewingMachinesTable(sewingMachine: SewingMachine): ContentValues {
        val values = ContentValues()
        values.put(SewingMachineTable.COLUMN_NAME, sewingMachine.name)
        values.put(SewingMachineTable.COLUMN_IMG, sewingMachine.imgUrl)
        values.put(SewingMachineTable.COLUMN_MOTOR_STEPS, sewingMachine.motorSteps)
        return values
    }

    /**
     * Devuelve todas las columnas de la tabla de las máquinas de coser
     *
     * @return Array<String> con las columnas de la tabla
     */
    fun getAllSewingMachinesColumns(): Array<String> {
        return arrayOf(
            SewingMachineTable.ID,
            SewingMachineTable.COLUMN_NAME,
            SewingMachineTable.COLUMN_IMG,
            SewingMachineTable.COLUMN_MOTOR_STEPS
        )
    }

    /**
     * Devuelve los parámetros a insertar de un logotipo
     *
     * @param logo logotipo que se desea insertar
     * @return ContentValues con los datos necesarios para insertar un logotipo
     */
    fun getInsertParamsLogosTable(logo: Logo): ContentValues {
        val values = ContentValues()
        values.put(LogoTable.COLUMN_IMG, logo.imgUrl)
        return values
    }

    /**
     * Devuelve todas las columnas de la tabla de los logotipos
     *
     * @return Array<String> con las columnas de la tabla
     */
    fun getAllLogosColumns(): Array<String> {
        return arrayOf(
            LogoTable.ID,
            LogoTable.COLUMN_IMG,
        )
    }

    /**
     * Devuelve las queries necesarias para crear las tablas de la base de datos
     *
     * @return ArrayList<String> con las queries
     */
    private fun getTablesCreationStrings(): ArrayList<String> {
        return arrayListOf(SewingMachineTable.creation(), LogoTable.creation())
    }

    /**
     * Contiene los datos básicos de la base de datos
     *
     * @property DATABASE_VERSION versión de la base de datos
     * @property DATABASE_NAME nombre de la base de datos
     * @property TABLE_SEWING_MACHINES nombre de la tabla de las máquinas de coser
     * @property TABLE_LOGOS nombre de la tabla de los logotipos
     */
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "stitchingBot"
        const val TABLE_SEWING_MACHINES = "SewingMachines"
        const val TABLE_LOGOS = "Logos"
    }

    /**
     * Contiene los campos de la tabla de las máquinas de coser así como la query para crearla
     */
    private object SewingMachineTable {
        const val ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_IMG = "imgUrl"
        const val COLUMN_MOTOR_STEPS = "motorSteps"

        fun creation(): String {
            return "CREATE TABLE  $TABLE_SEWING_MACHINES (" +
                    "$ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_NAME TEXT NOT NULL, " +
                    "$COLUMN_IMG TEXT, " +
                    "$COLUMN_MOTOR_STEPS INTEGER NOT NULL)"
        }
    }

    /**
     * Contiene los campos de la tabla de los logotipos así como la query para crearla
     */
    private object LogoTable {
        const val ID = "id"
        const val COLUMN_IMG = "imgUrl"

        fun creation(): String {
            return "CREATE TABLE $TABLE_LOGOS (" +
                    "$ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_IMG TEXT NULLABLE)"
        }
    }

}