package com.example.digitalkeyfobcomp.BluetoothSetup


import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

// BroadcastReceiver for monitoring Bluetooth state changes
class BluetoothStateReceiver(
    private val onStateChanged: (isConnected: Boolean, BluetoothDevice) -> Unit
) : BroadcastReceiver() {

    // Callback invoked when a Bluetooth state change is received
    override fun onReceive(context: Context?, intent: Intent?) {
        // Get the BluetoothDevice object from the received intent
        val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(
                BluetoothDevice.EXTRA_DEVICE,
                BluetoothDevice::class.java
            )
        } else {
            intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }

        // Check the action of the received intent and notify the callback accordingly
        when (intent?.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                onStateChanged(true, device ?: return)
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                onStateChanged(false, device ?: return)
            }
        }
    }
}
