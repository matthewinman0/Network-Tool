package com.example.networktool.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.networktool.settings.AppSettings
import com.example.networktool.ui.ToolScaffold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress

data class DnsRecord(val type: String, val value: String)

@Composable
fun DnsLookupScreen() {
    var query by remember { mutableStateOf("") }
    var records by remember { mutableStateOf<List<DnsRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    ToolScaffold(title = "DNS Lookup", icon = Icons.Outlined.Search) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Hostname or domain") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        isLoading = true
                        error = null
                        records = emptyList()
                        scope.launch {
                            val result = withContext(Dispatchers.IO) { resolveDns(query) }
                            when {
                                result.isSuccess -> records = result.getOrThrow()
                                else -> error = result.exceptionOrNull()?.message
                            }
                            isLoading = false
                        }
                    },
                    enabled = query.isNotBlank() && !isLoading
                ) {
                    Text("Lookup")
                }
            }

            Spacer(Modifier.height(12.dp))

            if (isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error?.let {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(
                        it,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(records) { record ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.extraSmall
                            ) {
                                Text(
                                    record.type,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                record.value,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

private suspend fun resolveDns(hostname: String): Result<List<DnsRecord>> = withContext(Dispatchers.IO) {
    return@withContext try {
        val addresses = InetAddress.getAllByName(hostname)
        val records = addresses.map { addr ->
            val type = if (addr.hostAddress?.contains(':') == true) "AAAA" else "A"
            DnsRecord(type, addr.hostAddress ?: "")
        }.toMutableList()
        // Reverse lookup
        addresses.firstOrNull()?.let { addr ->
            val reverse = addr.canonicalHostName
            if (reverse != addr.hostAddress) {
                records.add(DnsRecord("PTR", reverse))
            }
        }
        Result.success(records)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
