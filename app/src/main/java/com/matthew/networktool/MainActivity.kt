package com.matthew.networktool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.matthew.networktool.ui.HomeScreen
import com.matthew.networktool.ui.ToolsScreen
import com.matthew.networktool.ui.SettingsScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {

    var selectedRoute by remember { mutableStateOf("home") }

    val items = listOf(
        BottomItem("home", "Home", Icons.Default.Home),
        BottomItem("tools", "Tools", Icons.Default.Build),
        BottomItem("settings", "Settings", Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = selectedRoute == item.route,
                        onClick = { selectedRoute = item.route },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->

        Box(modifier = Modifier.padding(padding)) {
            when (selectedRoute) {
                "home" -> HomeScreen()
                "tools" -> ToolsScreen()
                "settings" -> SettingsScreen()
            }
        }
    }
}

data class BottomItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
