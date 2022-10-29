package com.example.egsapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context): SQLiteOpenHelper(context, DbName.DATABASE_NAME, null, DbName.DATABASE_VERSION) {
    override fun onCreate(p0: SQLiteDatabase?) { //Функция создания таблицы в бд
        p0?.execSQL(DbName.CREATE_TABLE)
        }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) { //Функция обновления таблицы в бд
        p0?.execSQL(DbName.SQL_DELETE_TABLE)
        onCreate(p0)
    }
}