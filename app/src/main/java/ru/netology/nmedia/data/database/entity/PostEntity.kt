package ru.netology.nmedia.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.data.database.PostColumns.TABLE_NAME
import ru.netology.nmedia.domain.model.Attachment
import ru.netology.nmedia.domain.model.Post
import ru.netology.nmedia.domain.model.PostUIModel

@Entity(tableName = TABLE_NAME)
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    @ColumnInfo(name = "author_id") val authorId: Long,
    val content: String,
    val published: String,
    @ColumnInfo(name = "is_like") val isLike: Boolean,
    @ColumnInfo(name = "like_count") val likesCount: Int,
    @ColumnInfo(name = "author_avatar") val authorAvatar: String,
    @ColumnInfo(name = "is_error") val isError: Boolean,
    @ColumnInfo(name = "is_new") val isNew: Boolean,
    @Embedded val attachment: Attachment?
)

fun PostEntity.toPost(): Post = Post(
    id = this.id,
    author = this.author,
    authorId = authorId,
    published = this.published,
    content = this.content,
    isLike = this.isLike,
    likesCount = this.likesCount,
    authorAvatar = this.authorAvatar,
    attachment = this.attachment
)

fun PostEntity.toPostUIModel(): PostUIModel = PostUIModel(
    post = this.toPost(),
    isError = this.isError,
    isNew = isNew
)

fun List<PostEntity>.toPostList(): List<Post> {
    return this.map { it.toPost() }
}

fun List<PostEntity>.toPostUIList(): List<PostUIModel> {
    return this.map { it.toPostUIModel() }
}