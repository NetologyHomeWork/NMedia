package ru.netology.nmedia.domain.model

data class PostUIModel(
    val post: Post,
    val isError: Boolean,
    val isNew: Boolean
)