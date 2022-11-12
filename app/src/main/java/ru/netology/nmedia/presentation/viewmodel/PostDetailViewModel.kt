package ru.netology.nmedia.presentation.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.nmedia.data.repository.PostRepository
import ru.netology.nmedia.data.utils.commandSharedFlow
import ru.netology.nmedia.domain.model.Post

class PostDetailViewModel(
    private val repository: PostRepository,
    private val postId: Long
) : ViewModel() {

    private val _currentPost = MutableLiveData<Post>()
    val currentPost: LiveData<Post> get() = _currentPost

    private val _isLoading = MutableStateFlow<IsLoading>(IsLoading.Loading)
    val isLoading = _isLoading.asStateFlow()

    val editPost = commandSharedFlow<Command>()

    init {
        loadPost()
    }

    fun like(post: Post) {
        viewModelScope.launch {
            repository.likeAsync(post)
        }
    }

    fun deletePost(id: Long) {
        viewModelScope.launch {
            repository.removeItemAsync(id)
        }
    }

    fun edit(post: Post) {
        editPost.tryEmit(Command.LaunchEditScreen(post.content))
    }

    fun share(post: Post): Intent = repository.share(post)

    fun loadPost() {
        viewModelScope.launch {
            repository.getPostById(postId).run {
                _currentPost.value = this
                _isLoading.tryEmit(IsLoading.HasLoad)
            }
        }
    }

    sealed interface IsLoading {
        object Loading : IsLoading
        object HasLoad : IsLoading
    }

    sealed interface Command {
        class LaunchEditScreen(val content: String) : Command
    }
}