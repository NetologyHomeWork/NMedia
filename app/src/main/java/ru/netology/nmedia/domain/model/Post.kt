package ru.netology.nmedia.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: Long = 0,
    val author: String = "",
    val content: String = "",
    val published: String = "",
    val authorAvatar: String = "",
    @SerializedName("likedByMe")
    val isLike: Boolean = false,
    @SerializedName("likes")
    val likesCount: Int = 0,
    val attachment: Attachment? = null,
) : Parcelable

@Parcelize
data class Attachment(
    val description: String = "",
    val type: String = "",
    val url: String = ""
) : Parcelable
