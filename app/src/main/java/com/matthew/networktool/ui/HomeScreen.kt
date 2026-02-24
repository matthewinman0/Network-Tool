package com.example.networktool.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.networktool.Screen

data class ToolCard(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String,
    val color: @Composable () -> Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val tools = listOf(
        ToolCard(
            title = "Auto Ping",
            description = "Continuous host reachability monitor",
            icon = Icons.Outlined.NetworkPing,
            route = Screen.Ping.route,
            color = { MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer }
        ),
        ToolCard(
            title = "IP Info",
            description = "Local & public IP address details",
            icon = Icons.Outlined.Dns,
            route = Screen.IpInfo.route,
            color = { MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer }
        ),
        ToolCard(
            title = "DNS Lookup",
            description = "Resolve hostnames & query DNS records",
            icon = Icons.Outlined.Search,
            route = Screen.DnsLookup.route,
            color = { MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer }
        ),
        ToolCard(
            title = "SIM Info",
            description = "Carrier, IMEI & network details",
            icon = Icons.Outlined.SimCard,
            route = Screen.SimInfo.route,
            color = { MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer }
        ),
        ToolCard(
            title = "Port Scanner",
            description = "Scan TCP ports on any host",
            icon = Icons.Outlined.Router,
            route = Screen.PortScanner.route,
            color = { MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer }
        ),
        ToolCard(
            title = "Wi-Fi Info",
            description = "SSID, signal, frequency & gateway",
            icon = Icons.Outlined.Wifi,
            route = Screen.WifiInfo.route,
            color = { MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer }
        ),
        ToolCard(
            title = "Traceroute",
            description = "Trace network path to any host",
            icon = Icons.Outlined.Timeline,
            route = Screen.Traceroute.route,
            color = { MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer }
        ),
        ToolCard(
            title = "Speed Test",
            description = "Measure download & upload speed",
            icon = Icons.Outlined.Speed,
            route = Screen.SpeedTest.route,
            color = { MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer }
        ),
        ToolCard(
            title = "Subnet Calc",
            description = "CIDR subnet calculations",
            icon = Icons.Outlined.Calculate,
            route = Screen.SubnetCalc.route,
            color = { MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer }
        ),
        ToolCard(
            title = "HTTP Checker",
            description = "Check HTTP status codes & headers",
            icon = Icons.Outlined.Http,
            route = Screen.HttpChecker.route,
            color = { MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer }
        ),
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Network Tool",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Select a tool to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(tools) { tool ->
                ToolGridCard(tool = tool, onClick = { navController.navigate(tool.route) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolGridCard(tool: ToolCard, onClick: () -> Unit) {
    val (containerColor, contentColor) = tool.color()
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.title,
                tint = contentColor,
                modifier = Modifier.size(36.dp)
            )
            Column {
                Text(
                    text = tool.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.75f),
                    maxLines = 2
                )
            }
        }
    }
}
