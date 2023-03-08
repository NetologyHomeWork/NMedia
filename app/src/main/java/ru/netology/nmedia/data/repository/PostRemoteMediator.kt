package ru.netology.nmedia.data.repository

import androidx.paging.*
import androidx.room.withTransaction
import okio.IOException
import ru.netology.nmedia.data.AppException
import ru.netology.nmedia.data.database.dao.PostDao
import ru.netology.nmedia.data.database.dao.PostRemoteKeyDao
import ru.netology.nmedia.data.database.db.PostDatabase
import ru.netology.nmedia.data.database.entity.PostEntity
import ru.netology.nmedia.data.database.entity.PostRemoteKeyEntity
import ru.netology.nmedia.data.network.PostService
import ru.netology.nmedia.domain.model.Post
import ru.netology.nmedia.domain.model.toPostEntity

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: PostService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: PostDatabase
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val result = when (loadType) {
                LoadType.REFRESH -> {
                    postRemoteKeyDao.max()?.let { key ->
                        apiService.getAfter(key, state.config.pageSize)
                    } ?: apiService.getLatestPosts(state.config.initialLoadSize)
                }
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getBefore(id, state.config.pageSize)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            if (result.isSuccessful.not()) {
                throw AppException.ApiError(code = result.code(), message = result.message())
            }

            val body = result.body() ?: throw AppException.ApiError(
                code = result.code(),
                message = result.message()
            )

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        if (postRemoteKeyDao.isEmpty()) {
                            postRemoteKeyDao.insert(
                                listOf(
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.AFTER,
                                        key = body.first().id
                                    ),
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.BEFORE,
                                        key = body.last().id
                                    )
                                )
                            )
                        } else {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.AFTER,
                                    key = body.first().id
                                )
                            )
                        }
                    }
                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.BEFORE,
                                key = body.last().id
                            )
                        )
                    }
                    else -> { /* no-op */ }
                }
                postDao.insert(body.map(Post::toPostEntity))
            }

            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}