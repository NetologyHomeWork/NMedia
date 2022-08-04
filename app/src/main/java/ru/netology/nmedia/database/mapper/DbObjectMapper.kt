package ru.netology.nmedia.database.mapper

import ru.netology.nmedia.database.entity.PostEntity
import ru.netology.nmedia.model.Post

class DbObjectMapper {

    fun mapDTOtoEntity(post: Post): PostEntity {
        return PostEntity(
            id = post.id,
            author = post.author,
            content = post.content,
            published = post.published,
            authorAvatar = post.authorAvatar,
            isLike = post.isLike,
            shareCount = post.shareCount,
            likesCount = post.likesCount,
            viewsCount = post.viewsCount
        )
    }

    fun mapEntityToDTO(postEntity: PostEntity): Post {
        return Post(
            id = postEntity.id,
            author = postEntity.author,
            content = postEntity.content,
            published = postEntity.published,
            authorAvatar = postEntity.authorAvatar,
            isLike = postEntity.isLike,
            shareCount = postEntity.shareCount,
            likesCount = postEntity.likesCount,
            viewsCount = postEntity.viewsCount
        )
    }

    fun mapListEntityToListDTO(listEntity: List<PostEntity>) =
        listEntity.asSequence().map { mapEntityToDTO(it) }.toList()
}