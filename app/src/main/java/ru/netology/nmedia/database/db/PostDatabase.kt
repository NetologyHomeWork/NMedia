package ru.netology.nmedia.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.nmedia.database.PostColumns.DATABASE_NAME
import ru.netology.nmedia.database.dao.PostDao
import ru.netology.nmedia.database.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1)
abstract class PostDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao

    companion object {
        @Volatile
        private var instance: PostDatabase? = null

        fun getInstance(context: Context): PostDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDataBase(context).also { instance = it }
            }
        }

        private fun buildDataBase(context: Context) =
            Room.databaseBuilder(context, PostDatabase::class.java, DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
    }
}