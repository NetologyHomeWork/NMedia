package ru.netology.nmedia.data.repository

import android.content.Intent
import android.net.Uri
import ru.netology.nmedia.data.network.PostService
import ru.netology.nmedia.data.utils.enqueue
import ru.netology.nmedia.data.utils.parsingUrlLink
import ru.netology.nmedia.domain.model.Post

class PostRepositoryImpl(
    private val postService: PostService
) : PostRepository {

    override fun getDataAsync(callback: PostRepository.PostCallback<List<Post>>) {
        postService.getAllPosts().enqueue(callback)
    }

    override fun savePostAsync(post: Post, callback: PostRepository.PostCallback<Post>) {
        postService.savePost(post).enqueue(callback)
    }

    override fun removeItemAsync(id: Long, callback: PostRepository.PostCallback<Unit>) {
        postService.removeById(id).enqueue(callback)
    }

    override fun likeAsync(post: Post, callback: PostRepository.PostCallback<Post>) {
        if (post.isLike) {
            postService.likeById(post.id).enqueue(callback)
        } else {
            postService.dislikeById(post.id).enqueue(callback)
        }
    }


    override fun share(post: Post): Intent {
        return sendIntent(post)
    }

    override fun launchYoutubeVideo(post: Post): Intent {
        val link = parsingUrlLink(post.content)
        return Intent(Intent.ACTION_VIEW, Uri.parse(link))
    }

    private fun sendIntent(post: Post): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post.content)
            type = "text/plain"
        }
    }
}
