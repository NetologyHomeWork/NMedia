package ru.netology.nmedia.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.data.database.PostColumns

@Entity(tableName = PostColumns.POST_KEY_TABLE)
data class PostRemoteKeyEntity(
    @PrimaryKey val type: KeyType,
    val key: Long
) {
    enum class KeyType {
        AFTER,
        BEFORE
    }
}