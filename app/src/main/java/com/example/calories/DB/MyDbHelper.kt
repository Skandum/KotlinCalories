package com.example.calories.DB

import android.content.Context
import android.database.sqlite.SQLiteDatabase

import com.example.calories.DB.DbNameClass.CREATE_TABLE
import android.database.sqlite.SQLiteOpenHelper
import com.example.calories.DB.DbNameClass.DELETE_TABLE
import com.example.calories.DB.DbNameClass.REMOVE_ALL

class MyDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?){
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL(DELETE_TABLE)
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "CaloriesApp.db"
    }
}