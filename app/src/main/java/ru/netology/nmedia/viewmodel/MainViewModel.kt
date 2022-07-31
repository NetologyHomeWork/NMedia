package ru.netology.nmedia.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.database.PostDb
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

class MainViewModel(
    application: Application
) : ViewModel() {

    private val empty = Post(
        id = 0,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "",
        published = "07 июля в 17:50",
    )

    private val repository: PostRepository = PostRepositoryImpl(
        PostDb.getInstance(application).postDao
    )
    private val _edited = MutableLiveData(empty)

    val data = repository.getData()

    fun like(post: Post) = repository.like(post)

    fun share(post: Post): Intent = repository.share(post)

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

    fun launchYoutubeVideo(post: Post): Intent {
        return repository.launchYoutubeVideo(post)
    }

    fun findPostById(postId: Long): Post? {
        return repository.findPostById(postId)
    }

    fun editingClear() {
        _edited.value = empty
    }
}