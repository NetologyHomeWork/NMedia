package ru.netology.nmedia.data.utils

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.data.repository.PostRepository
import java.text.SimpleDateFormat
import java.util.*

fun OkHttpClient.execute(request: Request) {
    this.newCall(request)
        .execute()
        .close()
}

fun <T> Call<T>.enqueue(
    callback: PostRepository.PostCallback<T>
) {
    this.enqueue(object : Callback<T> {

            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful.not()) {
                    callback.onFailure(IllegalStateException(response.message()))
                    return
                }

                callback.onSuccess(response.body() ?: throw IllegalStateException("body is null"))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onFailure(t)
            }
        })
}

fun String.formatDate(): String {
    val formatter = SimpleDateFormat.getDateTimeInstance()
    return formatter.format(Date(this.toLong() * 1000))
}
