package ru.netology.nmedia.data.auth

import ru.netology.nmedia.domain.model.PhotoModel

interface AuthRepository {

    suspend fun signIn(login: String, password: String): AuthState

    suspend fun signUp(
        name: String,
        login: String,
        password: String,
        media: PhotoModel? = null
    ): AuthState
}