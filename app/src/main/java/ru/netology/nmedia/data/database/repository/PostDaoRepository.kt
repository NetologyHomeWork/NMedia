package ru.netology.nmedia.data.database.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.data.database.entity.PostEntity

interface PostDaoRepository {

    val allPostEntity: Flow<List<PostEntity>>

    suspend fun save(postEntity: PostEntity)

    suspend fun like(postEntity: PostEntity)

    suspend fun delete(postId: Long)

    suspend fun findPostById(postId: Long): PostEntity?
}