package ru.netology.nmedia.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.SignState
import ru.netology.nmedia.repository.PostRepositoryImpl
import java.lang.IllegalArgumentException

class SignViewModel : ViewModel() {
    private val appAuthInst = AppAuth.getInstance()

    private val _state = MutableStateFlow(SignState())
    val state: StateFlow<SignState>
        get() = _state
    private val _avatar = MutableStateFlow<Uri?>(null)
    val avatar: StateFlow<Uri?>
        get() = _avatar

    fun authenticate(login: String, password: String) {
        viewModelScope.launch {
            try {
                _state.value = SignState(loading = true)
                val authState = PostRepositoryImpl.authenticate(login, password)
                Log.d("GUG", "login - ${authState}")
                _state.value = SignState(signedIn = true)

//                before uploading auth checks if token is not null and
//                throws error to catch if it is
                authState.token?.let { appAuthInst.setAuth(authState.id, it) }
                    ?: throw IllegalArgumentException()
            } catch (e: Exception) {
                _state.value = SignState(error = true)
            }
        }
    }

    fun register(login: String, password: String, name: String, mediaUri: Uri?) {
        viewModelScope.launch {
            try {
                _state.value = SignState(loading = true)
                val authState = PostRepositoryImpl.register(login, password, name, mediaUri)
                _state.value = SignState(signedIn = true)

//                before uploading auth checks if token is not null and
//                throws error to catch if it is
                authState.token?.let { appAuthInst.setAuth(authState.id, it) }
                    ?: throw IllegalArgumentException()
            } catch (e: Exception) {
                _state.value = SignState(error = true)
            }
        }
    }

    fun setImage(uri: Uri) {
        _avatar.value = uri
    }

    fun toDefault() {
        _state.value = SignState()
    }

}