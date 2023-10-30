package com.example.digitalkeyfobcomp.BluetoothSetup



typealias BluetoothDeviceDomain = BluetoothDevice

data class BluetoothDevice(
    val name: String?,
    val address: String
)