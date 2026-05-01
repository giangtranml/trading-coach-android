package com.rein.tradingcoach.ui.screens.violations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rein.tradingcoach.data.api.models.ViolationResponse
import com.rein.tradingcoach.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViolationDetailScreen(
    violationId: Int,
    onBack: () -> Unit,
    viewModel: ViolationDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(violationId) { viewModel.load(violationId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Violation Detail", color = TcTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TcTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TcPageBg),
            )
        },
        containerColor = TcPageBg,
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator(color = TcBlue)
            }
            state.error != null -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text(state.error!!, color = TcRed)
            }
            state.violation != null -> ViolationDetailContent(
                violation = state.violation!!,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun ViolationDetailContent(violation: ViolationResponse, modifier: Modifier = Modifier) {
    val severityColor = when (violation.severity.lowercase()) {
        "critical" -> TcRed
        "warning" -> TcAmber
        else -> TcYellow
    }
    val severityBg = when (violation.severity.lowercase()) {
        "critical" -> TcRedBg
        "warning" -> TcAmberBg
        else -> TcYellowBg
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(severityBg),
            contentAlignment = Alignment.Center,
        ) {
            Text("!", color = severityColor, style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(16.dp))

        val typeDisplay = when (violation.violationType) {
            "overleveraging" -> "Over-leveraging"
            "revenge_trading" -> "Revenge Trading"
            "overtrading" -> "Overtrading"
            "missing_stop_loss" -> "Missing Stop Loss"
            "daily_loss_limit" -> "Daily Loss Limit"
            "max_position_size" -> "Max Position Size"
            else -> violation.violationType.replace("_", " ").replaceFirstChar { it.uppercase() }
        }
        Text(typeDisplay, style = MaterialTheme.typography.titleLarge, color = TcTextPrimary)
        Spacer(Modifier.height(4.dp))
        Surface(
            color = severityBg,
            shape = RoundedCornerShape(4.dp),
        ) {
            Text(
                violation.severity.uppercase(),
                color = severityColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(violation.createdAt),
            style = MaterialTheme.typography.bodySmall,
            color = TcTextTertiary,
        )
        Spacer(Modifier.height(24.dp))

        // Coach message
        violation.coachMessage?.let { msg ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TcCardBg)
                    .padding(16.dp),
            ) {
                Text("Coach Message", style = MaterialTheme.typography.titleMedium, color = TcTextPrimary)
                Spacer(Modifier.height(8.dp))
                Text(msg, style = MaterialTheme.typography.bodyMedium, color = TcTextSecondary)
            }
            Spacer(Modifier.height(16.dp))
        }

        // Snapshot data
        violation.snapshotJson?.takeIf { it.isNotEmpty() }?.let { snapshot ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TcCardBg)
                    .padding(16.dp),
            ) {
                Text("Account Snapshot", style = MaterialTheme.typography.titleMedium, color = TcTextPrimary)
                Spacer(Modifier.height(8.dp))
                val currencyKeys = setOf("balance", "equity", "margin", "free_margin")
                snapshot.entries.sortedBy { it.key }.forEach { (key, value) ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(key.replace("_", " ").replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium, color = TcTextSecondary)
                        val formatted = if (key in currencyKeys) NumberFormat.getCurrencyInstance(Locale.US).format(value) else "%.2f".format(value)
                        Text(formatted, style = MaterialTheme.typography.bodyMedium, color = TcTextPrimary)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = TcBorder)
                }
            }
        }
    }
}
