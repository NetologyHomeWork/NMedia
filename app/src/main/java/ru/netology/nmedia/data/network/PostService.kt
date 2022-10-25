package ru.netology.nmedia.data.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.domain.model.Post

interface PostService {

    @GET("api/slow/posts")
    fun getAllPosts(): Call<List<Post>>

    @POST("api/posts")
    fun savePost(
        @Body post: Post
    ): Call<Post>


    @GET("api/slow/posts/{id}")
    fun getById(
        @Path("id") postId: Long
    ): Call<Post>

    @DELETE("api/posts/{id}")
    fun removeById(
        @Path("id") postId: Long
    ): Call<Unit>

    @POST("api/posts/{id}/likes")
    fun likeById(
        @Path("id") postId: Long
    ): Call<Post>

    @DELETE("api/posts/{id}/likes")
    fun dislikeById(
        @Path("id") postId: Long
    ): Call<Post>
}