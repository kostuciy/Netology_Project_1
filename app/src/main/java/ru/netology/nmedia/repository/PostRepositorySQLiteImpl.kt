package ru.netology.nmedia.repository

import androidx.lifecycle.map
import ru.netology.nmedia.entity.PostEntity
import androidx.lifecycle.LiveData
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.data_transfer_object.Post

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {
//    private var postList = emptyList<Post>()
//    private var postData = MutableLiveData(postList)
//
//    init {
//        postList = dao.getAll()
//        postData.value = postList
//    }
    override fun getPostData(): LiveData<List<Post>> = dao.getAll().map { posts ->
        posts.map { postEntity -> postEntity.toDto() }
    }

    override fun savePost(post: Post) = dao.savePost(
        PostEntity.fromDto(post)
    )

    override fun updateLikesById(id: Long) = dao.updateLikesById(id)

    override fun updateShares(id: Long) = dao.updateSharesById(id)

    override fun removeById(id: Long) = dao.deleteById(id)
}



