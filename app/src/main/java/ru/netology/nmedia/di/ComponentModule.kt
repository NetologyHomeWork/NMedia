package ru.netology.nmedia.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.data.auth.AuthRepository
import ru.netology.nmedia.data.auth.AuthRepositoryImpl
import ru.netology.nmedia.data.database.repository.PostDaoImpl
import ru.netology.nmedia.data.database.repository.PostDaoRepository
import ru.netology.nmedia.data.repository.PostRepository
import ru.netology.nmedia.data.repository.PostRepositoryImpl
import ru.netology.nmedia.presentation.viewmodel.*
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
interface ComponentModule {

    @[Binds Singleton]
    fun bindsPostRepository(impl: PostRepositoryImpl): PostRepository

    @[Binds Singleton]
    fun bindsAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @[Binds Singleton]
    fun bindsPostDaoRepository(impl: PostDaoImpl): PostDaoRepository
}