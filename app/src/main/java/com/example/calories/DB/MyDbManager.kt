package com.example.calories.DB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.calories.DB.DbNameClass.COLUMN_CALORIES
import com.example.calories.DB.DbNameClass.COLUMN_DESC
import com.example.calories.DB.DbNameClass.COLUMN_NAME
import com.example.calories.DB.DbNameClass.COLUMN_URI
import com.example.calories.DB.DbNameClass.TABLE_NAME
import android.provider.BaseColumns

class MyDbManager(context: Context) {
    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb() {
        db = myDbHelper.writableDatabase
    }

    fun insertToDb(name: String, calories: String, desc: String, uri: String) {
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_CALORIES, calories)
            put(COLUMN_DESC, desc)
            put(COLUMN_URI, uri)
        }
        db?.insert(TABLE_NAME, null, values)
    }

    fun getDbItems(number : Int): ArrayList<String> {
        val list = ArrayList<String>()
        val cursor = db?.query(TABLE_NAME, null, null, null, null, null, null)

        with(cursor) {
            while (this?.moveToNext()!!) {
//                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
//                listName.add(itemId.toString())

                val itemName = cursor?.getString(number)
                list.add(itemName.toString())
            }
            cursor?.close()
            return list
        }
    }

    fun clearDb(){
        db?.delete(TABLE_NAME,null,null)
    }

    fun closeDb(){
        myDbHelper.close()
    }
}