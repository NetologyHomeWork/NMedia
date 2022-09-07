package ru.netology.nmedia.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    @SerializedName("likedByMe")
    val isLike: Boolean = false,
    @SerializedName("likes")
    val likesCount: Int,
) : Parcelable
