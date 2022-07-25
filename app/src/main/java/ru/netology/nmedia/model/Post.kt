package ru.netology.nmedia.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.netology.nmedia.R

@Parcelize
data class Post(
    val id: Long = 1L,
    val author: String,
    val content: String,
    val published: String,
    val authorAvatar: Int = R.drawable.ic_netology,
    val isLike: Boolean = false,
    val shareCount: Int = 0,
    val likesCount: Int = 0,
    val viewsCount: Int = 0
) : Parcelable
