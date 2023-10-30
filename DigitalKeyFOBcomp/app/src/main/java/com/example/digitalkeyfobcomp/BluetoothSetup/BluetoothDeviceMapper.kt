package com.example.digitalkeyfobcomp.BluetoothSetup

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothDeviceDomain


@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = address
    )
}