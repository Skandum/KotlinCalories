package com.example.calories.DB

import android.provider.BaseColumns

object DbNameClass {
    const val TABLE_NAME = "torts"
    const val COLUMN_NAME = "name"
    const val COLUMN_CALORIES = "calories"
    const val COLUMN_DESC = "description"
    const val COLUMN_URI = "URI"
    const val DATABASE_VERSION = "1"
    const val DATABASE_NAME = "CaloriesApp.db"

    const val DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

    const val REMOVE_ALL = "DELETE * FROM $TABLE_NAME"

    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_CALORIES TEXT, " +
                "$COLUMN_DESC TEXT, " +
                "$COLUMN_URI TEXT)"
}