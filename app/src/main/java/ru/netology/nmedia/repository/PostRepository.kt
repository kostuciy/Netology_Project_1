package ru.netology.nmedia.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.PhotoModel

interface PostRepository {
//    changing liveData to flow as it can work in coroutines
//    (so that operations like updating database wouldn't be executed in UI thread)
    val data: Flow<List<Post>>
    suspend fun getAll()
    suspend fun save(post: Post, photo: PhotoModel?)
    suspend fun removeById(post: Post)
    suspend fun likeById(post: Post)

    fun getNewerCount(id: Long): Flow<Int>

    suspend fun authenticate(login: String, password: String): AuthState

    suspend fun register(login: String, password: String, name: String, media: Uri?): AuthState
}
