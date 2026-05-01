package com.rein.tradingcoach.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rein.tradingcoach.ui.screens.violations.ViolationRowCard
import com.rein.tradingcoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onViolationClick: (Int) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", color = TcTextPrimary) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TcTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TcPageBg),
            )
        },
        containerColor = TcPageBg,
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = viewModel::load,
            modifier = Modifier.padding(padding),
        ) {
            state.error?.let { error ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error, color = TcRed)
                }
                return@PullToRefreshBox
            }

            val dashboard = state.dashboard
            if (dashboard == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TcBlue)
                }
                return@PullToRefreshBox
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DisciplineScoreRing(score = dashboard.disciplineScore)
                Spacer(Modifier.height(24.dp))
                AccountCard(dashboard = dashboard)
                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Recent Violations", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = onNavigateToHistory) { Text("See All", color = TcLink) }
                }
                Spacer(Modifier.height(8.dp))
                if (dashboard.recentViolations.isEmpty()) {
                    Text("No violations today", style = MaterialTheme.typography.bodyMedium, color = TcTextSecondary)
                } else {
                    dashboard.recentViolations.forEach { violation ->
                        ViolationRowCard(violation = violation, onClick = { onViolationClick(violation.id) })
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
