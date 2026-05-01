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

data class ViolationHistoryUiState(
    val violations: List<ViolationResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val hasNextPage: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class ViolationHistoryViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    private val _state = MutableStateFlow(ViolationHistoryUiState())
    val state: StateFlow<ViolationHistoryUiState> = _state
    private var currentPage = 1

    init { loadFirstPage() }

    fun loadFirstPage() {
        currentPage = 1
        viewModelScope.launch {
            _state.value = ViolationHistoryUiState(isLoading = true)
            runCatching { apiService.getViolations(page = 1) }
                .onSuccess { resp ->
                    _state.value = ViolationHistoryUiState(
                        violations = resp.items,
                        hasNextPage = resp.page < resp.pages,
                    )
                    currentPage = 1
                }
                .onFailure { _state.value = ViolationHistoryUiState(error = it.message) }
        }
    }

    fun loadNextPageIfNeeded(lastVisible: ViolationResponse) {
        val s = _state.value
        if (s.isLoadingNextPage || !s.hasNextPage || s.violations.lastOrNull()?.id != lastVisible.id) return
        viewModelScope.launch {
            _state.value = s.copy(isLoadingNextPage = true)
            val nextPage = currentPage + 1
            runCatching { apiService.getViolations(page = nextPage) }
                .onSuccess { resp ->
                    currentPage = nextPage
                    _state.value = _state.value.copy(
                        violations = _state.value.violations + resp.items,
                        isLoadingNextPage = false,
                        hasNextPage = resp.page < resp.pages,
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(isLoadingNextPage = false)
                }
        }
    }
}
