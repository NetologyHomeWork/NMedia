package ru.netology.nmedia.data

sealed class AppException(
    val code: Int,
    override val message: String
) : RuntimeException(message) {

    class ApiError(
        code: Int,
        message: String
    ) : AppException(code, message)

    class NetworkError(message: String) : AppException(410, message)

    class UnknownError(message: String) : AppException(-1, message)
}

