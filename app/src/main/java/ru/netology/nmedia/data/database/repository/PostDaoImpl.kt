package ru.netology.nmedia.data.database.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.data.database.dao.PostDao
import ru.netology.nmedia.data.database.entity.PostEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostDaoImpl @Inject constructor(
    private val postDao: PostDao
) : PostDaoRepository {

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