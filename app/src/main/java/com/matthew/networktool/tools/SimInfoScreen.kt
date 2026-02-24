package com.example.networktool.tools

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.networktool.ui.InfoCard
import com.example.networktool.ui.ToolScaffold

@Composable
fun SimInfoScreen() {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    ToolScaffold(title = "SIM Info", icon = Icons.Outlined.SimCard) {
        if (!hasPermission) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.SimCard, null, modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                Text("Phone State permission required to read SIM details.",
                    style = MaterialTheme.typography.bodyMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { launcher.launch(Manifest.permission.READ_PHONE_STATE) }) {
                    Text("Grant Permission")
                }
            }
        } else {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simData = buildSimData(tm)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                simData.forEach { (label, value) -> InfoCard(title = label, value = value) }
            }
        }
    }
}

private fun buildSimData(tm: TelephonyManager): List<Pair<String, String>> {
    val simState = when (tm.simState) {
        TelephonyManager.SIM_STATE_ABSENT -> "Absent"
        TelephonyManager.SIM_STATE_READY -> "Ready"
        TelephonyManager.SIM_STATE_NOT_READY -> "Not Ready"
        TelephonyManager.SIM_STATE_NETWORK_LOCKED -> "Network Locked"
        TelephonyManager.SIM_STATE_PIN_REQUIRED -> "PIN Required"
        TelephonyManager.SIM_STATE_PUK_REQUIRED -> "PUK Required"
        else -> "Unknown"
    }
    val networkType = when (tm.dataNetworkType) {
        TelephonyManager.NETWORK_TYPE_LTE -> "4G LTE"
        TelephonyManager.NETWORK_TYPE_NR -> "5G NR"
        TelephonyManager.NETWORK_TYPE_HSPA,
        TelephonyManager.NETWORK_TYPE_HSPAP -> "3G HSPA+"
        TelephonyManager.NETWORK_TYPE_UMTS -> "3G UMTS"
        TelephonyManager.NETWORK_TYPE_EDGE -> "2G EDGE"
        TelephonyManager.NETWORK_TYPE_GPRS -> "2G GPRS"
        else -> "Unknown"
    }
    return listOf(
        "SIM State" to simState,
        "Carrier Name" to (tm.networkOperatorName.ifBlank { "—" }),
        "SIM Operator" to (tm.simOperatorName.ifBlank { "—" }),
        "MCC + MNC" to (tm.networkOperator.ifBlank { "—" }),
        "Country Code" to tm.networkCountryIso.uppercase().ifBlank { "—" },
        "Network Type" to networkType,
        "Roaming" to if (tm.isNetworkRoaming) "Yes" else "No",
        "Phone Type" to when (tm.phoneType) {
            TelephonyManager.PHONE_TYPE_GSM -> "GSM"
            TelephonyManager.PHONE_TYPE_CDMA -> "CDMA"
            TelephonyManager.PHONE_TYPE_SIP -> "SIP"
            else -> "None"
        }
    )
}
