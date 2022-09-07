package ru.netology.nmedia.data.database.mapper

import ru.netology.nmedia.data.database.entity.PostEntity
import ru.netology.nmedia.domain.model.Post

class DbObjectMapper {

    fun mapDTOtoEntity(post: Post): PostEntity {
        return PostEntity(
            id = post.id,
            author = post.author,
            content = post.content,
            published = post.published,
            isLike = post.isLike,
            likesCount = post.likesCount,
        )
    }

    fun mapEntityToDTO(postEntity: PostEntity): Post {
        return Post(
            id = postEntity.id,
            author = postEntity.author,
            content = postEntity.content,
            published = postEntity.published,
            isLike = postEntity.isLike,
            likesCount = postEntity.likesCount,
        )
    }

    fun mapListEntityToListDTO(listEntity: List<PostEntity>) =
        listEntity.asSequence().map { mapEntityToDTO(it) }.toList()
}