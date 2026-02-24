package com.example.networktool.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Simple in-memory settings object backed by Compose state.
 * For persistence across app restarts, wire these into DataStore
 * (see README for instructions).
 */
object AppSettings {

    // ── Ping ────────────────────────────────────────────────────────────────
    var pingAddress by mutableStateOf("8.8.8.8")
    var pingIntervalSeconds by mutableIntStateOf(5)
    var pingTimeoutMs by mutableIntStateOf(2000)
    var pingShowTimestamps by mutableStateOf(true)
    var pingSoundOnFailure by mutableStateOf(false)

    // ── Port Scanner ────────────────────────────────────────────────────────
    var portScanHost by mutableStateOf("192.168.1.1")
    var portScanStart by mutableIntStateOf(1)
    var portScanEnd by mutableIntStateOf(1024)
    var portScanTimeoutMs by mutableIntStateOf(500)

    // ── DNS Lookup ──────────────────────────────────────────────────────────
    var dnsServer by mutableStateOf("")           // blank = use system DNS
    var dnsQueryAll by mutableStateOf(false)

    // ── HTTP Checker ────────────────────────────────────────────────────────
    var httpTimeoutSeconds by mutableIntStateOf(15)
    var httpFollowRedirects by mutableStateOf(true)
    var httpShowHeaders by mutableStateOf(false)

    // ── Traceroute ──────────────────────────────────────────────────────────
    var tracerouteMaxHops by mutableIntStateOf(30)

    // ── Appearance ──────────────────────────────────────────────────────────
    var dynamicColour by mutableStateOf(true)
    var darkTheme by mutableStateOf(false)
}
