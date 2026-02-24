package com.matthew.networktool.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val availableTools = listOf(
        ToolDefinition("ping", "Ping"),
        ToolDefinition("ip", "IP Info"),
        ToolDefinition("wifi", "WiFi Toggle"),
        ToolDefinition("dns", "DNS Lookup")
    )

    val homeWidgets = mutableStateListOf<HomeWidget>()

    fun addWidget(tool: ToolDefinition, config: Map<String, String>) {
        homeWidgets.add(
            HomeWidget(
                toolId = tool.id,
                config = config
            )
        )
    }

    fun removeWidget(widget: HomeWidget) {
        homeWidgets.remove(widget)
    }
}