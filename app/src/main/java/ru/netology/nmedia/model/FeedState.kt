package ru.netology.nmedia.model

import ru.netology.nmedia.data_transfer_object.Post

data class FeedState(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
    val empty: Boolean = false
)