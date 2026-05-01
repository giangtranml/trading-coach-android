package com.rein.tradingcoach.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rein.tradingcoach.data.api.ApiService
import com.rein.tradingcoach.data.api.models.LoginRequest
import com.rein.tradingcoach.data.auth.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedIn: Boolean = false,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    fun onEmailChange(value: String) { _state.value = _state.value.copy(email = value, error = null) }
    fun onPasswordChange(value: String) { _state.value = _state.value.copy(password = value, error = null) }

    fun login() {
        val s = _state.value
        if (s.email.isBlank() || s.password.isBlank()) return
        viewModelScope.launch {
            _state.value = s.copy(isLoading = true, error = null)
            runCatching { apiService.login(LoginRequest(s.email, s.password)) }
                .onSuccess { resp ->
                    authManager.saveToken(resp.accessToken)
                    _state.value = _state.value.copy(isLoading = false, loggedIn = true)
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Login failed")
                }
        }
    }
}
