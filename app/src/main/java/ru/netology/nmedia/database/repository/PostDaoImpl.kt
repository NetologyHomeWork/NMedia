package ru.netology.nmedia.database.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import ru.netology.nmedia.database.dao.PostDao
import ru.netology.nmedia.database.db.PostDatabase
import ru.netology.nmedia.database.entity.PostEntity

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

    override fun share(postEntity: PostEntity) {
        postDao.share(postEntity.id, postEntity.shareCount)
    }

    override fun findPostById(postId: Long): PostEntity? {
        return postDao.findPostById(postId)
    }
}