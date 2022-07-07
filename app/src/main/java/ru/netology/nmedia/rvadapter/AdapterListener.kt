package ru.netology.nmedia.rvadapter

import ru.netology.nmedia.model.Post

interface AdapterListener {
    fun onClickLike(post: Post)
    fun onClickShare(post: Post)
    fun onClickDelete(post: Post)
    fun onClickEdit(post: Post)
}