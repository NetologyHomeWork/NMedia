package ru.netology.nmedia.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.data.database.PostColumns.TABLE_NAME
import ru.netology.nmedia.data.database.entity.PostEntity

@Dao
interface PostDao {

    @Query("select * from $TABLE_NAME order by id desc")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("update $TABLE_NAME set content = :content where id = :postId")
    suspend fun updateContentById(postId: Long, content: String)

    suspend fun save(post: PostEntity) {
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)
    }

    @Query("update $TABLE_NAME set is_like = :isLike, like_count = :likeCount where id = :postId")
    suspend fun like(postId: Long, isLike: Boolean, likeCount: Int)

    @Query("delete from $TABLE_NAME where id = :postId")
    suspend fun removeById(postId: Long)

    @Query("select * from $TABLE_NAME where id = :postId")
    suspend fun findPostById(postId: Long): PostEntity?
}