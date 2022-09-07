package ru.netology.nmedia.presentation.rvadapter

import ru.netology.nmedia.domain.model.Post

interface AdapterListener {
    fun onClickLike(post: Post)

    fun onClickShare(post: Post)

    fun onClickDelete(post: Post)

    fun onClickEdit(post: Post)

    fun onClickUrlVideo(post: Post)

    fun onClickPost(post: Post)
}