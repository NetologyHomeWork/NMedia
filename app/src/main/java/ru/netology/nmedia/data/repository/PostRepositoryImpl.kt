package ru.netology.nmedia.data.repository

import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.data.utils.enqueue
import ru.netology.nmedia.data.utils.parsingUrlLink
import ru.netology.nmedia.domain.model.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private val client = OkHttpClient.Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}
    private val jsonType = "application/json".toMediaType()

    override fun getDataAsync(callback: PostRepository.PostCallback<List<Post>>) {
        val request = Request.Builder()
            .url("${BuildConfig.BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                        ?: throw NullPointerException("response body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onFailure(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailure(e)
                }
            })
    }

    override fun savePostAsync(post: Post, callback: PostRepository.PostCallback<Post>) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BuildConfig.BASE_URL}/api/posts")
            .build()

        client.enqueue(request, post, callback)
    }

    override fun removeItemAsync(id: Long, callback: PostRepository.PostCallback<Long>) {
        val request = Request.Builder()
            .delete()
            .url("${BuildConfig.BASE_URL}/api/posts/$id")
            .build()

        client.enqueue(request, id, callback)
    }

    override fun likeAsync(post: Post, callback: PostRepository.PostCallback<Post>) {
        val request = if (post.isLike.not()) {
            Request.Builder()
                .post(gson.toJson(post.id).toRequestBody(jsonType))
                .url("${BuildConfig.BASE_URL}/api/posts/${post.id}/likes")
                .build()
        } else {
            Request.Builder()
                .delete()
                .url("${BuildConfig.BASE_URL}/api/posts/${post.id}/likes")
                .build()
        }

        client.enqueue(request, post, callback)
    }


    override fun share(post: Post): Intent {
        return sendIntent(post)
    }

    override fun launchYoutubeVideo(post: Post): Intent {
        val link = parsingUrlLink(post.content)
        return Intent(Intent.ACTION_VIEW, Uri.parse(link))
    }

    private fun sendIntent(post: Post): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post.content)
            type = "text/plain"
        }
    }
}
