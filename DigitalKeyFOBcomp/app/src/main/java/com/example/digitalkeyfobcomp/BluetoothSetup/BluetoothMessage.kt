package com.example.digitalkeyfobcomp.BluetoothSetup


// Data class representing a Bluetooth message with message content, sender's name, and origin information
data class BluetoothMessage(
    val message: String,         // Content of the Bluetooth message
    val senderName: String,      // Name of the sender of the message
    val isFromLocalUser: Boolean  // Indicates whether the message is from the local user
)
