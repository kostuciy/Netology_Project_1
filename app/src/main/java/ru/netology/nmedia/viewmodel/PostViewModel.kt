package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.data_transfer_object.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val postData: LiveData<List<Post>> = repository.getPostData()

    fun updateLikesById(id: Long) = repository.updateLikesById(id)
    fun updateSharesById(id: Long) = repository.updateShares(id)
}