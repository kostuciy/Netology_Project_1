package ru.netology.nmedia.repository

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import okio.IOException
import kotlinx.coroutines.flow.*
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError

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

            val body = response.body() ?: throw ApiError(response.code(), response.message())////////////////////////////////////////////
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = PostsApi.service.getNewer(id)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
            emit(body.size)
        }
    }
        .catch { e -> e.printStackTrace() }
        .flowOn(Dispatchers.Default)

    //    saving post to server with retrofit and adding to database if sucessfull
    override suspend fun save(post: Post) {
        if (post.id == 0L) {
            dao.insert(
                PostEntity.fromDto(post.copy(id = Long.MAX_VALUE))
            )
        }

        try {
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.removeById(Long.MAX_VALUE) // removing locally saved post
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Error) {
            throw UnknownError
        }

//        try {
//            val response = PostsApi.service.save(post)
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
//            dao.insert(PostEntity.fromDto(body))
//        } catch (e: IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
    }

//    removing post from database (causes ui to update) and then from server
    override suspend fun removeById(id: Long) {
        dao.removeById(id)
        try {
            val response = PostsApi.service.removeById(id)
            if (!response.isSuccessful || response.body() == null) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
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
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}
