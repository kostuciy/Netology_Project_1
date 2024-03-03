package ru.netology.nmedia.model

data class SignState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val signedIn: Boolean = false
)