package ru.netology.nmedia.presentation.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okio.IOException
import ru.netology.nmedia.domain.model.FeedModel
import ru.netology.nmedia.domain.model.FeedPost
import ru.netology.nmedia.domain.model.Post
import ru.netology.nmedia.data.repository.PostRepository
import ru.netology.nmedia.data.repository.PostRepositoryImpl
import ru.netology.nmedia.data.utils.SingleLiveEvent
import kotlin.concurrent.thread

class MainViewModel(
    private val repository: PostRepository = PostRepositoryImpl()
) : ViewModel() {

    private val empty = Post(
        id = 0,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "",
        published = "",
        likesCount = 0,
        isLike = false
    )

    private val _edited = MutableLiveData(empty)

    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel> get() = _data

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> get() = _postCreated

    private val _currentPost = MutableLiveData<FeedPost>()
    val currentPost: LiveData<FeedPost> get() = _currentPost

    init {
        loadPost()
    }

    fun loadPost() {
        thread {
            _data.postValue(FeedModel(loading = true))
            try {
                val posts = repository.getData()
                FeedModel(posts = posts, empty = posts.isEmpty(), loading = false)
            } catch (e: IOException) {
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }

    fun save() {
        _edited.value?.let {
            thread {
                repository.savePost(it)
                _postCreated.postValue(Unit)
            }
        }
        _edited.value = empty
    }

    fun like(post: Post) {
        thread {
            repository.like(post)
            changeLikeState(post)
            changeDetailLike(post)
        }
    }

    fun deletePost(id: Long) {
        thread {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(
                    posts = _data.value?.posts.orEmpty().filter { it.id != id }
                )
            )
            try {
                repository.removeItem(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }

    fun fundPostById(postId: Long) {
        _currentPost.value = FeedPost(loading = true, post = empty)
        thread {
            try {
                val post = repository.findPostById(postId)
                FeedPost(post = post, loading = false)
            } catch (e: IOException) {
                FeedPost(post = empty, error = true)
            }.also(_currentPost::postValue)
        }
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
            _currentPost.value?.post?.let {
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
            }?.let {
                _currentPost.value?.copy(
                    post = it
                )
            }
        )
    }
}