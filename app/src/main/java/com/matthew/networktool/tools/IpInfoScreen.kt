package com.example.networktool.tools

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.networktool.ui.InfoCard
import com.example.networktool.ui.ToolScaffold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.URL

@Composable
fun IpInfoScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var localIp by remember { mutableStateOf("—") }
    var publicIp by remember { mutableStateOf("Loading…") }
    var ipv6 by remember { mutableStateOf("—") }
    var gatewayIp by remember { mutableStateOf("—") }
    var networkType by remember { mutableStateOf("—") }
    var isLoading by remember { mutableStateOf(false) }

    fun refresh() {
        isLoading = true
        localIp = getLocalIpAddress() ?: "—"
        ipv6 = getLocalIpv6Address() ?: "—"
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nc = cm.getNetworkCapabilities(cm.activeNetwork)
        networkType = when {
            nc?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "Wi-Fi"
            nc?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Cellular"
            nc?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
            else -> "Unknown"
        }
        @Suppress("DEPRECATION")
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val gatewayInt = wm.dhcpInfo.gateway
        gatewayIp = intToIp(gatewayInt)

        scope.launch {
            publicIp = withContext(Dispatchers.IO) {
                try { URL("https://api.ipify.org").readText().trim() }
                catch (e: Exception) { "Unavailable" }
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    ToolScaffold(
        title = "IP Info",
        icon = Icons.Outlined.Dns,
        onRefresh = { refresh() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(title = "Local IPv4", value = localIp)
            InfoCard(title = "Public IP", value = if (isLoading) "Loading…" else publicIp)
            InfoCard(title = "Local IPv6", value = ipv6)
            InfoCard(title = "Default Gateway", value = gatewayIp)
            InfoCard(title = "Connection Type", value = networkType)
        }
    }
}

private fun getLocalIpAddress(): String? {
    return try {
        NetworkInterface.getNetworkInterfaces().toList().flatMap { intf ->
            intf.inetAddresses.toList()
        }.firstOrNull { addr ->
            !addr.isLoopbackAddress && addr.hostAddress?.contains(':') == false
        }?.hostAddress
    } catch (e: Exception) { null }
}

private fun getLocalIpv6Address(): String? {
    return try {
        NetworkInterface.getNetworkInterfaces().toList().flatMap { intf ->
            intf.inetAddresses.toList()
        }.firstOrNull { addr ->
            !addr.isLoopbackAddress && addr.hostAddress?.contains(':') == true
        }?.hostAddress
    } catch (e: Exception) { null }
}

private fun intToIp(i: Int): String {
    return "${i and 0xFF}.${(i shr 8) and 0xFF}.${(i shr 16) and 0xFF}.${(i shr 24) and 0xFF}"
}
