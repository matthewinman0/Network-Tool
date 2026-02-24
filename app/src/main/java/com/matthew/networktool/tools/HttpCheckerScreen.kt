package com.example.networktool.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Http
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
import java.net.HttpURLConnection
import java.net.URL

data class HttpCheckResult(
    val url: String,
    val statusCode: Int,
    val statusMessage: String,
    val responseTimeMs: Long,
    val contentType: String?,
    val headers: Map<String, List<String>>,
    val redirectUrl: String?
)

@Composable
fun HttpCheckerScreen() {
    var urlInput by remember { mutableStateOf("https://") }
    var isChecking by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<HttpCheckResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    ToolScaffold(title = "HTTP Checker", icon = Icons.Outlined.Http) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    label = { Text("URL") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !isChecking
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        isChecking = true
                        error = null
                        result = null
                        scope.launch {
                            val r = withContext(Dispatchers.IO) { doHttpCheck(urlInput.trim()) }
                            when {
                                r.isSuccess -> result = r.getOrThrow()
                                else -> error = r.exceptionOrNull()?.message ?: "Unknown error"
                            }
                            isChecking = false
                        }
                    },
                    enabled = urlInput.isNotBlank() && !isChecking
                ) { Text("Check") }
            }

            Spacer(Modifier.height(12.dp))

            if (isChecking) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error?.let {
                Card(colors = CardDefaults.cardColors(MaterialTheme.colorScheme.errorContainer)) {
                    Text(it, modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }

            result?.let { r ->
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        // Status code badge
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = statusColor(r.statusCode)
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${r.statusCode}",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(r.statusMessage, style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Text("${r.responseTimeMs}ms", style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }

                    r.contentType?.let { ct ->
                        item { InfoRow("Content-Type", ct) }
                    }
                    r.redirectUrl?.let { redirect ->
                        item { InfoRow("Redirect To", redirect) }
                    }

                    if (AppSettings.httpShowHeaders && r.headers.isNotEmpty()) {
                        item {
                            Text("Response Headers", style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(top = 8.dp))
                        }
                        items(r.headers.entries.toList()) { (key, values) ->
                            if (key.isNotBlank()) {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(key, style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary)
                                        Text(values.joinToString("; "),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text(value, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun statusColor(code: Int) = when (code / 100) {
    2 -> MaterialTheme.colorScheme.primaryContainer
    3 -> MaterialTheme.colorScheme.tertiaryContainer
    4, 5 -> MaterialTheme.colorScheme.errorContainer
    else -> MaterialTheme.colorScheme.secondaryContainer
}

private fun doHttpCheck(rawUrl: String): Result<HttpCheckResult> {
    return try {
        val url = if (rawUrl.startsWith("http")) rawUrl else "https://$rawUrl"
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.instanceFollowRedirects = AppSettings.httpFollowRedirects
        conn.connectTimeout = AppSettings.httpTimeoutSeconds * 1000
        conn.readTimeout = AppSettings.httpTimeoutSeconds * 1000
        val start = System.currentTimeMillis()
        conn.connect()
        val code = conn.responseCode
        val elapsed = System.currentTimeMillis() - start
        val redirectUrl = if (!AppSettings.httpFollowRedirects) conn.getHeaderField("Location") else null
        val result = HttpCheckResult(
            url = url,
            statusCode = code,
            statusMessage = conn.responseMessage ?: httpStatus(code),
            responseTimeMs = elapsed,
            contentType = conn.contentType,
            headers = conn.headerFields ?: emptyMap(),
            redirectUrl = redirectUrl
        )
        conn.disconnect()
        Result.success(result)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

private fun httpStatus(code: Int) = when (code) {
    200 -> "OK"; 201 -> "Created"; 204 -> "No Content"
    301 -> "Moved Permanently"; 302 -> "Found"; 304 -> "Not Modified"
    400 -> "Bad Request"; 401 -> "Unauthorized"; 403 -> "Forbidden"; 404 -> "Not Found"
    500 -> "Internal Server Error"; 502 -> "Bad Gateway"; 503 -> "Service Unavailable"
    else -> "Unknown"
}
