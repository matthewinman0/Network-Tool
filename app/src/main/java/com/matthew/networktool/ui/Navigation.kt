package com.matthew.networktool.ui

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home)
    object Tools : BottomNavItem("tools", "Tools", Icons.Filled.Build)
    object Settings : BottomNavItem("settings", "Settings", Icons.Filled.Settings)
}
