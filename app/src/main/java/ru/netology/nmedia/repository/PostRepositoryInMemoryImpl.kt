package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.data_transfer_object.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var nextId = 4L // TODO: change to 1
    private var postList: List<Post> = mutableListOf(
        Post(
            1,
            "Нетология",
            "24 сентября в 9:27",
            "Тект текст текст текст текст текст",
            999,
            1099,
            5000,
            false
        ),
        Post(
            2,
            "Нетология",
            "26 сентября в 10:44",
            "Тект текст текст текст текст текст",
            19,
            2000,
            60000,
            false
        ),
        Post(
            3,
            "Нетология",
            "26 сентября в 10:44",
            "Тект текст текст текст текст текст",
            19,
            2000,
            60000,
            false
        )

    )
    private val postData = MutableLiveData(postList)

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
}