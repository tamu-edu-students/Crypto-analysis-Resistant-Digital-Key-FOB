package com.example.digitalkeyfobcomp.BluetoothSetup


data class BluetoothMessage(
    val message: String,
    val senderName: String,
    val isFromLocalUser: Boolean
)