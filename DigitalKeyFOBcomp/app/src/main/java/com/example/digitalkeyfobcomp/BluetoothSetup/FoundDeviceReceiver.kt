package com.example.digitalkeyfobcomp.BluetoothSetup

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

// BroadcastReceiver for handling Bluetooth device discovery events
class FoundDeviceReceiver(
    private val onDeviceFound: (BluetoothDevice) -> Unit
) : BroadcastReceiver() {

    // Callback invoked when a Bluetooth device is found
    override fun onReceive(context: Context?, intent: Intent?) {
        // Check the action of the received intent
        when (intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                // Retrieve the BluetoothDevice object from the received intent
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice::class.java
                    )
                } else {
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }

                // Invoke the callback with the found BluetoothDevice (if not null)
                device?.let(onDeviceFound)
            }
        }
    }
}
