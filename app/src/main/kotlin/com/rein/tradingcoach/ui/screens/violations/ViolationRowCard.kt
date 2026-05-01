package com.rein.tradingcoach.ui.screens.violations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rein.tradingcoach.data.api.models.ViolationResponse
import com.rein.tradingcoach.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ViolationRowCard(violation: ViolationResponse, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val severityColor = violation.severityColor()
    val severityBg = violation.severityBgColor()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(TcCardBg)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(severityBg),
            contentAlignment = Alignment.Center,
        ) {
            Text("!", color = severityColor, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(violation.typeDisplayName(), style = MaterialTheme.typography.bodyMedium, color = TcTextPrimary)
            violation.coachMessage?.let { msg ->
                Text(
                    text = if (msg.length > 80) msg.take(80) + "…" else msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = TcTextSecondary,
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(violation.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = TcTextTertiary,
            )
            Text(
                text = violation.severity.uppercase(),
                style = MaterialTheme.typography.bodySmall,
                color = severityColor,
            )
        }
    }
}

private fun ViolationResponse.severityColor(): Color = when (severity.lowercase()) {
    "critical" -> TcRed
    "warning" -> TcAmber
    else -> TcYellow
}

private fun ViolationResponse.severityBgColor(): Color = when (severity.lowercase()) {
    "critical" -> TcRedBg
    "warning" -> TcAmberBg
    else -> TcYellowBg
}

private fun ViolationResponse.typeDisplayName(): String = when (violationType) {
    "overleveraging" -> "Over-leveraging"
    "revenge_trading" -> "Revenge Trading"
    "overtrading" -> "Overtrading"
    "missing_stop_loss" -> "Missing Stop Loss"
    "daily_loss_limit" -> "Daily Loss Limit"
    "max_position_size" -> "Max Position Size"
    else -> violationType.replace("_", " ").replaceFirstChar { it.uppercase() }
}
