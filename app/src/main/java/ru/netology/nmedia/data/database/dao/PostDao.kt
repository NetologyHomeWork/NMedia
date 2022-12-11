package ru.netology.nmedia.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.data.database.PostColumns.TABLE_NAME
import ru.netology.nmedia.data.database.entity.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE is_new = 0 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE $TABLE_NAME SET is_new = 0 WHERE is_new = 1")
    suspend fun updateVisibility()

    @Query("UPDATE $TABLE_NAME SET content = :content WHERE id = :postId")
    suspend fun updateContentById(postId: Long, content: String)

    suspend fun save(post: PostEntity) {
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)
    }

    @Query("UPDATE $TABLE_NAME SET is_like = :isLike, like_count = :likeCount WHERE id = :postId")
    suspend fun like(postId: Long, isLike: Boolean, likeCount: Int)

    @Query("DELETE FROM $TABLE_NAME WHERE id = :postId")
    suspend fun removeById(postId: Long)

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :postId")
    suspend fun findPostById(postId: Long): PostEntity?
}