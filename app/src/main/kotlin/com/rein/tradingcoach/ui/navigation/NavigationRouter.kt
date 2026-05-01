package com.rein.tradingcoach.ui.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NavigationRouter {
    val pendingViolationId: MutableStateFlow<Int?> = MutableStateFlow(null)
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object ViolationHistory : Screen("violations")
    object ViolationDetail : Screen("violations/{id}") {
        fun route(id: Int) = "violations/$id"
    }
    object Settings : Screen("settings")
    object ApiKeyDisplay : Screen("onboarding/api-key")
    object EaSetupGuide : Screen("onboarding/ea-setup")
}
