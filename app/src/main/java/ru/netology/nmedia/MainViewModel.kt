package ru.netology.nmedia

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val repository: PostRepository = PostRepositoryImpl()

    val data = repository.getData()

    fun like(post: Post) = repository.like(post)

    fun share(post: Post) = repository.share(post)

}