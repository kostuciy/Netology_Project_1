package ru.netology.nmedia.data_transfer_object

data class Post(
    val id: Long,
    val author: String,
    val publishedDate: String,
    val content: String,
    val likes: Int,
    val shares: Int,
    val views: Int,
    val likedByMe: Boolean,
    val videoAttachment: VideoAttachment? = null,
)