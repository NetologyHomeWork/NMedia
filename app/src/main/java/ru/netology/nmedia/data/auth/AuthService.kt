package ru.netology.nmedia.data.auth

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.domain.model.PushToken

interface AuthService {

    @[FormUrlEncoded POST("api/users/authentication")]
    suspend fun login(
        @Field("login") login: String,
        @Field("pass") password: String
    ): Response<AuthState>

    @[FormUrlEncoded POST("api/users/registration")]
    suspend fun signUp(
        @Field("login") login: String,
        @Field("pass") password: String,
        @Field("name") name: String
    ): Response<AuthState>

    @[Multipart POST("api/users/registration")]
    suspend fun signUpWithPhoto(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part media: MultipartBody.Part,
    ): Response<AuthState>

    @POST("api/users/push-tokens")
    suspend fun sendPushToken(
        @Body token: PushToken
    ): Response<Unit>
}