package com.example.networktool.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timeline
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
import java.net.InetAddress

data class TracerouteHop(
    val hopNumber: Int,
    val address: String,
    val hostname: String?,
    val latencyMs: Long?
)

@Composable
fun TracerouteScreen() {
    var target by remember { mutableStateOf("google.com") }
    var isRunning by remember { mutableStateOf(false) }
    val hops = remember { mutableStateListOf<TracerouteHop>() }
    val scope = rememberCoroutineScope()

    ToolScaffold(title = "Traceroute", icon = Icons.Outlined.Timeline) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Target Host") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !isRunning
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        isRunning = true
                        hops.clear()
                        scope.launch {
                            runTraceroute(target, AppSettings.tracerouteMaxHops) { hop ->
                                hops.add(hop)
                            }
                            isRunning = false
                        }
                    },
                    enabled = target.isNotBlank() && !isRunning
                ) {
                    Text("Trace")
                }
            }

            Spacer(Modifier.height(12.dp))

            if (isRunning) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Tracing route…", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(8.dp))
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(hops) { hop ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.extraSmall,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "${hop.hopNumber}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(hop.address, style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace)
                                hop.hostname?.let { hn ->
                                    Text(hn, style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text(
                                if (hop.latencyMs != null) "${hop.latencyMs}ms" else "* * *",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = when {
                                    hop.latencyMs == null -> MaterialTheme.colorScheme.onSurfaceVariant
                                    hop.latencyMs < 50 -> MaterialTheme.colorScheme.primary
                                    hop.latencyMs < 150 -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.error
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private suspend fun runTraceroute(
    host: String,
    maxHops: Int,
    onHop: (TracerouteHop) -> Unit
) = withContext(Dispatchers.IO) {
    // Android doesn't support raw ICMP sockets without root, so we use a TCP-based approach
    // or simply attempt InetAddress reachability at simulated hops. Real traceroute on Android
    // typically requires ProcessBuilder with system "traceroute" binary (available on most devices).
    try {
        val process = ProcessBuilder("traceroute", "-m", "$maxHops", "-w", "2", host)
            .redirectErrorStream(true)
            .start()
        process.inputStream.bufferedReader().forEachLine { line ->
            val hop = parseTracerouteLine(line)
            if (hop != null) onHop(hop)
        }
        process.waitFor()
    } catch (e: Exception) {
        // Fallback: resolve and show destination
        try {
            val addr = InetAddress.getByName(host)
            onHop(TracerouteHop(1, addr.hostAddress ?: host, addr.canonicalHostName, null))
        } catch (ex: Exception) {
            onHop(TracerouteHop(1, "Error: ${ex.message}", null, null))
        }
    }
}

private val hopRegex = Regex("""^\s*(\d+)\s+([\d.]+|\*)\s+(?:([\d.]+)\s+ms)?""")

private fun parseTracerouteLine(line: String): TracerouteHop? {
    val match = hopRegex.find(line) ?: return null
    val hop = match.groupValues[1].toIntOrNull() ?: return null
    val addr = match.groupValues[2]
    val latency = match.groupValues[3].toLongOrNull()
    return TracerouteHop(hop, addr, null, latency)
}
