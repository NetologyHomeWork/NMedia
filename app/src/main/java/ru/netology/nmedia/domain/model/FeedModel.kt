package ru.netology.nmedia.domain.model

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
    val empty: Boolean = false
)

data class FeedPost(
    val post: Post,
    val loading: Boolean = false,
    val error: Boolean = false
)