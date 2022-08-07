package ru.netology.nmedia.database.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.database.entity.PostEntity

interface PostDaoRepository {

    val allPostEntity: LiveData<List<PostEntity>>

    fun save(postEntity: PostEntity)

    fun like(postEntity: PostEntity)

    fun delete(postId: Long)

    fun share(postEntity: PostEntity)

    fun findPostById(postId: Long): PostEntity?
}