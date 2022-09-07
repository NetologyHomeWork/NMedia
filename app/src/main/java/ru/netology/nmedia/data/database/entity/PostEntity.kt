package ru.netology.nmedia.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.data.database.PostColumns.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 1L,
    val author: String,
    val content: String,
    val published: String,
    @ColumnInfo(name = "is_like")
    val isLike: Boolean,
    @ColumnInfo(name = "like_count")
    val likesCount: Int = 0
)