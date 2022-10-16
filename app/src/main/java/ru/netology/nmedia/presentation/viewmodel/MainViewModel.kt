package ru.netology.nmedia.presentation.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import ru.netology.nmedia.data.repository.PostRepository
import ru.netology.nmedia.data.utils.SingleLiveEvent
import ru.netology.nmedia.domain.model.FeedModel
import ru.netology.nmedia.domain.model.Post

class MainViewModel(
    private val repository: PostRepository
) : ViewModel() {

    private val empty = Post(
        id = 0,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "",
        published = "",
        likesCount = 0,
        isLike = false
    )

    val commands = MutableStateFlow<Command>(Command.ShowContent)

    private val _edited = MutableLiveData(empty)

    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel> get() = _data

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> get() = _postCreated

    private val _currentPost = MutableLiveData<Post>()
    val currentPost: LiveData<Post> get() = _currentPost

    fun loadPost() {
        _data.postValue(FeedModel(loading = true))
        repository.getDataAsync(object : PostRepository.PostCallback<List<Post>> {
            override fun onSuccess(value: List<Post>) {
                _data.postValue(FeedModel(posts = value, empty = value.isEmpty()))
                commands.tryEmit(Command.ShowContent)
            }

            override fun onFailure(t: Throwable) {
                _data.postValue(FeedModel(error = true))
                commands.tryEmit(Command.ShowErrorLayout)
            }
        })
    }

    fun save() {
        _edited.value?.let {
            repository.savePostAsync(it, object : PostRepository.PostCallback<Post> {
                override fun onSuccess(value: Post) {
                    _postCreated.postValue(Unit)
                    commands.tryEmit(Command.ShowContent)
                }

                override fun onFailure(t: Throwable) {
                    commands.tryEmit(Command.ShowErrorSnackbar)
                }
            })
        }
        _edited.value = empty
    }

    fun like(post: Post) {
        repository.likeAsync(post, object : PostRepository.PostCallback<Post> {
            override fun onSuccess(value: Post) {
                changeLikeState(post)
                changeDetailLike(post)
                commands.tryEmit(Command.ShowContent)
            }

            override fun onFailure(t: Throwable) {
                _data.postValue(FeedModel(error = true))
                commands.tryEmit(Command.ShowErrorSnackbar)
            }
        })
    }

    fun deletePost(id: Long) {
        val old = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(
                posts = _data.value?.posts.orEmpty().filter { it.id != id }
            )
        )
        repository.removeItemAsync(id, object : PostRepository.PostCallback<Unit> {
            override fun onSuccess(value: Unit) {
                _postCreated.postValue(Unit)
                commands.tryEmit(Command.ShowContent)
            }

            override fun onFailure(t: Throwable) {
                _data.postValue(_data.value?.copy(posts = old))
                commands.tryEmit(Command.ShowErrorSnackbar)
            }
        })
    }

    fun share(post: Post): Intent = repository.share(post)

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

    fun editingClear() {
        _edited.value = empty
    }

    fun getCurrentPost(post: Post) {
        _currentPost.value = post
    }

    private fun changeLikeState(post: Post) {
        _data.postValue(
            _data.value?.copy(
                posts = _data.value?.posts.orEmpty().map {
                    if (it.id == post.id) {
                        if (post.isLike) {
                            it.copy(
                                likesCount = it.likesCount - 1,
                                isLike = post.isLike.not()
                            )
                        } else {
                            it.copy(
                                likesCount = it.likesCount + 1,
                                isLike = post.isLike.not()
                            )
                        }
                    } else it
                }
            )
        )
    }

    private fun changeDetailLike(post: Post) {
        _currentPost.postValue(
            _currentPost.value?.let {
                if (it.id == post.id) {
                    if (post.isLike) {
                        it.copy(
                            likesCount = it.likesCount - 1,
                            isLike = post.isLike.not()
                        )
                    } else {
                        it.copy(
                            likesCount = it.likesCount + 1,
                            isLike = post.isLike.not()
                        )
                    }
                } else it
            }
        )
    }

    sealed class Command {
        object ShowErrorSnackbar : Command()
        object ShowErrorLayout : Command()
        object ShowContent : Command()
    }
}