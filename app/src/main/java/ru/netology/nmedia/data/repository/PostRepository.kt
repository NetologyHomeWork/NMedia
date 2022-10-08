package ru.netology.nmedia.data.repository

import android.content.Intent
import ru.netology.nmedia.domain.model.Post

interface PostRepository {
    fun getDataAsync(callback: PostCallback<List<Post>>)

    fun likeAsync(post: Post, callback: PostCallback<Post>)

    fun share(post: Post): Intent

    fun removeItemAsync(id: Long, callback: PostCallback<Long>)

    fun savePostAsync(post: Post, callback: PostCallback<Post>)

    fun launchYoutubeVideo(post: Post): Intent

    interface PostCallback<T> {
        fun onSuccess(value: T)

        fun onFailure(e: Exception)
    }
}