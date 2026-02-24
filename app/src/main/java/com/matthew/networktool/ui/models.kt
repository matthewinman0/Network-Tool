package com.matthew.networktool.ui

import java.util.UUID

data class ToolDefinition(
    val id: String,
    val name: String,
    val defaultConfig: Map<String, String> = emptyMap()
)

data class HomeWidget(
    val instanceId: String = UUID.randomUUID().toString(),
    val toolId: String,
    val config: Map<String, String>
)