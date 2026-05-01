package com.rein.tradingcoach.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ReinColorScheme = lightColorScheme(
    primary = TcBlue,
    onPrimary = TcCardBg,
    background = TcPageBg,
    surface = TcCardBg,
    onBackground = TcTextPrimary,
    onSurface = TcTextPrimary,
    error = TcRed,
    outline = TcBorder,
)

@Composable
fun ReinTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ReinColorScheme,
        typography = ReinTypography,
        content = content,
    )
}
