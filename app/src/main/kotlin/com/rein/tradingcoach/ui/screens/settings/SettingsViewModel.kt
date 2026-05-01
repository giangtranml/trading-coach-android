package com.rein.tradingcoach.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rein.tradingcoach.data.api.ApiService
import com.rein.tradingcoach.data.api.models.UserSettings
import com.rein.tradingcoach.data.auth.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settings: UserSettings = UserSettings(),
    val maskedApiKey: String = "••••••••••••••••",
    val newApiKey: String? = null,
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state
    private var debounceJob: Job? = null

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            runCatching {
                val settings = apiService.getSettings()
                val profile = apiService.getProfile()
                _state.value = _state.value.copy(
                    settings = settings,
                    maskedApiKey = profile.maskedApiKey,
                    isLoading = false,
                )
            }.onFailure {
                _state.value = _state.value.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun updateSettings(settings: UserSettings) {
        _state.value = _state.value.copy(settings = settings)
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(500)
            _state.value = _state.value.copy(isSaving = true)
            runCatching { apiService.updateSettings(_state.value.settings) }
                .onSuccess { _state.value = _state.value.copy(isSaving = false) }
                .onFailure { _state.value = _state.value.copy(isSaving = false, error = it.message) }
        }
    }

    fun applyPreset(preset: PropFirmPreset) {
        val presetSettings = when (preset) {
            PropFirmPreset.FTMO -> _state.value.settings.copy(
                maxDailyLossPct = 5.0, maxRiskPct = 2.0, requireStopLoss = true, revengeWindowMinutes = 15, propFirmPreset = "FTMO"
            )
            PropFirmPreset.FUNDED_NEXT -> _state.value.settings.copy(
                maxDailyLossPct = 5.0, maxRiskPct = 2.0, requireStopLoss = true, revengeWindowMinutes = 15, propFirmPreset = "FundedNext"
            )
            PropFirmPreset.FUNDING_PIPS -> _state.value.settings.copy(
                maxDailyLossPct = 4.0, maxRiskPct = 1.0, requireStopLoss = true, revengeWindowMinutes = 10, propFirmPreset = "FundingPips"
            )
            PropFirmPreset.CUSTOM -> _state.value.settings.copy(propFirmPreset = "Custom")
        }
        updateSettings(presetSettings)
    }

    fun regenerateApiKey() {
        viewModelScope.launch {
            runCatching { apiService.regenerateApiKey() }
                .onSuccess { _state.value = _state.value.copy(newApiKey = it.apiKey) }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
        }
    }

    fun dismissNewApiKey() { _state.value = _state.value.copy(newApiKey = null) }

    fun logout() { authManager.logout() }

    override fun onCleared() {
        debounceJob?.cancel()
        super.onCleared()
    }
}

enum class PropFirmPreset { FTMO, FUNDED_NEXT, FUNDING_PIPS, CUSTOM }
