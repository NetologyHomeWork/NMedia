package ru.netology.nmedia.data.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.domain.model.Media
import ru.netology.nmedia.domain.model.Post

interface PostService {

    @GET("api/slow/posts")
    suspend fun getAllPosts(): Response<List<Post>>

    @GET("api/slow/posts/latest")
    suspend fun getLatestPosts(
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("api/posts/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("api/posts/{id}/after")
    suspend fun getAfter(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

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

    @Multipart
    @POST("api/slow/media")
    suspend fun uploadPhoto(
        @Part part: MultipartBody.Part
    ): Response<Media>
}