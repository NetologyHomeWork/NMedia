package ru.netology.nmedia.repository

import android.content.Intent
import androidx.lifecycle.LiveData
import ru.netology.nmedia.model.Post

interface PostRepository {
    fun getData(): LiveData<List<Post>>

    fun like(post: Post)

    fun share(post: Post): Intent

    fun removeItem(id: Long)

    fun savePost(post: Post)

    fun launchYoutubeVideo(post: Post): Intent
}