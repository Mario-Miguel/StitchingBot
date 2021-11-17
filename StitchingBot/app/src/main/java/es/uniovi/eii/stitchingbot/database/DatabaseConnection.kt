package es.uniovi.eii.stitchingbot.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase

abstract class DatabaseConnection<T>(context: Context)  {
    protected var dbHelper: DatabaseHelper? = null
    protected var database: SQLiteDatabase? = null

    init {
        dbHelper = DatabaseHelper(context)
    }

    /**
     * Abre una conexión con la base de datos
     */
    fun open() {
        database = dbHelper!!.writableDatabase
    }

    /**
     * Cierra la conexión con la base de datos
     */
    fun close() {
        dbHelper!!.close()
    }

    fun deleteAllData() {
        dbHelper!!.onUpgrade(database, 0, 0)
    }

    /**
     * Añade un elemento a la base de datos
     *
     * @param element elemento que se desea añadir a la base de datos
     */
    abstract fun insert(element: T)

    /**
     * Actualiza un elemento en la base de datos
     *
     * @param element elemento que se desea actualizar en la base de datos
     */
    abstract fun update(element: T)

    /**
     * Elimina un elemento de la base de datos
     *
     * @param element elemento que se desea eliminar de la base de datos
     */
    abstract fun delete(element: T)

    /**
     * Obtiene todos los elementos de la base de datos
     *
     * @return todos los elementos existentes en la base de datos
     */
    abstract fun getAllData(): ArrayList<T>

}