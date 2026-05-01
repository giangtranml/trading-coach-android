package com.rein.tradingcoach.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.rein.tradingcoach.data.api.models.DashboardResponse
import com.rein.tradingcoach.ui.theme.*
import java.text.NumberFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun AccountCard(dashboard: DashboardResponse, modifier: Modifier = Modifier) {
    val connectionStatus = dashboard.lastUpdatedAt?.connectionStatus() ?: ConnectionStatus.OFFLINE

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TcCardBg)
            .padding(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MetricCell("Balance", dashboard.balance.formatCurrency())
            MetricCell("Equity", dashboard.equity.formatCurrency())
            MetricCell("Margin", dashboard.margin.formatCurrency())
            MetricCell("Free Margin", dashboard.freeMargin.formatCurrency())
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Open Positions: ${dashboard.openPositions}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = connectionStatus.label,
                color = connectionStatus.color,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun MetricCell(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TcTextTertiary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = TcTextPrimary)
    }
}

private enum class ConnectionStatus(val label: String, val color: androidx.compose.ui.graphics.Color) {
    LIVE("● Live", TcGreen),
    STALE("● Stale", TcAmber),
    OFFLINE("● Offline", TcRed),
}

private fun Date.connectionStatus(): ConnectionStatus {
    val ageMinutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - time)
    return when {
        ageMinutes < 5 -> ConnectionStatus.LIVE
        ageMinutes < 15 -> ConnectionStatus.STALE
        else -> ConnectionStatus.OFFLINE
    }
}

private fun Double.formatCurrency(): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(this)
