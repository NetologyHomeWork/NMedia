package ru.netology.nmedia.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.data.database.PostColumns
import ru.netology.nmedia.data.database.dao.PostDao
import ru.netology.nmedia.data.database.db.PostDatabase
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class DbModule {

    @[Provides Singleton]
    fun providePostDatabase(
        @ApplicationContext context: Context
    ): PostDatabase = Room.databaseBuilder(
        context,
        PostDatabase::class.java,
        PostColumns.DATABASE_NAME
    ).build()

    @[Provides Singleton]
    fun providePostDao(
        postDb: PostDatabase
    ): PostDao = postDb.postDao()
}
