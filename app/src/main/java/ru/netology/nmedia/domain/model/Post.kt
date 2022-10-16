package ru.netology.nmedia.domain.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: Long = 0,
    val author: String = "",
    val content: String = "",
    val published: String = "",
    val authorAvatar: String = "",
    @Json(name = "likedByMe")
    val isLike: Boolean = false,
    @Json(name = "likes")
    val likesCount: Int = 0,
    val attachment: Attachment? = null,
) : Parcelable

@Parcelize
data class Attachment(
    val description: String = "",
    val type: String = "",
    val url: String = ""
) : Parcelable
