package com.example.digitalkeyfobcomp.BluetoothSetup


// Data class representing the UI state of a Bluetooth-related component
data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),   // List of scanned Bluetooth devices
    val pairedDevices: List<BluetoothDevice> = emptyList(),    // List of paired Bluetooth devices
    val isConnected: Boolean = false,                          // Indicates whether a Bluetooth connection is established
    val isConnecting: Boolean = false,                         // Indicates whether a Bluetooth connection is in progress
    val errorMessage: String? = null,                          // Error message, if any
    val messages: List<BluetoothMessage> = emptyList(),        // List of Bluetooth messages
    val latestMessage: BluetoothMessage? = null                // The latest received Bluetooth message
)
