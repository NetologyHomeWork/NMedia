package ru.netology.nmedia.domain.model

data class FeedModel(
    val posts: List<PostUIModel> = emptyList(),
    val empty: Boolean = false
)

sealed interface FeedModelState {
    object Loading : FeedModelState
    object Refreshing: FeedModelState
    class Error(val message: String) : FeedModelState
    object Idle : FeedModelState
}