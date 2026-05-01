package com.rein.tradingcoach.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rein.tradingcoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EaSetupGuideScreen(onContinue: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EA Setup Guide", color = TcTextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TcPageBg),
            )
        },
        containerColor = TcPageBg,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SetupStep(number = 1, title = "Download the EA", body = "Download TradingCoach.ex5 from your account dashboard or the provided link.")
            SetupStep(number = 2, title = "Open MetaTrader 5", body = "Go to File → Open Data Folder → MQL5 → Experts.")
            SetupStep(number = 3, title = "Copy the EA file", body = "Place TradingCoach.ex5 in the Experts folder and restart MT5.")
            SetupStep(number = 4, title = "Attach to chart", body = "Drag TradingCoach from the Navigator panel onto any chart. Enable 'Allow live trading'.")
            SetupStep(number = 5, title = "Enter your API key", body = "Paste the API key from the previous screen into the EA inputs.")
            SetupStep(number = 6, title = "Whitelist the URL", body = "Tools → Options → Expert Advisors → add https://api.tradingcoach.app to the allowed URLs list.")

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                Text("Go to Dashboard")
            }
        }
    }
}

@Composable
private fun SetupStep(number: Int, title: String, body: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            color = TcBlueBg,
            shape = androidx.compose.foundation.shape.CircleShape,
            modifier = Modifier.size(32.dp),
        ) {
            Box(contentAlignment = androidx.compose.ui.Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text("$number", color = TcBlue, style = MaterialTheme.typography.labelLarge)
            }
        }
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, color = TcTextPrimary)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = TcTextSecondary)
        }
    }
}
