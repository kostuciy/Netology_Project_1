package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.data_transfer_object.Post

interface PostRepository {

    fun getPostData(): LiveData<List<Post>>
    fun updateLikesById(id: Long)
    fun updateShares(id: Long)
}