package ru.netology.nmedia.data.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.nmedia.data.database.dao.PostDao
import ru.netology.nmedia.data.database.entity.PostEntity

@Database(entities = [PostEntity::class], version = 4, exportSchema = false)
abstract class PostDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao
}