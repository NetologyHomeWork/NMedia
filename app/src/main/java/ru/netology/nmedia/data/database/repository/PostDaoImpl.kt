package ru.netology.nmedia.data.database.repository

import android.app.Application
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.data.database.db.PostDatabase
import ru.netology.nmedia.data.database.entity.PostEntity

class PostDaoImpl(
    application: Application
) : PostDaoRepository {

    private val postDao = PostDatabase.getInstance(application).postDao()

    override val allPostEntity: Flow<List<PostEntity>>
        get() = postDao.getAll()

    override suspend fun save(postEntity: PostEntity) {
        postDao.save(postEntity)
    }

    override suspend fun like(postEntity: PostEntity) {
        postDao.like(postEntity.id, postEntity.isLike, postEntity.likesCount)
    }

    override suspend fun delete(postId: Long) {
        postDao.removeById(postId)
    }

    override suspend fun findPostById(postId: Long): PostEntity? {
        return postDao.findPostById(postId)
    }
}