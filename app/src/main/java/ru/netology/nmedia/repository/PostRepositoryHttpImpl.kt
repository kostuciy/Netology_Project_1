package ru.netology.nmedia.repository

import android.view.View
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.data_transfer_object.Post
import java.io.IOException
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

    override fun getPostDataAsync(callback: PostRepository.PostCallback<List<Post>>) {
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts")
            .build()

        return client.newCall(request)
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseText = response.body?.string()

                        if (responseText == null) {
                            callback.onError(RuntimeException("body is null"))
                            return
                        }

                        try {
                            callback.onSuccess(gson.fromJson(responseText, type))
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                }
            )
//        val call = client.newCall(request)
//        val response = call.execute()
//
//        val body = requireNotNull(response.body)
//        val responseText = body.string()
//
//        return gson.fromJson<List<Post>>(responseText, type)


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

    override fun savePost(post: Post, callback: PostRepository.PostCallback<Post>){
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        return client.newCall(request)
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseText = response.body?.string()

                        if (responseText == null) {
                            callback.onError(RuntimeException("body is null"))
                            return
                        }

                        try {
                            callback.onSuccess(gson.fromJson(responseText, Post::class.java))
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                }
            )
//        val call = client.newCall(request)
//        val response = call.execute()
//
//        val body = requireNotNull(response.body)
//        val responseText = body.string()
//
//        return gson.fromJson(responseText, Post::class.java)
    }

    override fun updateLikesById(post: Post, callback: PostRepository.PostCallback<Post>) {
        val request = if (!post.likedByMe)
            Request.Builder()
                .url("${BASE_URL}api/slow/posts/${post.id}/likes")
                .post(gson.toJson(post).toRequestBody(jsonType))
                .build()
        else Request.Builder()
            .url("${BASE_URL}api/slow/posts/${post.id}/likes")
            .delete(gson.toJson(post).toRequestBody(jsonType))
            .build()

        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseText = response.body?.string()

                    if (responseText == null) {
                        callback.onError(RuntimeException("body is null"))
                        return
                    }

                    try {
                        callback.onSuccess(gson.fromJson(responseText, Post::class.java))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            }
        )
//        val response = call.execute()
//
//        val body = requireNotNull(response.body)
//        return gson.fromJson(body.string(), Post::class.java)
//
//        return post.copy(
//            likedByMe = !post.likedByMe,
//            likes = post.likes + if (post.likedByMe) -1 else 1,
//            publishedDate = "Now"
//        )
    }

    override fun updateShares(id: Long) {
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long) {
        TODO("Not yet implemented")
    }

    fun setAvatar(view: View, id: Long) {
        Glide.with(view)
            .load("${BASE_URL}api/slow/posts/${id}/likes")
    }
}



