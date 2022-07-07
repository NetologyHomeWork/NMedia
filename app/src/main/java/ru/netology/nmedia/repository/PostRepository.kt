package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.model.Post

interface PostRepository {
    fun getData(): LiveData<List<Post>>

    fun like(post: Post)

    fun share(post: Post)

    fun removeItem(id: Long)

    fun savePost(post: Post)
}