package com.example.egsapp.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.egsapp.Game

class DbManager (context: Context) {
    val dbHelper = DbHelper(context)
    var dataBase: SQLiteDatabase?= null

    fun openDb(){  //Функция открытия БД
        dataBase = dbHelper.writableDatabase
    }
    fun insertToDb(game: Game){ //Функция записи БД
        val values = ContentValues().apply {
            put(DbName.COLUMN_NAME_TITLE, game.title)
            put(DbName.COLUMN_NAME_DESCTITLE, game.description)
            put(DbName.COLUMN_NAME_IMGURL, game.imageURL)
            put(DbName.COLUMN_NAME_STATUSTITLE, game.status)

        }
        dataBase?.insert(DbName.TABLE_NAME, null, values)
    }

    @SuppressLint("Range")
    fun readDbDataFromDb(): ArrayList<Game>{ //Считывание из бд в лист
        val dataList = ArrayList<Game>()

        val cursor = dataBase?.query(DbName.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null)
        with(cursor){
            while (this?.moveToNext()!!){
                val titleText = cursor?.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_TITLE)) //Считывание в переменную из столбца
                val descText = cursor?.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_DESCTITLE)) //Считывание в переменную из столбца
                val imgText = cursor?.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_IMGURL)) //Считывание в переменную из столбца
                val statusText = cursor?.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_STATUSTITLE)) //Считывание в переменную из столбца

                dataList.add(Game(titleText, descText, imgText, statusText)) //Добавляем в лист считанный объект
            }
        }
        cursor?.close()
        return dataList//Возврат листа
    }

    fun deleteFromDb(){ //ОЧИСТКА БД
        dataBase?.execSQL(DbName.SQL_DELETE_TABLE) //УДАЛЕНИЕ ТЕКУЩЕЙ ТАБЛИЦЫ
        dataBase?.execSQL(DbName.CREATE_TABLE) //СОЗДАНИЕ НОВОЙ ТАБЛИЦЫ
    }

    fun closeDb(){ //Функция закрытия БД
        dbHelper.close()
    }
}