package com.example.egsapp.database

import android.provider.BaseColumns

object DbName: BaseColumns { //Набор констант для бд
    const val TABLE_NAME = "history" //Имя таблицы
    const val COLUMN_NAME_TITLE = "title" //Имя колонки
    const val COLUMN_NAME_DESCTITLE = "description" //Имя колонки
    const val COLUMN_NAME_IMGURL = "image" //Имя колонки
    const val COLUMN_NAME_STATUSTITLE = "status" //Имя колонки

    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "UserHistory.db" //Название бд

    const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME ("+
            "${BaseColumns._ID} INTEGER PRIMARY KEY, $COLUMN_NAME_TITLE TEXT, " +
            "$COLUMN_NAME_DESCTITLE TEXT, " +
            "$COLUMN_NAME_IMGURL TEXT, " +
            "$COLUMN_NAME_STATUSTITLE TEXT)" //Константа запроса создания таблицы

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME" //Константа запроса удаления таблицы

}