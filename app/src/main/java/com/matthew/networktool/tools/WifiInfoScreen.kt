package com.example.networktool.tools

import android.content.Context
import android.net.wifi.WifiManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.networktool.ui.InfoCard
import com.example.networktool.ui.ToolScaffold
import kotlin.math.roundToInt

@Composable
fun WifiInfoScreen() {
    val context = LocalContext.current
    var wifiData by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var signalBars by remember { mutableIntStateOf(0) }

    fun refresh() {
        @Suppress("DEPRECATION")
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wm.connectionInfo
        val dhcp = wm.dhcpInfo
        val rssi = info.rssi
        signalBars = WifiManager.calculateSignalLevel(rssi, 5)

        val freq = info.frequency
        val band = when {
            freq in 2400..2500 -> "2.4 GHz"
            freq in 4900..5900 -> "5 GHz"
            freq > 5900 -> "6 GHz"
            else -> "Unknown"
        }
        val channel = when {
            freq in 2412..2484 -> ((freq - 2407) / 5).toString()
            freq in 5170..5825 -> ((freq - 5000) / 5).toString()
            else -> "?"
        }
        val ipStr = intToIpv4(info.ipAddress)
        val gatewayStr = intToIpv4(dhcp.gateway)
        val maskStr = intToIpv4(dhcp.netmask)
        val dns1 = intToIpv4(dhcp.dns1)
        val dns2 = intToIpv4(dhcp.dns2)

        @Suppress("DEPRECATION")
        wifiData = listOf(
            "SSID" to (info.ssid?.removeSurrounding("\"") ?: "—"),
            "BSSID" to (info.bssid ?: "—"),
            "IP Address" to ipStr,
            "Gateway" to gatewayStr,
            "Subnet Mask" to maskStr,
            "DNS 1" to dns1,
            "DNS 2" to dns2,
            "Frequency" to "$freq MHz ($band)",
            "Channel" to channel,
            "Signal (RSSI)" to "$rssi dBm",
            "Signal Bars" to "▓".repeat(signalBars) + "░".repeat(5 - signalBars) + " ($signalBars/5)",
            "Link Speed" to "${info.linkSpeed} Mbps",
            "Network ID" to info.networkId.toString()
        )
    }

    LaunchedEffect(Unit) { refresh() }

    ToolScaffold(title = "Wi-Fi Info", icon = Icons.Outlined.Wifi, onRefresh = { refresh() }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Signal strength visual
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Signal Strength", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..5).forEach { bar ->
                            Surface(
                                modifier = Modifier.size(width = 24.dp, height = (bar * 8 + 8).dp),
                                color = if (bar <= signalBars) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.extraSmall
                            ) {}
                        }
                    }
                }
            }
            wifiData.forEach { (label, value) -> InfoCard(title = label, value = value) }
        }
    }
}

@Suppress("DEPRECATION")
private fun intToIpv4(i: Int): String {
    if (i == 0) return "—"
    return "${i and 0xFF}.${(i shr 8) and 0xFF}.${(i shr 16) and 0xFF}.${(i shr 24) and 0xFF}"
}
