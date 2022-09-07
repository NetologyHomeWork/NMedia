package ru.netology.nmedia.data.repository

import android.content.Intent
import ru.netology.nmedia.domain.model.Post

interface PostRepository {
    fun getData(): List<Post>

    fun like(post: Post)

    fun share(post: Post): Intent

    fun removeItem(id: Long)

    fun savePost(post: Post)

    fun findPostById(postId: Long): Post

    fun launchYoutubeVideo(post: Post): Intent
}