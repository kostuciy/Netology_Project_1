package ru.netology.nmedia.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.SignInState
import ru.netology.nmedia.repository.PostRepositoryImpl
import java.lang.IllegalArgumentException

class SignInViewModel : ViewModel() {
    private val appAuthInst = AppAuth.getInstance()

    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState>
        get() = _state

    fun authenticate(login: String, password: String) {
        viewModelScope.launch {
            try {
                _state.value = SignInState(loading = true)
                val authState = PostRepositoryImpl.authenticate(login, password)
                Log.d("GUG", "ggg - ${authState}")
                _state.value = SignInState(signedIn = true)

//                before uploading auth checks if token is not null and
//                throws error to catch if it is
                authState.token?.let { appAuthInst.setAuth(authState.id, it) }
                    ?: throw IllegalArgumentException()
            } catch (e: Exception) {
                _state.value = SignInState(error = true)
            }
        }
    }

    fun toDefault() {
        _state.value = SignInState()
    }

}