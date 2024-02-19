package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
//    changing liveData to flow as it can work in coroutines
//    (so that operations like updating database wouldn't be executed in UI thread)
    val data: Flow<List<Post>>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun likeById(post: Post)

    fun getNewerCount(id: Long): Flow<Int>
}
