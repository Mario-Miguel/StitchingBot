package es.uniovi.eii.stitchingbot.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase

abstract class DatabaseConnection<T>(context: Context) {
    protected var dbHelper: DatabaseHelper? = null
    protected var database: SQLiteDatabase? = null

    init {
        dbHelper = DatabaseHelper(context)
    }

    fun open() {
        database = dbHelper!!.writableDatabase
    }

    fun close() {
        dbHelper!!.close()
    }

    abstract fun insert(element: T)
    abstract fun update(element: T)
    abstract fun delete(element: T)
    abstract fun getAllData(): ArrayList<T>

}