package ru.netology.nmedia

import androidx.lifecycle.MutableLiveData

class PostRepositoryImpl : PostRepository {

    private val post = Post(
        author = R.string.post_author,
        published = R.string.post_published,
        content = R.string.post_content,
        authorAvatar = R.drawable.ic_netology,
        shareCount = 5,
        likesCount = 10,
        viewsCount = 50
    )

    private val data = MutableLiveData(post)

    override fun getData() = data

    override fun like() {
        data.value = data.value?.let {
            it.copy(
                likesCount = if (!it.isLike) it.likesCount + 1 else it.likesCount - 1,
                isLike = !it.isLike
            )
        }
    }

    override fun share() {
        data.value = data.value?.let {
            it.copy(
                shareCount = it.shareCount + 1
            )
        }
    }
}