package ru.netology.nmedia.data.database.repository

import android.app.Application
import androidx.lifecycle.LiveData
import ru.netology.nmedia.data.database.db.PostDatabase
import ru.netology.nmedia.data.database.entity.PostEntity

class PostDaoImpl(
    application: Application
) : PostDaoRepository {

    private val postDao = PostDatabase.getInstance(application).postDao()

    override val allPostEntity: LiveData<List<PostEntity>>
        get() = postDao.getAll()

    override fun save(postEntity: PostEntity) {
        postDao.save(postEntity)
    }

    override fun like(postEntity: PostEntity) {
        postDao.like(postEntity.id, postEntity.isLike, postEntity.likesCount)
    }

    override fun delete(postId: Long) {
        postDao.removeById(postId)
    }

    override fun findPostById(postId: Long): PostEntity? {
        return postDao.findPostById(postId)
    }
}