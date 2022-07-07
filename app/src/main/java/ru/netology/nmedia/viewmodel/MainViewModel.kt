package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

class MainViewModel : ViewModel() {

    private val empty = Post(
        id = 0,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "",
        published = "07 июля в 17:50",
    )

    private val repository: PostRepository = PostRepositoryImpl()
    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Post>
        get() = _edited

    val data = repository.getData()

    fun like(post: Post) = repository.like(post)

    fun share(post: Post) = repository.share(post)

    fun deletePost(id: Long) = repository.removeItem(id)

    fun save() {
        _edited.value?.let {
            repository.savePost(it)
        }
        _edited.value = empty
    }

    fun edit(post: Post) {
        _edited.value = post
    }

    fun editedContent(content: String) {
        val text = content.trim()
        if (_edited.value?.content == text) {
            return
        }
        _edited.value = _edited.value?.copy(content = text)
    }
}