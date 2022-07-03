package ru.netology.nmedia

import androidx.lifecycle.LiveData

interface PostRepository {
    fun getData(): LiveData<Post>

    fun like()

    fun share()
}