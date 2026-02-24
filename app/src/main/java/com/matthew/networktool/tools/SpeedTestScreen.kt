package com.example.networktool.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.networktool.ui.ToolScaffold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL
import kotlin.math.roundToInt

@Composable
fun SpeedTestScreen() {
    var isTesting by remember { mutableStateOf(false) }
    var downloadMbps by remember { mutableStateOf<Double?>(null) }
    var uploadMbps by remember { mutableStateOf<Double?>(null) }
    var pingMs by remember { mutableStateOf<Long?>(null) }
    var status by remember { mutableStateOf("Press start to begin") }
    var progress by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    ToolScaffold(title = "Speed Test", icon = Icons.Outlined.Speed) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Speed gauges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SpeedGauge("Download", downloadMbps, modifier = Modifier.weight(1f))
                SpeedGauge("Upload", uploadMbps, modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            // Ping
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ping", style = MaterialTheme.typography.titleSmall)
                    Text(
                        if (pingMs != null) "${pingMs}ms" else "—",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            pingMs == null -> MaterialTheme.colorScheme.onSurfaceVariant
                            pingMs!! < 30 -> MaterialTheme.colorScheme.primary
                            pingMs!! < 80 -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(status, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (isTesting) {
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    isTesting = true
                    downloadMbps = null
                    uploadMbps = null
                    pingMs = null
                    progress = 0f
                    scope.launch {
                        // Ping phase
                        status = "Measuring ping…"
                        pingMs = measurePing("https://www.google.com")
                        progress = 0.3f

                        // Download phase
                        status = "Testing download…"
                        downloadMbps = measureDownload()
                        progress = 0.7f

                        // Upload phase (simulated via POST to httpbin)
                        status = "Testing upload…"
                        uploadMbps = measureUpload()
                        progress = 1f

                        status = "Complete"
                        isTesting = false
                    }
                },
                enabled = !isTesting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isTesting) "Testing…" else "Start Speed Test")
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "Test uses ~10 MB of data",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SpeedGauge(label: String, speedMbps: Double?, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(
                if (speedMbps != null) "${(speedMbps * 10).roundToInt() / 10.0}" else "—",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            Text("Mbps", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private suspend fun measurePing(url: String): Long = withContext(Dispatchers.IO) {
    return@withContext try {
        val start = System.currentTimeMillis()
        URL(url).openConnection().apply { connectTimeout = 5000; connect() }
        System.currentTimeMillis() - start
    } catch (e: Exception) { -1L }
}

private suspend fun measureDownload(): Double = withContext(Dispatchers.IO) {
    // ~10MB file from a CDN (Cloudflare speed test)
    return@withContext try {
        val testUrl = "https://speed.cloudflare.com/__down?bytes=10000000"
        val start = System.currentTimeMillis()
        val conn = URL(testUrl).openConnection().apply { connectTimeout = 10000 }
        conn.connect()
        val stream: InputStream = conn.getInputStream()
        var totalBytes = 0L
        val buf = ByteArray(8192)
        var read: Int
        while (stream.read(buf).also { read = it } != -1) { totalBytes += read }
        stream.close()
        val elapsed = (System.currentTimeMillis() - start) / 1000.0
        if (elapsed > 0) (totalBytes * 8 / 1_000_000.0) / elapsed else 0.0
    } catch (e: Exception) { 0.0 }
}

private suspend fun measureUpload(): Double = withContext(Dispatchers.IO) {
    return@withContext try {
        val uploadData = ByteArray(5_000_000) // 5 MB
        val conn = URL("https://speed.cloudflare.com/__up").openConnection()
            as java.net.HttpURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.setRequestProperty("Content-Length", uploadData.size.toString())
        conn.connectTimeout = 10000
        val start = System.currentTimeMillis()
        conn.outputStream.use { it.write(uploadData) }
        conn.responseCode // wait for response
        val elapsed = (System.currentTimeMillis() - start) / 1000.0
        if (elapsed > 0) (uploadData.size * 8 / 1_000_000.0) / elapsed else 0.0
    } catch (e: Exception) { 0.0 }
}
