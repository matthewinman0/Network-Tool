package com.matthew.networktool.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ToolsScreen(viewModel: MainViewModel) {

    var selectedTool by remember { mutableStateOf<ToolDefinition?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(viewModel.availableTools) { tool ->
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(tool.name)
                    Button(onClick = { selectedTool = tool }) {
                        Text("Add to Home")
                    }
                }
            }
        }
    }

    selectedTool?.let { tool ->
        AddToHomeDialog(
            tool = tool,
            onDismiss = { selectedTool = null },
            onConfirm = { config ->
                viewModel.addWidget(tool, config)
                selectedTool = null
            }
        )
    }
}