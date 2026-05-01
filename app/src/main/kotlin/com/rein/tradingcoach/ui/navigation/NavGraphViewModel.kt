package com.rein.tradingcoach.ui.navigation

import androidx.lifecycle.ViewModel
import com.rein.tradingcoach.data.auth.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavGraphViewModel @Inject constructor(val authManager: AuthManager) : ViewModel()
