package ru.netology.nmedia.data.repository

import android.content.Intent
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.domain.model.Post
import ru.netology.nmedia.domain.model.PostUIModel

interface PostRepository {

    val data: Flow<List<PostUIModel>>
    suspend fun getDataAsync(): List<Post>

    fun getNewerCount(postId: Long): Flow<Int>

    suspend fun likeAsync(post: Post)

    fun share(post: Post): Intent

    suspend fun removeItemAsync(id: Long)

    suspend fun savePostAsync(post: Post)

    fun launchYoutubeVideo(post: Post): Intent

    suspend fun getPostById(postId: Long): Post

    suspend fun removeFromDatabase(postId: Long)
}