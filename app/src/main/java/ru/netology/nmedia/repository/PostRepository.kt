package ru.netology.nmedia.repository

import ru.netology.nmedia.data_transfer_object.Post

interface PostRepository {

    fun getPostDataAsync(callback: GetPostsCallback)
    fun savePost(post: Post): Post
    fun updateLikesById(post: Post): Post
    fun updateShares(id: Long)
    fun removeById(id: Long)

    interface GetPostsCallback {

        fun onSuccess(posts: List<Post>)
        fun onError(throwable: Throwable)
    }
}