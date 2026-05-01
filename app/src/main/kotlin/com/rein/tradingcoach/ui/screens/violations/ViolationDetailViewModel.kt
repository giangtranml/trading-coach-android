package com.rein.tradingcoach.ui.screens.violations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rein.tradingcoach.data.api.ApiService
import com.rein.tradingcoach.data.api.models.ViolationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ViolationDetailUiState(
    val violation: ViolationResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ViolationDetailViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    private val _state = MutableStateFlow(ViolationDetailUiState())
    val state: StateFlow<ViolationDetailUiState> = _state

    fun load(id: Int) {
        viewModelScope.launch {
            _state.value = ViolationDetailUiState(isLoading = true)
            runCatching { apiService.getViolation(id) }
                .onSuccess { _state.value = ViolationDetailUiState(violation = it) }
                .onFailure { _state.value = ViolationDetailUiState(error = it.message ?: "Failed to load") }
        }
    }
}
