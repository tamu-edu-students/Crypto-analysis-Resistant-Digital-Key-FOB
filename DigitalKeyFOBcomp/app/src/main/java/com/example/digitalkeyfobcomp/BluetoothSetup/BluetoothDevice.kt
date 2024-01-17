package com.example.digitalkeyfobcomp.BluetoothSetup



// Alias for a BluetoothDevice, representing a Bluetooth device with a name and an address
typealias BluetoothDeviceDomain = BluetoothDevice

// Data class representing a Bluetooth device with a name and an address
data class BluetoothDevice(
    val name: String?,  // Name of the Bluetooth device (can be null)
    val address: String  // Address of the Bluetooth device (unique identifier)
)
