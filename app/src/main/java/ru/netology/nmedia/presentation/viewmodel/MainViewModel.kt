package ru.netology.nmedia.presentation.viewmodel

import android.content.Intent
import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.data.AppException
import ru.netology.nmedia.data.repository.PostRepository
import ru.netology.nmedia.data.utils.SingleLiveEvent
import ru.netology.nmedia.domain.model.FeedModel
import ru.netology.nmedia.domain.model.FeedModelState
import ru.netology.nmedia.domain.model.Post

class MainViewModel(
    private val repository: PostRepository
) : ViewModel() {

    private val _commands = MutableStateFlow<Command>(Command.ShowContent)
    val commands = _commands.asStateFlow()

    private val responseError = MutableStateFlow<ResponseError?>(null)
    private val errorPost = MutableStateFlow<Post?>(null)

    val data: LiveData<FeedModel>
        get() = repository.data.map {
            FeedModel(it, it.isEmpty())
        }

    private val _state = MutableLiveData<FeedModelState>(FeedModelState.Idle)
    val state: LiveData<FeedModelState> get() = _state

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> get() = _postCreated

    init {
        loadPost()
    }

    fun loadPost() = viewModelScope.launch {
        try {
            _state.value = FeedModelState.Loading
            repository.getDataAsync()
            _state.value = FeedModelState.Idle
        } catch (e: AppException) {
            _commands.tryEmit(Command.ShowErrorSnackbar(e.message))
            _state.value = FeedModelState.Error(e.message)
            responseError.tryEmit(ResponseError.LoadPostError)
            errorPost.tryEmit(null)
        } finally {
            _state.value = FeedModelState.Idle
        }
    }

    fun refresh() = viewModelScope.launch {
        try {
            _state.value = FeedModelState.Refreshing
            repository.getDataAsync()
        } catch (e: AppException) {
            _commands.tryEmit(Command.ShowErrorSnackbar(e.message))
            _state.value = FeedModelState.Error(e.message)
            responseError.tryEmit(ResponseError.LoadPostError)
            errorPost.tryEmit(null)
        } finally {
            _state.value = FeedModelState.Idle
        }
    }

    fun save(post: Post) {
        viewModelScope.launch {
            try {
                repository.savePostAsync(post)
            } catch (e: AppException) {
                responseError.tryEmit(ResponseError.SavePostError)
                errorPost.tryEmit(post)
                _state.value = FeedModelState.Error(e.message)
            } finally {
                _postCreated.setValue(Unit)
            }
        }
    }

    fun like(post: Post) = viewModelScope.launch {
        try {
            repository.likeAsync(post)
        } catch (e: AppException) {
            _commands.tryEmit(Command.ShowErrorSnackbar(e.message))
            _state.value = FeedModelState.Error(e.message)
            responseError.tryEmit(ResponseError.LikePostError)
            errorPost.tryEmit(post)
        }
    }

    fun deletePost(post: Post) = viewModelScope.launch {
        try {
            repository.removeItemAsync(post.id)
        } catch (e: AppException) {
            _commands.tryEmit(Command.ShowErrorSnackbar(e.message))
            _state.value = FeedModelState.Error(e.message)
            responseError.tryEmit(ResponseError.DeletePostError)
            errorPost.tryEmit(post)
        }
    }

    fun share(post: Post): Intent = repository.share(post)

    fun launchYoutubeVideo(post: Post): Intent {
        return repository.launchYoutubeVideo(post)
    }

    fun retry() {
        viewModelScope.launch {
            responseError.collectLatest { error ->
                when (error) {
                    is ResponseError.LoadPostError -> refresh()
                    ResponseError.DeletePostError -> deletePost(errorPost.value!!)
                    ResponseError.LikePostError -> like(errorPost.value!!)
                    ResponseError.SavePostError -> retrySavePost(errorPost.value!!)
                    null -> { /* no-op */ }
                }
            }
        }
    }

    fun retrySavePost(post: Post) {
        viewModelScope.launch {
            try {
                repository.savePostAsync(post.copy(id = 0))
                repository.removeFromDatabase(post.id)
                refresh()
            } catch (e: AppException) {
                responseError.tryEmit(ResponseError.SavePostError)
                _state.value = FeedModelState.Error(e.message)
                errorPost.tryEmit(post)
            }
        }
    }

    sealed interface ResponseError {
        object LoadPostError : ResponseError
        object SavePostError : ResponseError
        object LikePostError : ResponseError
        object DeletePostError : ResponseError
    }

    sealed class Command {
        class ShowErrorSnackbar(val message: String) : Command()
        object ShowErrorLayout : Command()
        object ShowContent : Command()
    }
}