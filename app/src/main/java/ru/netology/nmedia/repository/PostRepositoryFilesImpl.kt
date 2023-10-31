package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.data_transfer_object.Post

class PostRepositoryFilesImpl(private val context: Context) : PostRepository {
    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val filename = "posts.json"

    private var nextId = 1L
    private var postList = emptyList<Post>()
        set(value) {
            field = value
            sync()
        }

    private val postData = MutableLiveData(postList)

    init {
        val file = context.filesDir.resolve(filename)
        if (file.exists()) {
            context.openFileInput(filename).bufferedReader().use {
                postList = gson.fromJson(it, type)
                nextId = postList.maxOf { it.id } + 1
                postData.value = postList
            }
        } else sync()
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
    }

    override fun updateShares(id: Long) {
        postList = postList.map { post ->
            if (post.id != id) post
            else post.copy(
                shares = post.shares + 1
            )
        }

        postData.value = postList
    }

    override fun removeById(id: Long) {
        postList = postList.filter { post -> post.id != id }
        postData.value = postList
    }

    private fun sync() {
        context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(postList))
        }
    }
}