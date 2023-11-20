package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.data_transfer_object.Post
import ru.netology.nmedia.data_transfer_object.VideoAttachment

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val publishedDate: String,
    val content: String,
    val likes: Int,
    val shares: Int,
    val views: Int,
    val likedByMe: Boolean,
    val videoAttachment: String? = null,
) {

    fun toDto(): Post = with(this) {
        Post(
            id = id, author = author, publishedDate = publishedDate,
            content = content, likes = likes, shares = shares,
            views = views, likedByMe = likedByMe, videoAttachment = videoAttachment?.let { VideoAttachment(it) }
        )
    }

    companion object {
        fun fromDto(dto: Post): PostEntity = with(dto) {
            PostEntity(
                id = id, author = author, publishedDate = publishedDate,
                content = content, likes = likes, shares = shares,
                views = views, likedByMe = likedByMe, videoAttachment = videoAttachment?.link
            )
        }
    }
}