package com.rein.tradingcoach.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rein.tradingcoach.data.auth.AuthManager
import com.rein.tradingcoach.ui.screens.auth.LoginScreen
import com.rein.tradingcoach.ui.screens.auth.RegisterScreen
import com.rein.tradingcoach.ui.screens.dashboard.DashboardScreen
import com.rein.tradingcoach.ui.screens.onboarding.ApiKeyDisplayScreen
import com.rein.tradingcoach.ui.screens.onboarding.EaSetupGuideScreen
import com.rein.tradingcoach.ui.screens.settings.SettingsScreen
import com.rein.tradingcoach.ui.screens.violations.ViolationDetailScreen
import com.rein.tradingcoach.ui.screens.violations.ViolationHistoryScreen
import javax.inject.Inject

@Composable
fun NavGraph(authManager: AuthManager = hiltViewModel<NavGraphViewModel>().authManager) {
    val navController = rememberNavController()
    val isLoggedIn by authManager.isLoggedIn.collectAsState()
    val startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Login.route

    // Handle deep-link from push notification
    val pendingViolationId by NavigationRouter.pendingViolationId.collectAsState()
    LaunchedEffect(pendingViolationId) {
        pendingViolationId?.let { id ->
            navController.navigate(Screen.ViolationDetail.route(id))
            NavigationRouter.pendingViolationId.value = null
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Dashboard.route) { popUpTo(0) } },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { apiKey ->
                    navController.navigate("${Screen.ApiKeyDisplay.route}?key=$apiKey") { popUpTo(0) }
                },
                onNavigateToLogin = { navController.popBackStack() },
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onViolationClick = { id -> navController.navigate(Screen.ViolationDetail.route(id)) },
                onNavigateToHistory = { navController.navigate(Screen.ViolationHistory.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
            )
        }
        composable(Screen.ViolationHistory.route) {
            ViolationHistoryScreen(
                onViolationClick = { id -> navController.navigate(Screen.ViolationDetail.route(id)) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Screen.ViolationDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) { backStack ->
            ViolationDetailScreen(
                violationId = backStack.arguments!!.getInt("id"),
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) } },
                onBack = { navController.popBackStack() },
            )
        }
        composable("${Screen.ApiKeyDisplay.route}?key={key}") { backStack ->
            ApiKeyDisplayScreen(
                apiKey = backStack.arguments?.getString("key") ?: "",
                onContinue = { navController.navigate(Screen.EaSetupGuide.route) { popUpTo(0) } },
            )
        }
        composable(Screen.EaSetupGuide.route) {
            EaSetupGuideScreen(
                onContinue = { navController.navigate(Screen.Dashboard.route) { popUpTo(0) } },
            )
        }
    }
}
