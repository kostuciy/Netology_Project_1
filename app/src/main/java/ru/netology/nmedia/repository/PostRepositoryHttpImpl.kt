package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.data_transfer_object.Post
import java.util.concurrent.TimeUnit

class PostRepositoryHttpImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999/"
        private val jsonType = "application/json".toMediaType()
        private val type = object : TypeToken<List<Post>>() {}.type
    }

    override fun getPostData(): List<Post> {
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts")
            .build()
        val call = client.newCall(request)
        val response = call.execute()

        val body = requireNotNull(response.body)
        val responseText = body.string()

        return gson.fromJson<List<Post>>(responseText, type)
    }

//    private fun getPostById(id: Long): Post? {
//        val request = Request.Builder()
//            .url("${BASE_URL}api/slow/posts/${id}")
//            .build()
//        val call = client.newCall(request)
//        val response = call.execute()
//
//        val body = requireNotNull(response.body)
//        val responseText = body.string()
//
//        return gson.fromJson<Post>(responseText, Post::class.java)
//    }

    override fun savePost(post: Post): Post {
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()
        val call = client.newCall(request)
        val response = call.execute()

        val body = requireNotNull(response.body)
        val responseText = body.string()

        return gson.fromJson(responseText, Post::class.java)
    }

    override fun updateLikesById(id: Long) {
        val post = getPostData().find { it.id == id }.let { post ->
            post ?: throw IllegalArgumentException()
        }

        val request = if (!post.likedByMe)
            Request.Builder()
                .url("${BASE_URL}api/slow/posts/${post.id}/likes")
                .post(gson.toJson(post).toRequestBody(jsonType))
                .build()
        else Request.Builder()
            .url("${BASE_URL}api/slow/posts/${post.id}/likes")
            .delete(gson.toJson(post).toRequestBody(jsonType))
            .build()
        val call = client.newCall(request)
        val response = call.execute()

//        val body = requireNotNull(response.body)
//        return gson.fromJson(body.string(), Post::class.java)
    }

    override fun updateShares(id: Long) {
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long) {
        TODO("Not yet implemented")
    }
}



