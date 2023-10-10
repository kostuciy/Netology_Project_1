package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.data_transfer_object.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

private val emptyPost = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    publishedDate = "",
    likes = 0,
    shares = 0,
    views = 0
)

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val postData: LiveData<List<Post>> = repository.getPostData()
    val currentPost = MutableLiveData(emptyPost)

    fun savePost() {
        currentPost.value?.let {
            repository.savePost(it)
        }

        currentPost.value = emptyPost
    }

    fun setToEdit(post: Post) {
        currentPost.value = post
    }
    fun setToNewPost() {
        currentPost.value = emptyPost
    }

    fun changeContent(content: String) {
        currentPost.value?.let { post ->
            val text = content.trim()
            if (text != post.content) {
                currentPost.value = post.copy(content = text)
            }
        }
    }

    fun updateLikesById(id: Long) = repository.updateLikesById(id)
    fun updateSharesById(id: Long) = repository.updateShares(id)
    fun removeById(id: Long) = repository.removeById(id)
}