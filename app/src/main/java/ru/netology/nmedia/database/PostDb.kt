package ru.netology.nmedia.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase

class PostDb private constructor(db: SQLiteDatabase) {

    val postDao: PostDao = PostDaoImpl(db)

    companion object {
        @Volatile
        private var instance: PostDb? = null

        fun getInstance(context: Context): PostDb {
            return instance ?: synchronized(this) {
                instance ?: PostDb(
                    buildDataBase(context, arrayOf(PostDaoImpl.DDLs))
                ).also { instance = it }
            }
        }

        private fun buildDataBase(context: Context, dDLs: Array<String>) = DbHelper(
            context, 1, "post_db", dDLs
        ).writableDatabase
    }
}