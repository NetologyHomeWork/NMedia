package ru.netology.nmedia.data.repository

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import okio.IOException
import ru.netology.nmedia.R
import ru.netology.nmedia.data.AppException
import ru.netology.nmedia.data.database.dao.PostDao
import ru.netology.nmedia.data.database.entity.PostEntity
import ru.netology.nmedia.data.database.entity.toPostUIList
import ru.netology.nmedia.data.network.PostService
import ru.netology.nmedia.data.utils.ResourceManager
import ru.netology.nmedia.data.utils.parsingUrlLink
import ru.netology.nmedia.data.utils.wrapException
import ru.netology.nmedia.domain.model.Post
import ru.netology.nmedia.domain.model.PostUIModel
import ru.netology.nmedia.domain.model.toPostEntity
import ru.netology.nmedia.domain.model.toPostEntityList

class PostRepositoryImpl(
    private val postService: PostService,
    private val postDao: PostDao,
    private val resourceManager: ResourceManager
) : PostRepository {

    override val data: LiveData<List<PostUIModel>> =
        postDao.getAll().map(List<PostEntity>::toPostUIList)

    override suspend fun getDataAsync(): List<Post> = wrapException(resourceManager) {
        val response = postService.getAllPosts()
        if (response.isSuccessful.not() || response.body() == null) {
            throw AppException.ApiError(response.code(), response.message())
        }
        postDao.insert(response.body()!!.toPostEntityList())
        return@wrapException response.body()!!
    }
    override suspend fun savePostAsync(post: Post) {
        try {
            val response = postService.savePost(post)
            if (response.isSuccessful.not()) {
                throw AppException.ApiError(response.code(), response.message())
            }
            postDao.insert(post.toPostEntity())
        } catch (e: Exception) {
            postDao.insert(post.toPostEntity().copy(isError = true))
            when (e) {
                is AppException.ApiError -> throw e
                is IOException -> {
                    throw AppException.NetworkError(resourceManager.getString(R.string.error_connection))
                }

                else -> {
                    throw AppException.UnknownError(resourceManager.getString(R.string.unknown_error))
                }
            }
        }
    }

    override suspend fun removeItemAsync(id: Long) = wrapException(resourceManager) {
        val response = postService.removeById(id)
        if (response.isSuccessful.not()) throw AppException.ApiError(response.code(), response.message())
        postDao.removeById(id)
    }

    override suspend fun likeAsync(post: Post) = wrapException(resourceManager) {
        val response = if (post.isLike.not()) {
            postService.likeById(post.id)
        } else {
            postService.dislikeById(post.id)
        }

        if (response.isSuccessful.not()) throw AppException.ApiError(response.code(), response.message())

        val newPost = post.copy(
            likesCount = if (post.isLike.not()) post.likesCount + 1 else post.likesCount - 1,
            isLike = post.isLike.not()
        )

        postDao.like(
            postId = newPost.id,
            isLike = newPost.isLike,
            likeCount = newPost.likesCount
        )
    }

    override suspend fun getPostById(postId: Long): Post = wrapException(resourceManager) {
        val response = postService.getById(postId)
        if (response.isSuccessful.not() || response.body() == null) {
            throw AppException.ApiError(response.code(), response.message())
        }
        return@wrapException response.body()!!
    }


    override fun share(post: Post): Intent {
        return sendIntent(post)
    }

    override fun launchYoutubeVideo(post: Post): Intent {
        val link = parsingUrlLink(post.content)
        return Intent(Intent.ACTION_VIEW, Uri.parse(link))
    }

    override suspend fun removeFromDatabase(postId: Long) {
        postDao.removeById(postId)
    }

    private fun sendIntent(post: Post): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post.content)
            type = "text/plain"
        }
    }
}
