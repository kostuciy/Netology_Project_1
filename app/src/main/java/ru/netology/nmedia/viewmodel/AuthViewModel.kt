package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.auth.AppAuth

class AuthViewModel : ViewModel() {
    private val authApp = AppAuth.getInstance()

    val data = authApp.authState
    val authenticated: Boolean
        get() = data.value.id != 0L

    fun signOut() = authApp.removeAuth()
}