package ru.netology.nmedia.presentation.rvadapter

import ru.netology.nmedia.domain.model.PostUIModel

interface AdapterListener {
    fun onClickLike(post: PostUIModel)

    fun onClickShare(post: PostUIModel)

    fun onClickDelete(post: PostUIModel)

    fun onClickEdit(post: PostUIModel)

    fun onClickUrlVideo(post: PostUIModel)

    fun onClickPost(post: PostUIModel)

    fun onClickRetry(post: PostUIModel)

    fun onPhotoClick(post: PostUIModel)
}