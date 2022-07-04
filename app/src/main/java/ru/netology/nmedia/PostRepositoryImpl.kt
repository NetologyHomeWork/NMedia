package ru.netology.nmedia

import androidx.lifecycle.MutableLiveData

class PostRepositoryImpl : PostRepository {

    private val postList = sortedSetOf<Post>({o1, o2 -> o2.id.compareTo(o1.id)})

    init {
        postList.add(Post(
            author = R.string.post_author,
            published = R.string.post_published,
            content = R.string.post_content,
            authorAvatar = R.drawable.ic_netology,
            shareCount = 5,
            likesCount = 10,
            viewsCount = 50
        ))
        postList.add(Post(
            id = 2,
            author = R.string.post_author,
            published = R.string.post2_published,
            content = R.string.post2_content,
            authorAvatar = R.drawable.ic_netology,
            shareCount = 3,
            likesCount = 15,
            viewsCount = 75
        ))
    }

    private val data = MutableLiveData(postList.toList())

    override fun getData() = data

    override fun like(post: Post) {
        val newPost = post.copy(
            likesCount = if (!post.isLike) post.likesCount + 1 else post.likesCount - 1,
            isLike = !post.isLike
        )
        postList.remove(post)
        postList.add(newPost)
    }


    override fun share(post: Post) {
        val newPost = post.copy(
            shareCount = post.shareCount + 1
        )
        postList.remove(post)
        postList.add(newPost)
    }
}