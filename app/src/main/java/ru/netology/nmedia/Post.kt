package ru.netology.nmedia

data class Post(
    val id: Long = 1L,
    val author: String,
    val content: String,
    val published: String,
    val authorAvatar: Int,
    val isLike: Boolean = false,
    val shareCount: Int = 0,
    val likesCount: Int = 0,
    val viewsCount: Int = 0
)
