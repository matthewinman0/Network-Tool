package com.example.networktool.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.networktool.ui.InfoCard
import com.example.networktool.ui.ToolScaffold

@Composable
fun SubnetCalcScreen() {
    var ipInput by remember { mutableStateOf("192.168.1.0") }
    var cidrInput by remember { mutableStateOf("24") }
    var result by remember { mutableStateOf<SubnetResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    ToolScaffold(title = "Subnet Calculator", icon = Icons.Outlined.Calculate) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = ipInput,
                    onValueChange = { ipInput = it },
                    label = { Text("IP Address") },
                    modifier = Modifier.weight(2f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = cidrInput,
                    onValueChange = { cidrInput = it.filter { c -> c.isDigit() } },
                    label = { Text("/ CIDR") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text("/") }
                )
            }

            Button(
                onClick = {
                    error = null
                    result = null
                    try {
                        result = calculateSubnet(ipInput.trim(), cidrInput.trim().toInt())
                    } catch (e: Exception) {
                        error = e.message ?: "Invalid input"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Calculate") }

            error?.let {
                Card(colors = CardDefaults.cardColors(MaterialTheme.colorScheme.errorContainer)) {
                    Text(it, modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }

            result?.let { r ->
                InfoCard("Network Address", r.networkAddress)
                InfoCard("Broadcast Address", r.broadcastAddress)
                InfoCard("Subnet Mask", r.subnetMask)
                InfoCard("Wildcard Mask", r.wildcardMask)
                InfoCard("First Host", r.firstHost)
                InfoCard("Last Host", r.lastHost)
                InfoCard("Total Hosts", "${r.totalHosts}")
                InfoCard("Usable Hosts", "${r.usableHosts}")
                InfoCard("CIDR Notation", "/${r.cidr}")
                InfoCard("IP Class", r.ipClass)
                InfoCard("Type", r.type)
            }
        }
    }
}

data class SubnetResult(
    val networkAddress: String,
    val broadcastAddress: String,
    val subnetMask: String,
    val wildcardMask: String,
    val firstHost: String,
    val lastHost: String,
    val totalHosts: Long,
    val usableHosts: Long,
    val cidr: Int,
    val ipClass: String,
    val type: String
)

private fun calculateSubnet(ipStr: String, cidr: Int): SubnetResult {
    require(cidr in 0..32) { "CIDR must be between 0 and 32" }
    val parts = ipStr.split(".")
    require(parts.size == 4 && parts.all { it.toIntOrNull() in 0..255 }) { "Invalid IP address" }

    val ipInt = parts.fold(0L) { acc, part -> (acc shl 8) or part.toLong() }
    val maskInt = if (cidr == 0) 0L else (0xFFFFFFFFL shl (32 - cidr)) and 0xFFFFFFFFL
    val wildcardInt = maskInt.xor(0xFFFFFFFFL)
    val networkInt = ipInt and maskInt
    val broadcastInt = networkInt or wildcardInt
    val firstHostInt = if (cidr < 31) networkInt + 1 else networkInt
    val lastHostInt = if (cidr < 31) broadcastInt - 1 else broadcastInt
    val total = Math.pow(2.0, (32 - cidr).toDouble()).toLong()
    val usable = if (cidr < 31) (total - 2).coerceAtLeast(0) else total

    val firstOctet = (ipInt shr 24).toInt()
    val ipClass = when {
        firstOctet < 128 -> "A"
        firstOctet < 192 -> "B"
        firstOctet < 224 -> "C"
        firstOctet < 240 -> "D (Multicast)"
        else -> "E (Reserved)"
    }
    val isPrivate = (firstOctet == 10) ||
            (firstOctet == 172 && ((ipInt shr 16) and 0xF) in 16..31) ||
            (firstOctet == 192 && ((ipInt shr 16) and 0xFF) == 168L)
    val type = if (isPrivate) "Private" else "Public"

    return SubnetResult(
        networkAddress = longToIp(networkInt),
        broadcastAddress = longToIp(broadcastInt),
        subnetMask = longToIp(maskInt),
        wildcardMask = longToIp(wildcardInt),
        firstHost = longToIp(firstHostInt),
        lastHost = longToIp(lastHostInt),
        totalHosts = total,
        usableHosts = usable,
        cidr = cidr,
        ipClass = ipClass,
        type = type
    )
}

private fun longToIp(l: Long) =
    "${(l shr 24) and 0xFF}.${(l shr 16) and 0xFF}.${(l shr 8) and 0xFF}.${l and 0xFF}"
