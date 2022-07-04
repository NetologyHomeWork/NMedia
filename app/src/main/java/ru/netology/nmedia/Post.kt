package ru.netology.nmedia

data class Post(
    val id: Int = 1,
    val author: String,
    val content: String,
    val published: String,
    val authorAvatar: Int,
    var isLike: Boolean = false,
    var shareCount: Int = 0,
    var likesCount: Int = 0,
    var viewsCount: Int = 0
)
