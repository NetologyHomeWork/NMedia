package ru.netology.nmedia.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.domain.model.Post

interface PostService {

    @GET("api/slow/posts")
    suspend fun getAllPosts(): Response<List<Post>>

    @GET("api/posts/{id}/newer")
    suspend fun getNewer(
        @Path("id") postId: Long
    ): Response<List<Post>>

    @POST("api/posts")
    suspend fun savePost(@Body post: Post): Response<Post>


    @GET("api/slow/posts/{id}")
    suspend fun getById(
        @Path("id") postId: Long
    ): Response<Post>

    @DELETE("api/posts/{id}")
    suspend fun removeById(
        @Path("id") postId: Long
    ): Response<Unit>

    @POST("api/posts/{id}/likes")
    suspend fun likeById(
        @Path("id") postId: Long
    ): Response<Post>

    @DELETE("api/posts/{id}/likes")
    suspend fun dislikeById(
        @Path("id") postId: Long
    ): Response<Post>
}