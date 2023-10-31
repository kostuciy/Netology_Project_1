package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.data_transfer_object.Post

class PostRepositorySharedPrefsImpl(context: Context) : PostRepository {
    private val gson = Gson()
    private val preferences = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val key = "postList"

    private var nextId = 1L
    private var postList = emptyList<Post>()

    private val postData = MutableLiveData(postList)

    init {
        preferences.getString(key, null)?.let {
            postList = gson.fromJson(it, type)
            nextId = postList.maxOf { it.id }
            postData.value = postList

        }
    }

    override fun getPostData(): LiveData<List<Post>> = postData


    override fun savePost(post: Post) {
        if (post.id == 0L) {
            postList = listOf(
                post.copy(
                    id = nextId++,
                    author = "Me",
                    publishedDate = "Now"
                )
            ) + postList
        } else {
            postList = postList.map {
                if (it.id != post.id) it
                else it.copy(content = post.content)
            }
        }
        postData.value = postList
        sync()
    }

    override fun updateLikesById(id: Long) {
        postList = postList.map { post ->
            if (post.id != id) post
            else post.copy(
                likes = post.likes + if (!post.likedByMe) 1 else -1,
                likedByMe = !post.likedByMe
            )
        }

        postData.value = postList
        sync()
    }

    override fun updateShares(id: Long) {
        postList = postList.map { post ->
            if (post.id != id) post
            else post.copy(
                shares = post.shares + 1
            )
        }

        postData.value = postList
        sync()
    }

    override fun removeById(id: Long) {
        postList = postList.filter { post -> post.id != id }
        postData.value = postList
        sync()
    }

    private fun sync() {
        with(preferences.edit()) {
            putString(key, gson.toJson(postList))
            apply()
        }
    }
}