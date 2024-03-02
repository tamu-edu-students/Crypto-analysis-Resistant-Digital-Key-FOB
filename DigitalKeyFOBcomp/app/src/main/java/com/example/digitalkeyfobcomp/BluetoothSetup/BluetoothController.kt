package com.example.digitalkeyfobcomp.BluetoothSetup


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow



// Interface representing a Bluetooth controller for managing Bluetooth connections and communication
interface BluetoothController {

    // StateFlow indicating whether the device is currently connected
    val isConnected: StateFlow<Boolean>

    // StateFlow indicating whether the device is currently registered
    val isRegistered: StateFlow<Boolean>

    // StateFlow containing a list of discovered Bluetooth devices
    val scannedDevices: StateFlow<List<BluetoothDevice>>

    // StateFlow containing a list of paired Bluetooth devices
    val pairedDevices: StateFlow<List<BluetoothDevice>>

    // SharedFlow for emitting error messages
    val errors: SharedFlow<String>

    val usermessage: SharedFlow<String>

    // Function to start discovering nearby Bluetooth devices
    fun startDiscovery()

    // Function to stop Bluetooth device discovery
    fun stopDiscovery()

    // Function to start a Bluetooth server and listen for incoming connections
    fun startBluetoothServer(): Flow<ConnectionResult>

    // Function to initiate a connection to a remote Bluetooth device
    fun connectToDevice(device: BluetoothDevice): Flow<ConnectionResult>


    // Function to initiate a registration connection to a remote Bluetooth device
    fun registerToDevice(device: BluetoothDevice): Flow<RegistrationResult> //new

    // Function to attempt sending a message via Bluetooth
    suspend fun trySendMessage(message: String): BluetoothMessage?

    // Function to close the Bluetooth connection
    fun closeConnection()

    fun closeRegistration() // new

    // Function to release resources and perform cleanup
    fun release()
}
