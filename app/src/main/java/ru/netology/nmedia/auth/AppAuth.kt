package ru.netology.nmedia.auth

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.dto.PushToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authState = MutableStateFlow(
        AuthState(
            prefs.getLong(KEY_ID, 0L),
            prefs.getString(KET_TOKEN, null)
        )

    )
    val authState = _authState.asStateFlow()

    init {
        val id = _authState.value.id
        val token = _authState.value.token

        if (id == 0L || token == null) {
            with(prefs.edit()) {
                clear()
                apply()
            }
        }

        sendPushToken()
    }

    @Synchronized // means that fun cannot be used in multiple threads at the same time
    fun setAuth(id: Long, token: String) {
        _authState.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(KEY_ID, id)
            putString(KET_TOKEN, token)
            commit()
        }

        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        _authState.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }

        sendPushToken()
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {

        fun getApiService(): PostsApiService
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = PushToken(token ?: Firebase.messaging.token.await())
                val apiService =
                    EntryPointAccessors
                        .fromApplication(context, AppAuthEntryPoint::class.java)
                        .getApiService()
                apiService.sendPushToken(pushToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
//        @Volatile // if used in multiple threads the changes in variables syncs between all of them
//        private var instance: AppAuth? = null

        private const val KEY_ID = "id"
        private const val KET_TOKEN = "token"
//
//        fun getInstance() = synchronized(this) {
//            instance ?: throw IllegalAccessException("AppAuth is not initialized.")
//        }
//
//        fun initAuth(context: Context) =
//            instance ?: synchronized(this) {
//                instance ?: AppAuth(context).also { instance = it }
//            }
    }
}

data class AuthState(val id: Long = 0L, val token: String? = null)