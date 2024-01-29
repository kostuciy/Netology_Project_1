package ru.netology.nmedia.repository

import ru.netology.nmedia.data_transfer_object.Post

interface PostRepository {

    fun getPostDataAsync(callback: PostCallback<List<Post>>)
    fun savePost(post: Post, callback: PostCallback<Post>)
    fun updateLikesById(post: Post, callback: PostCallback<Post>)
    fun updateShares(id: Long)
    fun removeById(id: Long)

    interface PostCallback<T> {

        fun onSuccess(argument: T)
        fun onError(throwable: Throwable)
    }
}