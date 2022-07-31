package ru.netology.nmedia.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(
    context: Context,
    dbVersion: Int,
    dbName: String,
    private val dDLS: Array<String>
) : SQLiteOpenHelper(context, dbName, null, dbVersion) {

    override fun onCreate(db: SQLiteDatabase) {
        dDLS.forEach { db.execSQL(it) }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS posts_table")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        super.onDowngrade(db, oldVersion, newVersion)
    }
}