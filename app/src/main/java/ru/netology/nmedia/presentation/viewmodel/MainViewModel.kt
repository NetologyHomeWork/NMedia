package ru.netology.nmedia.presentation.viewmodel

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.data.AppException
import ru.netology.nmedia.data.auth.AppAuth
import ru.netology.nmedia.data.repository.PostRepository
import ru.netology.nmedia.domain.model.FeedModel
import ru.netology.nmedia.domain.model.FeedModelState
import ru.netology.nmedia.domain.model.PhotoModel
import ru.netology.nmedia.domain.model.Post
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: PostRepository
) : ViewModel() {

    private val _commands = MutableSharedFlow<Command>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val commands = _commands.asSharedFlow()

    private val responseError = MutableStateFlow<ResponseError?>(null)
    private val errorPost = MutableStateFlow<Post?>(null)

    private val _blockContent = MutableStateFlow(false)
    val blockContent = _blockContent.asStateFlow()

    val data = AppAuth.getInstance().state
        .map { it?.id }
        .flatMapLatest { id ->
            repository.data
                .map { posts ->
                    FeedModel(
                        posts.map { it.copy(ownedByMe = it.post.authorId == id) },
                        posts.isEmpty()
                    )
                }
        }

    val newerCount = data.flatMapLatest {
        repository.getNewerCount(it.posts.firstOrNull()?.post?.id ?: 0L)
    }.catch { throwable ->
        val e = throwable as? AppException ?: return@catch
        _commands.tryEmit(Command.ShowErrorSnackbar(e.message))
    }

    private val _state = MutableLiveData<FeedModelState>(FeedModelState.Idle)
    val state: LiveData<FeedModelState> get() = _state

    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?> get() = _photo

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
                _blockContent.tryEmit(true)
                _photo.value?.let { photo ->
                    repository.savePostAsync(post, photo)
                } ?: run {
                    repository.savePostAsync(post)
                }
            } catch (e: AppException) {
                responseError.tryEmit(ResponseError.SavePostError)
                errorPost.tryEmit(post)
                _state.value = FeedModelState.Error(e.message)
            } finally {
                _commands.tryEmit(Command.SavePost)
                _blockContent.tryEmit(false)
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

    fun loadNew() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadNew()
            _commands.tryEmit(Command.Scroll)
        }
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = if (uri != null && file != null) {
            PhotoModel(uri, file)
        } else null
    }

    sealed interface ResponseError {
        object LoadPostError : ResponseError
        object SavePostError : ResponseError
        object LikePostError : ResponseError
        object DeletePostError : ResponseError
    }

    sealed interface Command {
        class ShowErrorSnackbar(val message: String) : Command
        object ShowErrorLayout : Command
        object Scroll : Command
        object SavePost : Command
    }
}