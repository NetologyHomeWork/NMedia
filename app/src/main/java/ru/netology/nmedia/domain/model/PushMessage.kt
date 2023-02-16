package ru.netology.nmedia.domain.model

data class PushMessage(
    val recipientId: Long?,
    val content: String
)
