package com.matthew.networktool.ui

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AppNavigation(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf("Home", "Tools", "Settings")

    NavigationBar {
        items.forEachIndexed { index, label ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                label = { Text(label) },
                icon = { }
            )
        }
    }
}