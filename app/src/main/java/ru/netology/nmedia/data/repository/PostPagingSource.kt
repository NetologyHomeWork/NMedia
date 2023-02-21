package ru.netology.nmedia.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import okio.IOException
import ru.netology.nmedia.data.AppException
import ru.netology.nmedia.data.network.PostService
import ru.netology.nmedia.domain.model.Post

class PostPagingSource(
    private val apiService: PostService
) : PagingSource<Long, Post>() {

    override fun getRefreshKey(state: PagingState<Long, Post>): Long? = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        try {
            val result = when (params) {
                is LoadParams.Refresh -> {
                    apiService.getLatestPosts(params.loadSize)
                }
                is LoadParams.Append -> {
                    apiService.getBefore(id = params.key, count = params.loadSize)
                }
                is LoadParams.Prepend -> {
                    return LoadResult.Page(
                        data = emptyList(),
                        nextKey = null,
                        prevKey = params.key
                    )
                }
            }

            if (result.isSuccessful.not()) {
                throw AppException.ApiError(result.code(), result.message())
            }

            val data = result.body().orEmpty()
            val nextKey = if (data.isEmpty()) null else data.last().id
            return LoadResult.Page(
                data = data,
                prevKey = params.key,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        }
    }
}