package com.rein.tradingcoach.ui.screens.settings

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rein.tradingcoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var showRegenerateDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // New API key sheet
    state.newApiKey?.let { key ->
        AlertDialog(
            onDismissRequest = viewModel::dismissNewApiKey,
            title = { Text("New API Key") },
            text = {
                Text(
                    key,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TcTextPrimary,
                )
            },
            confirmButton = { TextButton(onClick = viewModel::dismissNewApiKey) { Text("Done") } },
        )
    }

    if (showRegenerateDialog) {
        AlertDialog(
            onDismissRequest = { showRegenerateDialog = false },
            title = { Text("Regenerate API Key?") },
            text = { Text("Your MT5 EA will stop working until you update it with the new key.") },
            confirmButton = {
                TextButton(onClick = {
                    showRegenerateDialog = false
                    viewModel.regenerateApiKey()
                }) { Text("Regenerate", color = TcRed) }
            },
            dismissButton = { TextButton(onClick = { showRegenerateDialog = false }) { Text("Cancel") } },
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out?") },
            confirmButton = {
                TextButton(onClick = { viewModel.logout(); onLogout() }) { Text("Sign Out", color = TcRed) }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = TcTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TcTextSecondary)
                    }
                },
                actions = {
                    if (state.isSaving) {
                        CircularProgressIndicator(Modifier.size(20.dp).padding(end = 16.dp), color = TcBlue)
                    }
                },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Prop firm preset
            SettingsSection(title = "PROP FIRM PRESET") {
                val presets = PropFirmPreset.entries
                var expanded by remember { mutableStateOf(false) }
                val currentPreset = presets.find {
                    it.name.equals(state.settings.propFirmPreset, ignoreCase = true)
                } ?: PropFirmPreset.CUSTOM

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = currentPreset.name.replace("_", " "),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        presets.forEach { preset ->
                            DropdownMenuItem(
                                text = { Text(preset.name.replace("_", " ")) },
                                onClick = { viewModel.applyPreset(preset); expanded = false },
                            )
                        }
                    }
                }
            }

            // Risk rules
            val s = state.settings
            SettingsSection(title = "RISK RULES") {
                SliderRow("Max Risk per Trade", "%.1f%%".format(s.maxRiskPct), s.maxRiskPct.toFloat(), 0.5f..10f) {
                    viewModel.updateSettings(s.copy(maxRiskPct = it.toDouble()))
                }
                SliderRow("Max Daily Loss", "%.1f%%".format(s.maxDailyLossPct), s.maxDailyLossPct.toFloat(), 1f..10f) {
                    viewModel.updateSettings(s.copy(maxDailyLossPct = it.toDouble()))
                }
                SliderRow("Max Position Size", "%.2f lots".format(s.maxPositionSizeLots), s.maxPositionSizeLots.toFloat(), 0.01f..10f) {
                    viewModel.updateSettings(s.copy(maxPositionSizeLots = it.toDouble()))
                }
                StepperRow("Max Open Trades", s.maxOpenTrades, 1..20) {
                    viewModel.updateSettings(s.copy(maxOpenTrades = it))
                }
                StepperRow("Revenge Window (min)", s.revengeWindowMinutes, 5..60) {
                    viewModel.updateSettings(s.copy(revengeWindowMinutes = it))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Require Stop Loss", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = s.requireStopLoss, onCheckedChange = { viewModel.updateSettings(s.copy(requireStopLoss = it)) })
                }
            }

            // EA connection
            SettingsSection(title = "EA CONNECTION") {
                Text("API Key", style = MaterialTheme.typography.bodySmall, color = TcTextTertiary)
                Text(
                    state.maskedApiKey,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TcTextPrimary,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { showRegenerateDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Regenerate Key", color = TcRed)
                }
            }

            // Sign out
            Button(
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = TcRedBg, contentColor = TcRed),
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Sign Out") }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.bodySmall, color = TcTextTertiary)
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(TcCardBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content,
        )
    }
}

@Composable
private fun SliderRow(label: String, valueLabel: String, value: Float, range: ClosedFloatingPointRange<Float>, onChange: (Float) -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(valueLabel, style = MaterialTheme.typography.bodyMedium, color = TcBlue)
        }
        Slider(value = value, onValueChange = onChange, valueRange = range, colors = SliderDefaults.colors(thumbColor = TcBlue, activeTrackColor = TcBlue))
    }
}

@Composable
private fun StepperRow(label: String, value: Int, range: IntRange, onChange: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (value > range.first) onChange(value - 1) }) { Text("-") }
            Text("$value", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { if (value < range.last) onChange(value + 1) }) { Text("+") }
        }
    }
}
