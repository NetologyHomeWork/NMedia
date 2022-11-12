package ru.netology.nmedia.data.repository

import android.content.Intent
import androidx.lifecycle.LiveData
import ru.netology.nmedia.domain.model.Post
import ru.netology.nmedia.domain.model.PostUIModel

interface PostRepository {

    val data: LiveData<List<PostUIModel>>
    suspend fun getDataAsync(): List<Post>

    suspend fun likeAsync(post: Post)

    fun share(post: Post): Intent

    suspend fun removeItemAsync(id: Long)

    suspend fun savePostAsync(post: Post)

    fun launchYoutubeVideo(post: Post): Intent

    suspend fun getPostById(postId: Long): Post

    suspend fun removeFromDatabase(postId: Long)
}