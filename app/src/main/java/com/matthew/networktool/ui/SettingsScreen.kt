package com.example.networktool.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.networktool.settings.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Header
        Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Configure tools and preferences",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // ── PING SETTINGS ──────────────────────────────────────────────
            SettingsSectionCard(title = "Auto Ping", icon = Icons.Outlined.NetworkPing) {
                SettingsTextField(
                    label = "Ping Address",
                    value = AppSettings.pingAddress,
                    onValueChange = { AppSettings.pingAddress = it },
                    placeholder = "e.g. 8.8.8.8 or google.com"
                )
                Spacer(Modifier.height(8.dp))
                SettingsSlider(
                    label = "Ping Interval",
                    value = AppSettings.pingIntervalSeconds.toFloat(),
                    valueRange = 1f..60f,
                    steps = 58,
                    onValueChange = { AppSettings.pingIntervalSeconds = it.toInt() },
                    valueLabel = "${AppSettings.pingIntervalSeconds}s"
                )
                Spacer(Modifier.height(8.dp))
                SettingsSlider(
                    label = "Ping Timeout",
                    value = AppSettings.pingTimeoutMs.toFloat(),
                    valueRange = 500f..10000f,
                    steps = 19,
                    onValueChange = { AppSettings.pingTimeoutMs = it.toInt() },
                    valueLabel = "${AppSettings.pingTimeoutMs}ms"
                )
                Spacer(Modifier.height(8.dp))
                SettingsSwitch(
                    label = "Show timestamps",
                    checked = AppSettings.pingShowTimestamps,
                    onCheckedChange = { AppSettings.pingShowTimestamps = it }
                )
                SettingsSwitch(
                    label = "Sound on failure",
                    checked = AppSettings.pingSoundOnFailure,
                    onCheckedChange = { AppSettings.pingSoundOnFailure = it }
                )
            }

            // ── PORT SCANNER ───────────────────────────────────────────────
            SettingsSectionCard(title = "Port Scanner", icon = Icons.Outlined.Router) {
                SettingsTextField(
                    label = "Default Host",
                    value = AppSettings.portScanHost,
                    onValueChange = { AppSettings.portScanHost = it },
                    placeholder = "e.g. 192.168.1.1"
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsTextField(
                        label = "Start Port",
                        value = AppSettings.portScanStart.toString(),
                        onValueChange = { AppSettings.portScanStart = it.toIntOrNull() ?: 1 },
                        placeholder = "1",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    )
                    SettingsTextField(
                        label = "End Port",
                        value = AppSettings.portScanEnd.toString(),
                        onValueChange = { AppSettings.portScanEnd = it.toIntOrNull() ?: 1024 },
                        placeholder = "1024",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    )
                }
                Spacer(Modifier.height(8.dp))
                SettingsSlider(
                    label = "Timeout per port",
                    value = AppSettings.portScanTimeoutMs.toFloat(),
                    valueRange = 100f..3000f,
                    steps = 29,
                    onValueChange = { AppSettings.portScanTimeoutMs = it.toInt() },
                    valueLabel = "${AppSettings.portScanTimeoutMs}ms"
                )
            }

            // ── DNS LOOKUP ─────────────────────────────────────────────────
            SettingsSectionCard(title = "DNS Lookup", icon = Icons.Outlined.Search) {
                SettingsTextField(
                    label = "Custom DNS Server",
                    value = AppSettings.dnsServer,
                    onValueChange = { AppSettings.dnsServer = it },
                    placeholder = "e.g. 1.1.1.1 (blank = system)"
                )
                Spacer(Modifier.height(8.dp))
                SettingsSwitch(
                    label = "Query all record types",
                    checked = AppSettings.dnsQueryAll,
                    onCheckedChange = { AppSettings.dnsQueryAll = it }
                )
            }

            // ── HTTP CHECKER ───────────────────────────────────────────────
            SettingsSectionCard(title = "HTTP Checker", icon = Icons.Outlined.Http) {
                SettingsSlider(
                    label = "Connection Timeout",
                    value = AppSettings.httpTimeoutSeconds.toFloat(),
                    valueRange = 5f..60f,
                    steps = 10,
                    onValueChange = { AppSettings.httpTimeoutSeconds = it.toInt() },
                    valueLabel = "${AppSettings.httpTimeoutSeconds}s"
                )
                Spacer(Modifier.height(8.dp))
                SettingsSwitch(
                    label = "Follow redirects",
                    checked = AppSettings.httpFollowRedirects,
                    onCheckedChange = { AppSettings.httpFollowRedirects = it }
                )
                SettingsSwitch(
                    label = "Show response headers",
                    checked = AppSettings.httpShowHeaders,
                    onCheckedChange = { AppSettings.httpShowHeaders = it }
                )
            }

            // ── TRACEROUTE ─────────────────────────────────────────────────
            SettingsSectionCard(title = "Traceroute", icon = Icons.Outlined.Timeline) {
                SettingsSlider(
                    label = "Max hops",
                    value = AppSettings.tracerouteMaxHops.toFloat(),
                    valueRange = 5f..64f,
                    steps = 58,
                    onValueChange = { AppSettings.tracerouteMaxHops = it.toInt() },
                    valueLabel = "${AppSettings.tracerouteMaxHops}"
                )
            }

            // ── APP APPEARANCE ─────────────────────────────────────────────
            SettingsSectionCard(title = "Appearance", icon = Icons.Outlined.Palette) {
                SettingsSwitch(
                    label = "Dynamic colour (Material You)",
                    checked = AppSettings.dynamicColour,
                    onCheckedChange = { AppSettings.dynamicColour = it }
                )
                SettingsSwitch(
                    label = "Dark theme",
                    checked = AppSettings.darkTheme,
                    onCheckedChange = { AppSettings.darkTheme = it }
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Reusable setting composables ────────────────────────────────────────────

@Composable
fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))
            content()
        }
    }
}

@Composable
fun SettingsTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

@Composable
fun SettingsSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
    valueLabel: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = valueLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps
        )
    }
}

@Composable
fun SettingsSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
