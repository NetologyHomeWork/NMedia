package ru.netology.nmedia.database

import ru.netology.nmedia.model.Post

interface PostDao {

    fun getAll(): List<Post>

    fun save(post: Post): Post

    fun like(post: Post)

    fun removeById(postId: Long)

    fun share(post: Post)
}