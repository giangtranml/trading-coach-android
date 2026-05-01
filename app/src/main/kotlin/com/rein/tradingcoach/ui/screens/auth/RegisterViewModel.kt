package com.rein.tradingcoach.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rein.tradingcoach.data.api.ApiService
import com.rein.tradingcoach.data.api.models.RegisterRequest
import com.rein.tradingcoach.data.auth.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val apiKey: String? = null,
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state

    fun onEmailChange(value: String) { _state.value = _state.value.copy(email = value, error = null) }
    fun onPasswordChange(value: String) { _state.value = _state.value.copy(password = value, error = null) }

    fun register() {
        val s = _state.value
        if (s.email.isBlank() || s.password.length < 8) {
            _state.value = s.copy(error = "Password must be at least 8 characters")
            return
        }
        viewModelScope.launch {
            _state.value = s.copy(isLoading = true, error = null)
            runCatching { apiService.register(RegisterRequest(s.email, s.password)) }
                .onSuccess { resp ->
                    authManager.saveToken(resp.accessToken)
                    _state.value = _state.value.copy(isLoading = false, apiKey = resp.apiKey)
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Registration failed")
                }
        }
    }
}
