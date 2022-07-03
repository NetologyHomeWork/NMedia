package ru.netology.nmedia

import androidx.annotation.StringRes

data class Post(
    val id: Int = 1,
    @StringRes val author: Int,
    @StringRes val content: Int,
    @StringRes val published: Int,
    val authorAvatar: Int,
    val isLike: Boolean = false,
    val shareCount: Int = 0,
    val likesCount: Int = 0,
    val viewsCount: Int = 0
)
