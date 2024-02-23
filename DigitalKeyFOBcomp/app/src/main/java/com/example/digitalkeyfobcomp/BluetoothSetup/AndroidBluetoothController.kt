package com.example.digitalkeyfobcomp.BluetoothSetup

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothController
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothDeviceDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*


// Suppress lint warning about missing Bluetooth permissions, as they are handled at runtime
@SuppressLint("MissingPermission")
class AndroidBluetoothController(
    private val context: Context
): BluetoothController {

    // BluetoothManager is used to access the BluetoothAdapter
    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    // BluetoothAdapter is used for Bluetooth-related operations
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    // Service for handling Bluetooth data transfer
    private var dataTransferService: BluetoothDataTransferService? = null

    // StateFlow indicating whether the device is currently connected
    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    //new
    private val _isRegistered = MutableStateFlow(false)
    override val isRegistered: StateFlow<Boolean>
        get() = _isRegistered.asStateFlow()

    // StateFlow containing a list of discovered Bluetooth devices
    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    // StateFlow containing a list of paired Bluetooth devices
    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

    // SharedFlow for emitting error messages
    private val _errors = MutableSharedFlow<String>()
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    // BroadcastReceiver for handling discovered devices
    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if(newDevice in devices) devices else devices + newDevice
        }
    }

    // BroadcastReceiver for handling Bluetooth state changes
    private val bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
        // Update the connection state based on Bluetooth state changes
        if(bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
            _isConnected.update { isConnected }
        } else {
            // Emit an error if trying to connect to a non-paired device
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Can't connect to a non-paired device.")
            }
        }
    }

    // Bluetooth server socket and client socket for handling incoming connections
    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    // Initialization block to set up receivers and update paired devices
    init {
        updatePairedDevices()
        // Register the Bluetooth state receiver for relevant Bluetooth events
        context.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
        )
    }

    // Function to start discovering nearby Bluetooth devices
    override fun startDiscovery() {
        // Check if the necessary Bluetooth scan permission is granted
        if(!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        // Register the found device receiver for handling discovered devices
        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )

        // Update the list of paired devices before starting discovery
        updatePairedDevices()

        // Start Bluetooth device discovery
        bluetoothAdapter?.startDiscovery()
    }

    // Function to stop Bluetooth device discovery
    override fun stopDiscovery() {
        // Check if the necessary Bluetooth scan permission is granted
        if(!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        // Cancel ongoing Bluetooth device discovery
        bluetoothAdapter?.cancelDiscovery()
    }

    // Function to start a Bluetooth server and listen for incoming connections
    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow {
            // Check if the necessary Bluetooth connect permission is granted
            if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }

            // Initialize the Bluetooth server socket
            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "chat_service",
                UUID.fromString(SERVICE_UUID)
            )

            // Loop to continuously accept incoming connections
            var shouldLoop = true
            while(shouldLoop) {
                currentClientSocket = try {
                    // Attempt to accept an incoming connection
                    currentServerSocket?.accept()
                } catch(e: IOException) {
                    shouldLoop = false
                    null
                }
                // Emit a connection established result
                emit(ConnectionResult.ConnectionEstablished)
                currentClientSocket?.let {
                    // Close the server socket and initialize the data transfer service
                    currentServerSocket?.close()
                    val service = BluetoothDataTransferService(it)
                    dataTransferService = service

                    // Emit data transfer results from the service
                    emitAll(
                        service
                            .listenForIncomingMessages()
                            .map {
                                ConnectionResult.TransferSucceeded(it)
                            }
                    )
                }
            }
        }.onCompletion {
            // Close the connection when the flow completes
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    // Function to initiate a connection to a remote Bluetooth device
    override fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult> {
        return flow {
            // Check if the necessary Bluetooth connect permission is granted
            if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }

            // Create a Bluetooth socket to the specified remote device
            currentClientSocket = bluetoothAdapter
                ?.getRemoteDevice(device.address)
                ?.createRfcommSocketToServiceRecord(
                    UUID.fromString(SERVICE_UUID)
                )
            // Stop ongoing device discovery
            stopDiscovery()

            currentClientSocket?.let { socket ->
                try {
                    // Attempt to connect to the remote device
                    socket.connect()
                    // Emit a connection established result

                    emit(ConnectionResult.ConnectionEstablished)

                    // Initialize the data transfer service and emit transfer results
                    BluetoothDataTransferService(socket).also {
                        dataTransferService = it
                        emitAll(
                            it.listenForIncomingMessages()
                                .map { ConnectionResult.TransferSucceeded(it) }
                        )
                    }
                } catch(e: IOException) {
                    // Close the socket if the connection was interrupted
                    socket.close()
                    currentClientSocket = null
                    // Emit an error result
                    emit(ConnectionResult.Error("Connection was interrupted"))
                }
            }
        }.onCompletion {
            // Close the connection when the flow completes
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun registerToDevice(device: BluetoothDeviceDomain): Flow<RegistrationResult> {
        return flow {
            // Check if the necessary Bluetooth connect permission is granted
            if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }

            // Create a Bluetooth socket to the specified remote device
            currentClientSocket = bluetoothAdapter
                ?.getRemoteDevice(device.address)
                ?.createRfcommSocketToServiceRecord(
                    UUID.fromString(SERVICE_UUID)
                )
            // Stop ongoing device discovery
            stopDiscovery()

            currentClientSocket?.let { socket ->
                try {
                    // Attempt to connect to the remote device
                    socket.connect()
                    // Emit a connection established result

                    emit(RegistrationResult.RegistrationEstablished)

                    // Initialize the data transfer service and emit transfer results
                    BluetoothDataTransferService(socket).also {
                        dataTransferService = it
                        emitAll(
                            it.listenForIncomingMessages()
                                .map { RegistrationResult.RegistrationSucceeded(it) }
                        )
                    }
                } catch(e: IOException) {
                    // Close the socket if the connection was interrupted
                    socket.close()
                    currentClientSocket = null
                    // Emit an error result
                    emit(RegistrationResult.Error("Registration was interrupted"))
                }
            }
        }.onCompletion {
            // Close the connection when the flow completes
            closeRegistration()
        }.flowOn(Dispatchers.IO)
    }

    // Function to attempt sending a message via Bluetooth
    override suspend fun trySendMessage(message: String): BluetoothMessage? {
        // Check if the necessary Bluetooth connect permission is granted
        if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return null
        }

        // Check if the data transfer service is initialized
        if(dataTransferService == null) {
            return null
        }

        // Create a BluetoothMessage instance
        val bluetoothMessage = BluetoothMessage(
            message = message,
            senderName = bluetoothAdapter?.name ?: "Unknown name",
            isFromLocalUser = true
        )

        // Send the message using the data transfer service
        dataTransferService?.sendMessage(bluetoothMessage.toByteArray())

        return bluetoothMessage
    }

    // Function to close the Bluetooth connection
    override fun closeConnection() {
        // Close the client and server sockets
        currentClientSocket?.close()
        currentServerSocket?.close()
        // Set sockets to null after closing
        currentClientSocket = null
        currentServerSocket = null
    }

    override fun closeRegistration() {
        // Close the client and server sockets
        currentClientSocket?.close()
        currentServerSocket?.close()
        // Set sockets to null after closing
        currentClientSocket = null
        currentServerSocket = null
    }

    // Function to release resources and unregister receivers
    override fun release() {
        // Unregister the found device and Bluetooth state receivers
        context.unregisterReceiver(foundDeviceReceiver)
        context.unregisterReceiver(bluetoothStateReceiver)
        // Close the Bluetooth connection
        closeConnection()
    }

    // Function to update the list of paired devices
    private fun updatePairedDevices() {
        // Check if the necessary Bluetooth connect permission is granted
        if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }
        // Map the bonded devices to BluetoothDeviceDomain and update the paired devices StateFlow
        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

    // Function to check if a specific permission is granted
    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
    // Companion object containing the service UUID for Bluetooth communication
    companion object {
        const val SERVICE_UUID = "00001101-0000-1000-8000-00805F9B34FB"
    }
}
