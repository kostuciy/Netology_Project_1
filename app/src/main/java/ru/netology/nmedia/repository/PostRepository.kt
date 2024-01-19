package ru.netology.nmedia.repository

import ru.netology.nmedia.data_transfer_object.Post

interface PostRepository {

    fun getPostData(): List<Post>
    fun savePost(post: Post): Post
    fun updateLikesById(post: Post)
    fun updateShares(id: Long)
    fun removeById(id: Long)
}