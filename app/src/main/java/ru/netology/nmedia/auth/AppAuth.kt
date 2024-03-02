package ru.netology.nmedia.auth

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authState = MutableStateFlow(
        AuthState(
            prefs.getLong(KEY_ID, 0L),
            prefs.getString(KET_TOKEN, null)
        )

    )
    val authState = _authState.asStateFlow()

    @Synchronized // means that fun cannot be used in multiple threads at the same time
    fun setAuth(id: Long, token: String) {
        _authState.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(KEY_ID, id)
            putString(KET_TOKEN, token)
            commit()
        }
    }

    @Synchronized
    fun removeAuth() {
        _authState.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
    }
    companion object {
        @Volatile // if used in multiple threads the changes in variables syncs between all of them
        private var instance: AppAuth? = null

        private const val KEY_ID = "id"
        private const val KET_TOKEN = "token"

        fun getInstance() = synchronized(this) {
            instance ?: throw IllegalAccessException("AppAuth is not initialized.")
        }

        fun initAuth(context: Context) =
            instance ?: synchronized(this) {
                instance ?: AppAuth(context).also { instance = it }
            }
    }
}

data class AuthState(val id: Long = 0L, val token: String? = null)