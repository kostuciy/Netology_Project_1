package ru.netology.nmedia.repository

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import okio.IOException
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.PhotoModel
import java.io.File

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
//    link to dao LiveData with PostEntity list (transformed to LiveData of Posts)
    override val data =
        dao.getAll().map(List<PostEntity>::toDto).flowOn(Dispatchers.Main)

//    loading posts from server with retrofit and adding to database if successful
    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
            dao.updateVisibility() // changes all posts to visible
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        Log.d("GUG", "$id")
        while (true) {
            delay(10_000L)
            val response = PostsApi.service.getNewer(id)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())

            val body = response.body()?.map { it.copy(onScreen = false) }
                ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
            emit(body.size) // returns new posts' ids
        }
    }
        .catch { e -> e.printStackTrace() }
        .flowOn(Dispatchers.Default)

    //    saving post to server with retrofit and adding to database if sucessfull
    override suspend fun save(post: Post, photo: PhotoModel?) {
        if (post.id == 0L) {
            dao.insert(
                PostEntity.fromDto(post.copy(id = Long.MAX_VALUE))
            )
        }

        try {
            val postWithAttachment = if (photo != null) {
                val media = upload(photo.file)
                post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            } else {
                post
            }

            val response = PostsApi.service.save(postWithAttachment)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.removeById(Long.MAX_VALUE) // removing locally saved post
            dao.insert(PostEntity.fromDto(body).copy(onScreen = true)) // adding post from server to db
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Error) {
            throw UnknownError
        }
    }

//    attachment full path during saving:
//      gallery/camera -> photoModel (as file and uri) -> multipart (file uploaded to server
//      as mutlipart) -> media (if upload successful returned as media with id)
//      -> attachment in post (media id and type saved as Attachment and attached to post,
//      so server could find and load correct attachment to post)
    private suspend fun upload(file: File): Media {
        val response = PostsApi.service.upload(
            MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        )

        if (!response.isSuccessful)
            throw ApiError(response.code(), response.message())

        return response.body() ?: throw ApiError(response.code(), response.message())
    }

//    removing post from database (causes ui to update) and then from server
    override suspend fun removeById(post: Post) {
        dao.removeById(post.id)
        try {
            val response = PostsApi.service.removeById(post.id)
            if (!response.isSuccessful || response.body() == null) {
                dao.insert(PostEntity.fromDto(post))
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            dao.insert(PostEntity.fromDto(post))
            throw NetworkError
        } catch (e: Exception) {
            dao.insert(PostEntity.fromDto(post))
            throw UnknownError
        }
    }

    override suspend fun likeById(post: Post) {
        dao.updateLikesById(post.id)
        try {
            val response =
                if (post.likedByMe) PostsApi.service.dislikeById(post.id)
                else PostsApi.service.likeById(post.id)
            if (!response.isSuccessful || response.body() == null) {
                dao.updateLikesById(post.id)
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            dao.updateLikesById(post.id)
            throw NetworkError
        } catch (e: Exception) {
            dao.updateLikesById(post.id)
            throw UnknownError
        }
    }
}
