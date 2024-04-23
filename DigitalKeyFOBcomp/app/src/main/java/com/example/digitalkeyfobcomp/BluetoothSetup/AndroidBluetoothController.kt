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
import android.widget.Toast
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothController
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothDeviceDomain
import com.example.digitalkeyfobcomp.Encryption.DDS
import com.example.digitalkeyfobcomp.Encryption.DHKEAfter
import com.example.digitalkeyfobcomp.Encryption.DHKEBefore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
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
import java.math.BigInteger
import java.security.PublicKey
import java.util.*

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit
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

    private val _usermessage = MutableSharedFlow<String>()
    override val usermessage: SharedFlow<String>
        get() = _usermessage.asSharedFlow()

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
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }

            // Create a Bluetooth socket to the specified remote device
            currentClientSocket = bluetoothAdapter
                ?.getRemoteDevice(device.address)
                ?.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID))

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
                } catch (e: IOException) {
                    // Close the socket if the connection was interrupted
                    socket.close()
                    currentClientSocket = null
                    // Emit an error result
                    emit(ConnectionResult.Error("Connection was interrupted"))
                }
            }
        }.catch { e ->
            if (e is CancellationException && e.cause is TimeoutCancellationException) {
                emit(ConnectionResult.Error("Connection timed out"))
            } else {
                throw e
            }
        }.onCompletion {
            // Close the connection when the flow completes
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

//    override fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult> {
//        return flow {
//            // Check if the necessary Bluetooth connect permission is granted
//            if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
//                throw SecurityException("No BLUETOOTH_CONNECT permission")
//            }
//
//            // Create a Bluetooth socket to the specified remote device
//            currentClientSocket = bluetoothAdapter
//                ?.getRemoteDevice(device.address)
//                ?.createRfcommSocketToServiceRecord(
//                    UUID.fromString(SERVICE_UUID)
//                )
//            // Stop ongoing device discovery
//            stopDiscovery()
//
//            currentClientSocket?.let { socket ->
//                try {
//                    // Attempt to connect to the remote device
//                    socket.connect()
//                    // Emit a connection established result
//
//                    emit(ConnectionResult.ConnectionEstablished)
//
//                    // Initialize the data transfer service and emit transfer results
//                    BluetoothDataTransferService(socket).also {
//                        dataTransferService = it
//                        emitAll(
//                            it.listenForIncomingMessages()
//                                .map { ConnectionResult.TransferSucceeded(it) }
//                        )
//                    }
//                } catch(e: IOException) {
//                    // Close the socket if the connection was interrupted
//                    socket.close()
//                    currentClientSocket = null
//                    // Emit an error result
//                    emit(ConnectionResult.Error("Connection was interrupted"))
//                }
//            }
//        }.onCompletion {
//            // Close the connection when the flow completes
//            closeConnection()
//        }.flowOn(Dispatchers.IO)
//    }

    override fun registerToDevice(device: BluetoothDeviceDomain, additionalData: String): Flow<RegistrationResult> {
        return flow {
            // Generate DHKE values
            val dhkeBeforeResult = DHKEBefore()

            // Check if the necessary Bluetooth connect permission is granted
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
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
                    // Initialize the data transfer service
                    val dataTransferService = BluetoothDataTransferService(socket)

                    // Send the public key generated by DHKE
                    val publicKeyMessage = "DKE" + "${dhkeBeforeResult.first}"
                    dataTransferService.sendMessage(publicKeyMessage.encodeToByteArray())

                    // Listen for incoming messages to receive the response
                    val incomingMessages = dataTransferService.listenForIncomingMessages()

                    // Receive and process the public key from the remote device
                    incomingMessages.collect { message ->
                        // Check if the received message contains the public key
                        val receivedMessage = message.message
                        if (receivedMessage.startsWith("Public Key:")) {
                            // Extract the public key from the message
                            val publicKeyString = receivedMessage.substringAfter("Public Key:").trim()

                            // Remove the decimal part if present
                            val integerPart = publicKeyString.substringBefore(".").trim()

                            // Parse the integer part to BigInteger
                            val receivedPublicKey = BigInteger(integerPart)

                            // Show a toast message with the received public key
                            val sharedKey = DHKEAfter(receivedPublicKey, dhkeBeforeResult.second)

                            val aessend = "AES$additionalData"
                            dataTransferService.sendMessage(aessend.encodeToByteArray())

                            emit(RegistrationResult.Usermessage("Registration Successful"))
                            emit(RegistrationResult.RegistrationEstablished)
                        } else {
                            // Handle unexpected response or key exchange failure
                            emit(RegistrationResult.Usermessage("Registration Failed"))
                            emit(RegistrationResult.RegistrationEstablished)
                        }
                    }
                } catch (e: IOException) {
                    // Emit an error result
                    emit(RegistrationResult.Usermessage("Registration Failed"))
                    emit(RegistrationResult.Error("Registration was ended"))

                } finally {
                    // Close the socket if it's still open
                    if (socket.isConnected) {
                        socket.close()
                    }
                    currentClientSocket = null
                }
            }
        }.onCompletion {
            // Close the connection when the flow completes
            closeRegistration()
        }.flowOn(Dispatchers.IO)
    }

