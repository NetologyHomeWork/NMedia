package ru.netology.nmedia.domain.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import ru.netology.nmedia.data.database.entity.PostEntity

@Parcelize
data class Post(
    val id: Long = 0,
    val author: String = "",
    val content: String = "",
    val published: String = "",
    val authorAvatar: String = "",
    @Json(name = "likedByMe") val isLike: Boolean = false,
    @Json(name = "likes") val likesCount: Int = 0,
    val attachment: Attachment? = null,
) : Parcelable

@Parcelize
data class Attachment(
    val description: String = "",
    val type: String = "",
    val url: String = ""
) : Parcelable

fun Post.toPostEntity(isNew: Boolean = false): PostEntity = PostEntity(
    id = this.id,
    author = this.author,
    content = this.content,
    published = this.published,
    isLike = this.isLike,
    likesCount = this.likesCount,
    authorAvatar = this.authorAvatar,
    isError = false,
    isNew = isNew
    /*description = this.attachment?.description,
    type = this.attachment?.type,
    url = this.attachment?.url*/
)

fun List<Post>.toPostEntityList(isNew: Boolean = false): List<PostEntity> {
    return this.map { it.toPostEntity(isNew) }
}
