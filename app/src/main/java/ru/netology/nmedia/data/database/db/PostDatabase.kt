package ru.netology.nmedia.data.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.nmedia.data.database.dao.PostDao
import ru.netology.nmedia.data.database.dao.PostRemoteKeyDao
import ru.netology.nmedia.data.database.entity.PostEntity
import ru.netology.nmedia.data.database.entity.PostRemoteKeyEntity

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class], version = 5, exportSchema = false)
abstract class PostDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao

    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}