//    override fun registerToDevice(device: BluetoothDeviceDomain,additionalData: String): Flow<RegistrationResult> {
//        return flow {
//            // Generate DHKE values
//            val dhkeBeforeResult = DHKEBefore()
//
//            // Check if the necessary Bluetooth connect permission is granted
//            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
//                throw SecurityException("No BLUETOOTH_CONNECT permission")
//            }
//
//            // Create a Bluetooth socket to the specified remote device
//            currentClientSocket = bluetoothAdapter
//                ?.getRemoteDevice(device.address)
//                ?.createRfcommSocketToServiceRecord(
//                    UUID.fromString(SERVICE_UUID)
//                )
//            // Stop ongoing device discovery
//            stopDiscovery()
//
//            currentClientSocket?.let { socket ->
//                try {
//                    // Attempt to connect to the remote device
//                    socket.connect()
//                    // Initialize the data transfer service
//                    val dataTransferService = BluetoothDataTransferService(socket)
//
//                    // Send the public key generated by DHKE
//                    val publicKeyMessage = "DKE" + "${dhkeBeforeResult.first}"
//                    dataTransferService.sendMessage(publicKeyMessage.encodeToByteArray())
//
//                    // Listen for incoming messages to receive the response
//                    val incomingMessages = dataTransferService.listenForIncomingMessages()
//
//                    // Receive and process the public key from the remote device
//                    incomingMessages.collect { message ->
//                        // Check if the received message contains the public key
//                        val receivedMessage = message.message
//                        if (receivedMessage.startsWith("Public Key:")) {
//                            // Extract the public key from the message
//                            val publicKeyString = receivedMessage.substringAfter("Public Key:").trim()
//
//                            // Remove the decimal part if present
//                            val integerPart = publicKeyString.substringBefore(".").trim()
//
//                            // Parse the integer part to BigInteger
//                            val receivedPublicKey = BigInteger(integerPart)
//
//                            // Show a toast message with the received public key
//                            val sharedKey = DHKEAfter(receivedPublicKey, dhkeBeforeResult.second)
//
//                            val aessend = "AES$additionalData"
//                            dataTransferService.sendMessage(aessend.encodeToByteArray())
//
////                            emit(RegistrationResult.Usermessage("Registration Success"))
//                            emit(RegistrationResult.Error("Registration was Successful"))
//                            emit(RegistrationResult.RegistrationEstablished)
//                            socket.close()
//                            currentClientSocket = null
//                        } else {
//                            // Handle unexpected response or key exchange failure
//                           emit(RegistrationResult.Usermessage("Registration Failed"))
//                        }
//                    }
//                } catch (e: IOException) {
//                    // Close the socket if the connection was interrupted
//                    socket.close()
//                    currentClientSocket = null
//                    // Emit an error result
//                    emit(RegistrationResult.Error("Registration was ended"))
//                }
//            }
//        }.onCompletion {
//            // Close the connection when the flow completes
//            closeRegistration()
//        }.flowOn(Dispatchers.IO)
//    }




    // Function to attempt sending a message via Bluetooth
    override suspend fun trySendMessage(message: String, devicekey: String,  devicename: String): BluetoothMessage? {
        // Check if the necessary Bluetooth connect permission is granted
        if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return null
        }

        // Check if the data transfer service is initialized
        if(dataTransferService == null) {
            return null
        }
        val timestamp = System.currentTimeMillis()

        // Create a BluetoothMessage instance
        val bluetoothMessage = BluetoothMessage(
            message = message,
            senderName = bluetoothAdapter?.name ?: "Unknown name",
            isFromLocalUser = true
        )

        val finalmessage = "COM" + DDS("$devicekey$message$timestamp") + "|"  + message + "|" + timestamp + "|" + devicename
        // Send the message using the data transfer service
        dataTransferService?.sendMessage(finalmessage.toByteArray())

        return bluetoothMessage
    }

    // Function to attempt sending a message via Bluetooth
//    override suspend fun trySendMessage(message: String): BluetoothMessage? {
//        // Check if the necessary Bluetooth connect permission is granted
//        if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
//            return null
//        }
//
//        // Check if the data transfer service is initialized
//        if(dataTransferService == null) {
//            return null
//        }
//
//        // Create a BluetoothMessage instance
//        val bluetoothMessage = BluetoothMessage(
//            message = message,
//            senderName = bluetoothAdapter?.name ?: "Unknown name",
//            isFromLocalUser = true
//        )
//
//        // Send the message using the data transfer service
//        dataTransferService?.sendMessage(bluetoothMessage.toByteArray())
//
//        return bluetoothMessage
//    }



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
