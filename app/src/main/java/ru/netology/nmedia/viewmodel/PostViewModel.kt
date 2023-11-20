package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.data_transfer_object.Post
import ru.netology.nmedia.data_transfer_object.VideoAttachment
import ru.netology.nmedia.database.AppDatabase
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositorySQLiteImpl

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

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = // PostRepositoryFilesImpl(application)
        PostRepositorySQLiteImpl(
            AppDatabase.getInstance(context = application).postDao()
        )
    val postData: LiveData<List<Post>> = repository.getPostData()
    val currentPost = MutableLiveData(emptyPost)

    fun savePost() {
        currentPost.value?.let {
            repository.savePost(it)
        }
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
    fun changeVideoAttachment(videoLink: String) {
        currentPost.value?.let { post ->
            val text = videoLink.trim()
            currentPost.value = when {
                text.isBlank() -> post.copy(videoAttachment = null)
                post.videoAttachment == null ->
                    post.copy(videoAttachment = VideoAttachment(text))
                text != post.videoAttachment.link ->
                    post.copy(videoAttachment = VideoAttachment(text))
                else -> post
            }
        }
    }

    fun updateLikesById(id: Long) = repository.updateLikesById(id)
    fun updateSharesById(id: Long) = repository.updateShares(id)
    fun removeById(id: Long) = repository.removeById(id)
}