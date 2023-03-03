package ru.netology.nmedia.data.repository

import android.content.Intent
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.domain.model.PhotoModel
import ru.netology.nmedia.domain.model.Post

interface PostRepository {

    val data: Flow<PagingData<Post>>
    suspend fun getDataAsync(): List<Post>

    suspend fun loadNew()

    fun getNewerCount(postId: Long): Flow<Int>

    suspend fun likeAsync(post: Post)

    fun share(post: Post): Intent

    suspend fun removeItemAsync(id: Long)

    suspend fun savePostAsync(post: Post, model: PhotoModel? = null)

    fun launchYoutubeVideo(post: Post): Intent

    suspend fun getPostById(postId: Long): Post

    suspend fun removeFromDatabase(postId: Long)
}