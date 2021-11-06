package es.uniovi.eii.stitchingbot.database

import android.content.ContentValues
import android.content.Context
import es.uniovi.eii.stitchingbot.model.Logo

class LogoDatabaseConnection(context: Context) : DatabaseConnection<Logo>(context) {

    override fun insert(element: Logo) {
        val values = ContentValues()
        values.putAll(dbHelper!!.getInsertParamsLogosTable(element))
        database!!.insert(DatabaseHelper.TABLE_LOGOS, null, values)
    }

    override fun update(element: Logo) {
        val values = ContentValues()
        values.putAll(dbHelper!!.getInsertParamsLogosTable(element))
        val where = "id=?"
        val whereArgs = arrayOf("${element.id}")
        database!!.update(DatabaseHelper.TABLE_LOGOS, values, where, whereArgs)
    }

    override fun delete(element: Logo) {
        val where = "id=?"
        val whereArgs = arrayOf("${element.id}")
        database!!.delete(DatabaseHelper.TABLE_LOGOS, where, whereArgs)
    }

    override fun getAllData(): ArrayList<Logo> {
        val logos = ArrayList<Logo>()

        val cursor = database!!.query(
            DatabaseHelper.TABLE_LOGOS,
            dbHelper!!.getAllLogosColumns(),
            null,
            null,
            null,
            null,
            null
        )
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val logo = Logo(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3)
            )

            logos.add(logo)
            cursor.moveToNext()
        }
        cursor.close()

        return logos
    }

    /**
     * Obtiene el último logotipo añadido a la base de datos
     *
     * @return último logo añadido a la base de datos
     */
    fun getLastElement(): Logo {
        val orderBy = "id DESC LIMIT 1"
        val cursor = database!!.query(
            DatabaseHelper.TABLE_LOGOS,
            dbHelper!!.getAllLogosColumns(),
            null,
            null,
            null,
            null,
            orderBy
        )
        cursor.moveToFirst()
        val logo = Logo(
            cursor.getInt(0),
            cursor.getString(1),
            cursor.getString(2),
            cursor.getString(3)
        )
        cursor.close()

        return logo
    }

}