package ru.netology.nmedia.services

enum class Action {
    LIKE, POST
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String
)

data class NewPostNotification(
    val postId: Long,
    val postAuthor: String,
    val postContent: String
)