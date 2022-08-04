package ru.netology.nmedia.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.database.PostColumns.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 1L,
    val author: String,
    val content: String,
    val published: String,
    @ColumnInfo(name = "author_avatar")
    val authorAvatar: Int,
    @ColumnInfo(name = "is_like")
    val isLike: Boolean,
    @ColumnInfo(name = "share_count")
    val shareCount: Int,
    @ColumnInfo(name = "like_count")
    val likesCount: Int = 0,
    @ColumnInfo(name = "views_count")
    val viewsCount: Int = 0
)