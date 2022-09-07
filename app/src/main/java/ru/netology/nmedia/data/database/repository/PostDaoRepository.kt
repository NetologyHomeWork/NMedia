package ru.netology.nmedia.data.database.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.data.database.entity.PostEntity

interface PostDaoRepository {

    val allPostEntity: LiveData<List<PostEntity>>

    fun save(postEntity: PostEntity)

    fun like(postEntity: PostEntity)

    fun delete(postId: Long)

    fun findPostById(postId: Long): PostEntity?
}