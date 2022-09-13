package ru.netology.nmedia.data.repository

import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.data.utils.execute
import ru.netology.nmedia.data.utils.parsingUrlLink
import ru.netology.nmedia.domain.model.Post
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private val client = OkHttpClient.Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}
    private val typePost = object : TypeToken<Post>() {}
    private val jsonType = "application/json".toMediaType()

    override fun getData(): List<Post> {
        val request = Request.Builder()
            .url("${BuildConfig.BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw IllegalStateException("request body is null") }
            .let { gson.fromJson(it, typeToken.type) }
    }

    override fun savePost(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BuildConfig.BASE_URL}/api/posts")
            .build()

        client.execute(request)
    }

    override fun removeItem(id: Long) {
        val request = Request.Builder()
            .delete()
            .url("${BuildConfig.BASE_URL}/api/posts/$id")
            .build()

        client.execute(request)
    }

    override fun like(post: Post) {
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

        client.execute(request)
    }

    override fun findPostById(postId: Long): Post {
        val request = Request.Builder()
            .get()
            .url("${BuildConfig.BASE_URL}/api/posts/$postId")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw IllegalStateException("request body is null") }
            .let { gson.fromJson(it, typePost.type) }
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
