package ru.netology.nmedia.data.utils

import okhttp3.*
import ru.netology.nmedia.data.repository.PostRepository
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

fun OkHttpClient.execute(request: Request) {
    this.newCall(request)
        .execute()
        .close()
}

fun <T> OkHttpClient.enqueue(
    request: Request,
    value: T,
    callback: PostRepository.PostCallback<T>
) {
    newCall(request)
        .enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    callback.onSuccess(value)
                } catch (e: Exception) {
                    callback.onFailure(e)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(e)
            }
        })
}

fun String.formatDate(): String {
    val formatter = SimpleDateFormat.getDateTimeInstance()
    return formatter.format(Date(this.toLong() * 1000))
}
