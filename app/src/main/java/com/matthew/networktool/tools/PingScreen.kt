package com.example.networktool.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.networktool.settings.AppSettings
import com.example.networktool.ui.ToolScaffold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.*

data class PingResult(
    val timestamp: String,
    val host: String,
    val latencyMs: Long?,
    val success: Boolean,
    val message: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PingScreen() {
    var target by remember { mutableStateOf(AppSettings.pingAddress) }
    var isRunning by remember { mutableStateOf(false) }
    val results = remember { mutableStateListOf<PingResult>() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var sentCount by remember { mutableIntStateOf(0) }
    var receivedCount by remember { mutableIntStateOf(0) }
    var avgLatency by remember { mutableStateOf(0L) }

    ToolScaffold(title = "Auto Ping", icon = Icons.Outlined.NetworkPing) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            // Input row
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Host / IP") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !isRunning
                )
                Spacer(Modifier.width(8.dp))
                FilledIconButton(
                    onClick = {
                        if (isRunning) {
                            isRunning = false
                        } else {
                            isRunning = true
                            sentCount = 0
                            receivedCount = 0
                            results.clear()
                            scope.launch {
                                AppSettings.pingAddress = target
                                while (isRunning) {
                                    val result = doPing(target, AppSettings.pingTimeoutMs)
                                    sentCount++
                                    if (result.success) {
                                        receivedCount++
                                        val allLatencies = results.mapNotNull { it.latencyMs }
                                        avgLatency = if (allLatencies.isNotEmpty()) allLatencies.average().toLong() else 0L
                                    }
                                    results.add(0, result)
                                    if (results.size > 200) results.removeLast()
                                    listState.animateScrollToItem(0)
                                    delay(AppSettings.pingIntervalSeconds * 1000L)
                                }
                            }
                        }
                    },
                    colors = FilledIconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isRunning) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        if (isRunning) Icons.Outlined.Stop else Icons.Outlined.PlayArrow,
                        contentDescription = if (isRunning) "Stop" else "Start"
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Stats row
            if (sentCount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatChip("Sent", "$sentCount", modifier = Modifier.weight(1f))
                    StatChip("Recv", "$receivedCount", modifier = Modifier.weight(1f),
                        color = if (receivedCount == sentCount) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.errorContainer)
                    StatChip("Loss", "${((sentCount - receivedCount) * 100 / sentCount)}%",
                        modifier = Modifier.weight(1f),
                        color = if (sentCount == receivedCount) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.errorContainer)
                    StatChip("Avg", "${avgLatency}ms", modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
            }

            // Log
            Card(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    reverseLayout = false
                ) {
                    if (results.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    "Press ▶ to start pinging",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    items(results) { result ->
                        PingResultRow(result)
                    }
                }
            }
        }
    }
}

@Composable
fun PingResultRow(result: PingResult) {
    val textColor = if (result.success) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.error
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (AppSettings.pingShowTimestamps) {
            Text(
                result.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.width(56.dp)
            )
        }
        Text(
            result.message,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatChip(label: String, value: String, modifier: Modifier = Modifier,
             color: Color = MaterialTheme.colorScheme.primaryContainer) {
    Surface(
        modifier = modifier,
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

private suspend fun doPing(host: String, timeoutMs: Int): PingResult = withContext(Dispatchers.IO) {
    val timestamp = timeFormat.format(Date())
    return@withContext try {
        val start = System.currentTimeMillis()
        val reachable = InetAddress.getByName(host).isReachable(timeoutMs)
        val latency = System.currentTimeMillis() - start
        if (reachable) {
            PingResult(timestamp, host, latency, true, "Reply from $host: time=${latency}ms")
        } else {
            PingResult(timestamp, host, null, false, "Request timeout for $host")
        }
    } catch (e: Exception) {
        PingResult(timestamp, host, null, false, "Error: ${e.message}")
    }
}
