package ru.netology.nmedia.data.database.dao

import androidx.room.*
import ru.netology.nmedia.data.database.PostColumns.POST_KEY_TABLE
import ru.netology.nmedia.data.database.entity.PostRemoteKeyEntity

@Dao
interface PostRemoteKeyDao {

    @Query("SELECT max(`key`) FROM $POST_KEY_TABLE")
    suspend fun max(): Long?

    @Query("SELECT min(`key`) FROM $POST_KEY_TABLE")
    suspend fun min(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postRemoteKey: PostRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postRemoteKey: List<PostRemoteKeyEntity>)

    @Query("DELETE FROM $POST_KEY_TABLE")
    suspend fun clear()

    @Query("SELECT COUNT(*) == 0 FROM $POST_KEY_TABLE")
    suspend fun isEmpty(): Boolean
}