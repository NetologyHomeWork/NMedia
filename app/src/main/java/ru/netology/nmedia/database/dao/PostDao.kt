package ru.netology.nmedia.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.database.PostColumns.TABLE_NAME
import ru.netology.nmedia.database.entity.PostEntity

@Dao
interface PostDao {

    @Query("select * from $TABLE_NAME order by id desc")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: PostEntity)

    @Query("update $TABLE_NAME set content = :content where id = :postId")
    fun updateContentById(postId: Long, content: String)

    fun save(post: PostEntity) {
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)
    }

    @Query("update $TABLE_NAME set is_like = :isLike, like_count = :likeCount where id = :postId")
    fun like(postId: Long, isLike: Boolean, likeCount: Int)

    @Query("delete from $TABLE_NAME where id = :postId")
    fun removeById(postId: Long)

    @Query("update $TABLE_NAME set share_count = :shareCount where id = :postId")
    fun share(postId: Long, shareCount: Int)

    @Query("select * from $TABLE_NAME where id = :postId")
    fun findPostById(postId: Long): PostEntity?
}