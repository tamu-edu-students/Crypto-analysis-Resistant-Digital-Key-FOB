package com.example.digitalkeyfobcomp.BluetoothSetup

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothDeviceDomain


// Extension function to convert a BluetoothDevice to a BluetoothDeviceDomain
@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    // Create and return a BluetoothDeviceDomain using the properties of the BluetoothDevice
    return BluetoothDeviceDomain(
        name = name,        // Assign the name property of the BluetoothDevice
        address = address   // Assign the address property of the BluetoothDevice
    )
}
