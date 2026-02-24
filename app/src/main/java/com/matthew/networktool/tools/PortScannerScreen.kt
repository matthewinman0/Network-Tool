package com.example.networktool.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Router
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.networktool.settings.AppSettings
import com.example.networktool.ui.ToolScaffold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

data class PortResult(val port: Int, val open: Boolean, val service: String)

val commonServices = mapOf(
    21 to "FTP", 22 to "SSH", 23 to "Telnet", 25 to "SMTP",
    53 to "DNS", 80 to "HTTP", 110 to "POP3", 143 to "IMAP",
    443 to "HTTPS", 445 to "SMB", 3306 to "MySQL", 3389 to "RDP",
    5432 to "PostgreSQL", 6379 to "Redis", 8080 to "HTTP-Alt",
    8443 to "HTTPS-Alt", 27017 to "MongoDB"
)

@Composable
fun PortScannerScreen() {
    var host by remember { mutableStateOf(AppSettings.portScanHost) }
    var startPort by remember { mutableStateOf(AppSettings.portScanStart.toString()) }
    var endPort by remember { mutableStateOf(AppSettings.portScanEnd.toString()) }
    var isScanning by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    val openPorts = remember { mutableStateListOf<PortResult>() }
    var scannedCount by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    ToolScaffold(title = "Port Scanner", icon = Icons.Outlined.Router) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = host,
                onValueChange = { host = it },
                label = { Text("Target Host / IP") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isScanning
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = startPort,
                    onValueChange = { startPort = it },
                    label = { Text("Start Port") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !isScanning
                )
                OutlinedTextField(
                    value = endPort,
                    onValueChange = { endPort = it },
                    label = { Text("End Port") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !isScanning
                )
            }
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    if (!isScanning) {
                        val start = startPort.toIntOrNull() ?: 1
                        val end = endPort.toIntOrNull() ?: 1024
                        isScanning = true
                        openPorts.clear()
                        scannedCount = 0
                        progress = 0f
                        AppSettings.portScanHost = host
                        scope.launch {
                            val total = (end - start + 1).coerceAtLeast(1).toFloat()
                            (start..end).forEach { port ->
                                val open = withContext(Dispatchers.IO) { isPortOpen(host, port, AppSettings.portScanTimeoutMs) }
                                if (open) {
                                    openPorts.add(PortResult(port, true, commonServices[port] ?: "Unknown"))
                                }
                                scannedCount++
                                progress = scannedCount / total
                            }
                            isScanning = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = host.isNotBlank() && !isScanning
            ) {
                Text(if (isScanning) "Scanning… ($scannedCount ports)" else "Start Scan")
            }

            if (isScanning) {
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(12.dp))

            if (!isScanning && scannedCount > 0) {
                Text(
                    "Found ${openPorts.size} open port(s) out of $scannedCount scanned",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(openPorts.sortedBy { it.port }) { result ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${result.port}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                result.service,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.extraSmall
                            ) {
                                Text(
                                    "OPEN",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun isPortOpen(host: String, port: Int, timeoutMs: Int): Boolean {
    return try {
        Socket().use { socket ->
            socket.connect(InetSocketAddress(host, port), timeoutMs)
            true
        }
    } catch (e: Exception) { false }
}
