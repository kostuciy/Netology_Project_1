package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.data_transfer_object.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var testPost: Post = Post(
        0,
        "Нетология",
        "24 сентября в 9:27",
        "Тект текст текст текст текст текст",
        999,
        1099,
        5000,
        false
    )
    private val postData = MutableLiveData(testPost)

    override fun getPostData(): LiveData<Post> = postData

    override fun updateLikes() {
        val newLikeAmount =
            testPost.likes + if (!testPost.likedByMe) 1 else -1

        testPost = testPost.copy(
            likes = newLikeAmount,
            likedByMe = !testPost.likedByMe
        )
        postData.value = testPost
    }

    override fun updateShares() {
        testPost = testPost.copy(
            shares = testPost.shares + 1
        )

        postData.value = testPost
    }

}