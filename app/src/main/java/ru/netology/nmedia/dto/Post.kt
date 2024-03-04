package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val attachment: Attachment? = null,
    val onScreen: Boolean = true,
    val ownedByMe: Boolean = false
)

data class Attachment(
    val url: String,
    val type: AttachmentType
)

