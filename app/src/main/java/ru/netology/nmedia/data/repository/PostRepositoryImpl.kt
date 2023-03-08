package ru.netology.nmedia.data.repository

import android.content.Intent
import android.net.Uri
import androidx.paging.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import ru.netology.nmedia.R
import ru.netology.nmedia.data.AppException
import ru.netology.nmedia.data.database.dao.PostDao
import ru.netology.nmedia.data.database.dao.PostRemoteKeyDao
import ru.netology.nmedia.data.database.db.PostDatabase
import ru.netology.nmedia.data.database.entity.PostEntity
import ru.netology.nmedia.data.database.entity.toPost
import ru.netology.nmedia.data.network.PostService
import ru.netology.nmedia.data.utils.ResourceManager
import ru.netology.nmedia.data.utils.parsingUrlLink
import ru.netology.nmedia.data.utils.wrapException
import ru.netology.nmedia.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postService: PostService,
    private val postDao: PostDao,
    private val resourceManager: ResourceManager,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: PostDatabase
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { postDao.getPagingSource() },
        remoteMediator = PostRemoteMediator(apiService = postService, postDao = postDao, postRemoteKeyDao = postRemoteKeyDao, appDb = appDb)
    ).flow
        .map { posts -> posts.map(PostEntity::toPost) }

    override suspend fun getDataAsync(): List<Post> = wrapException(resourceManager) {
        val response = postService.getAllPosts()
        if (response.isSuccessful.not() || response.body() == null) {
            throw AppException.ApiError(response.code(), response.message())
        }
        postDao.insert(response.body()!!.toPostEntityList(false))
        return@wrapException response.body()!!
    }

    override suspend fun loadNew() {
        postDao.updateVisibility()
    }

    override fun getNewerCount(postId: Long): Flow<Int> = callbackFlow {
        while (true) {
            try {
                delay(15_000L)
                val response = postService.getNewer(postId)
                if (response.isSuccessful.not()) {
                    throw AppException.ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw AppException.ApiError(
                    response.code(),
                    response.message()
                )
                postDao.insert(body.toPostEntityList(true))
                trySend(body.size)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
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
    }

    override suspend fun savePostAsync(post: Post, model: PhotoModel?) {
        try {
            val file = model?.let { uploadFile(it) }
            val response = file?.let {
                postService.savePost(post.copy(attachment = Attachment(url = it.id, type = AttachmentType.IMAGE, description = "")))
            } ?: postService.savePost(post)
            if (response.isSuccessful.not()) {
                throw AppException.ApiError(response.code(), response.message())
            }
            postDao.insert(post.toPostEntity())
        } catch (e: Exception) {
            e.printStackTrace()
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

    private suspend fun uploadFile(model: PhotoModel): Media = wrapException(resourceManager) {
        val part = MultipartBody.Part.createFormData(
            name = "file",
            filename = model.file?.name,
            body = model.file!!.asRequestBody()
        )

        val response = postService.uploadPhoto(part)
        if (response.isSuccessful.not() || response.body() == null) throw AppException.ApiError(
            response.code(),
            response.message()
        )
        return response.body() ?: throw AppException.ApiError(response.code(), response.message())
    }

    override suspend fun removeItemAsync(id: Long) = wrapException(resourceManager) {
        val response = postService.removeById(id)
        if (response.isSuccessful.not()) throw AppException.ApiError(
            response.code(),
            response.message()
        )
        postDao.removeById(id)
    }

    override suspend fun likeAsync(post: Post) = wrapException(resourceManager) {
        val response = if (post.isLike.not()) {
            postService.likeById(post.id)
        } else {
            postService.dislikeById(post.id)
        }

        if (response.isSuccessful.not()) throw AppException.ApiError(
            response.code(),
            response.message()
        )

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
