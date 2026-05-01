package com.rein.tradingcoach.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rein.tradingcoach.ui.theme.*

@Composable
fun DisciplineScoreRing(score: Int, modifier: Modifier = Modifier) {
    val ringColor = when {
        score > 70 -> TcGreen
        score >= 40 -> TcAmber
        else -> TcRed
    }
    val label = when {
        score > 70 -> "Excellent"
        score >= 40 -> "Fair"
        else -> "Poor"
    }

    Box(modifier = modifier.size(140.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            val startAngle = -90f
            val sweepAngle = 360f * (score / 100f)

            // Track
            drawArc(color = TcBorder, startAngle = startAngle, sweepAngle = 360f, useCenter = false, style = stroke)
            // Progress
            drawArc(color = ringColor, startAngle = startAngle, sweepAngle = sweepAngle, useCenter = false, style = stroke)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$score", fontSize = 32.sp, color = ringColor, style = MaterialTheme.typography.titleLarge)
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = TcTextSecondary)
        }
    }
}
