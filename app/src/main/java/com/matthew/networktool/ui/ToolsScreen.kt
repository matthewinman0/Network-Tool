package com.matthew.networktool.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

data class NetworkTool(
    val name: String,
    val action: (Context) -> Unit
)

@Composable
fun ToolsScreen() {
    val context = LocalContext.current

    val tools = listOf(
        NetworkTool("WiFi Settings") {
            context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        },
        NetworkTool("Mobile Data Settings") {
            context.startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
        },
        NetworkTool("Open Router (192.168.1.1)") {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.1.1"))
            context.startActivity(intent)
        },
        NetworkTool("IP Info (Browser)") {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://whatismyipaddress.com"))
            context.startActivity(intent)
        },
        NetworkTool("Ping Google (Demo)") {
            Toast.makeText(context, "Pinging google.com...", Toast.LENGTH_SHORT).show()
        },
        NetworkTool("DNS Settings") {
            context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tools) { tool ->
            ToolCard(tool, context)
        }
    }
}

@Composable
fun ToolCard(tool: NetworkTool, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        onClick = { tool.action(context) }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = tool.name,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}