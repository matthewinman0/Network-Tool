package com.example.networktool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.networktool.ui.theme.NetworkToolTheme
import com.example.networktool.screens.HomeScreen
import com.example.networktool.screens.SettingsScreen
import com.example.networktool.tools.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NetworkToolTheme {
                NetworkToolApp()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Settings : Screen("settings", "Settings")
    // Tool screens
    object Ping : Screen("tool/ping", "Auto Ping")
    object IpInfo : Screen("tool/ipinfo", "IP Info")
    object DnsLookup : Screen("tool/dns", "DNS Lookup")
    object SimInfo : Screen("tool/sim", "SIM Info")
    object PortScanner : Screen("tool/portscan", "Port Scanner")
    object WifiInfo : Screen("tool/wifi", "Wi-Fi Info")
    object Traceroute : Screen("tool/traceroute", "Traceroute")
    object SpeedTest : Screen("tool/speedtest", "Speed Test")
    object SubnetCalc : Screen("tool/subnet", "Subnet Calc")
    object HttpChecker : Screen("tool/http", "HTTP Checker")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkToolApp() {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        Triple(Screen.Home.route, "Home", Icons.Default.Home),
        Triple(Screen.Settings.route, "Settings", Icons.Default.Settings),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { (route, title, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = title) },
                        label = { Text(title) },
                        selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable(Screen.Ping.route) { PingScreen() }
            composable(Screen.IpInfo.route) { IpInfoScreen() }
            composable(Screen.DnsLookup.route) { DnsLookupScreen() }
            composable(Screen.SimInfo.route) { SimInfoScreen() }
            composable(Screen.PortScanner.route) { PortScannerScreen() }
            composable(Screen.WifiInfo.route) { WifiInfoScreen() }
            composable(Screen.Traceroute.route) { TracerouteScreen() }
            composable(Screen.SpeedTest.route) { SpeedTestScreen() }
            composable(Screen.SubnetCalc.route) { SubnetCalcScreen() }
            composable(Screen.HttpChecker.route) { HttpCheckerScreen() }
        }
    }
}
