package ru.netology.nmedia

import androidx.lifecycle.LiveData

interface PostRepository {
    fun getData(): LiveData<List<Post>>

    fun like(post: Post)

    fun share(post: Post)
}