package com.matthew.networktool.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(viewModel: MainViewModel) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(viewModel.homeWidgets) { widget ->
            HomeWidgetCard(widget, viewModel)
        }
    }
}

@Composable
fun HomeWidgetCard(widget: HomeWidget, viewModel: MainViewModel) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        onClick = { /* Run tool logic here */ }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(widget.toolId)

            TextButton(
                onClick = { viewModel.removeWidget(widget) }
            ) {
                Text("Remove")
            }
        }
    }
}