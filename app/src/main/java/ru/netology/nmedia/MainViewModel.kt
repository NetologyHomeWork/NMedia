package ru.netology.nmedia

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val repository: PostRepository = PostRepositoryImpl()

    val data = repository.getData()

    fun like() = repository.like()

    fun share() = repository.share()

}