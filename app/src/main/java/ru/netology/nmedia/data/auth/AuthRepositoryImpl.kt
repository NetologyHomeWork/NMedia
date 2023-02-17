package ru.netology.nmedia.data.auth

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.data.AppException
import ru.netology.nmedia.data.utils.ResourceManager
import ru.netology.nmedia.data.utils.wrapException
import ru.netology.nmedia.domain.model.PhotoModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val resourceManager: ResourceManager
) : AuthRepository {

    override suspend fun signIn(
        login: String,
        password: String
    ): AuthState = wrapException(resourceManager) {
        val response = authService.login(login, password)
        if (response.isSuccessful.not() || response.body() == null) {
            throw AppException.ApiError(response.code(), response.message())
        }
        return@wrapException response.body()!!
    }

    override suspend fun signUp(
        name: String,
        login: String,
        password: String,
        media: PhotoModel?
    ): AuthState = wrapException(resourceManager) {
        media?.let {
            val nameBody = name.toRequestBody("text/plain".toMediaType())
            val loginBody = name.toRequestBody("text/plain".toMediaType())
            val passwordBody = name.toRequestBody("text/plain".toMediaType())
            val mediaBody = MultipartBody.Part.createFormData(
                "file", requireNotNull(it.file).name, it.file.asRequestBody()
            )
            val response = authService.signUpWithPhoto(
                login = loginBody,
                pass = passwordBody,
                name = nameBody,
                media = mediaBody
            )
            if (response.isSuccessful.not() || response.body() == null) {
                throw AppException.ApiError(response.code(), response.message())
            }
            return@wrapException response.body()!!
        } ?: run {
            val response = authService.signUp(login, password, name)
            if (response.isSuccessful.not() || response.body() == null) {
                throw AppException.ApiError(response.code(), response.message())
            }
            return@wrapException response.body()!!
        }
    }
}