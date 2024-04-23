package com.example.digitalkeyfobcomp.BluetoothSetup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Base64
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule
import kotlin.experimental.xor


@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    // MutableStateFlow representing the UI state of the Bluetooth-related component
    private val _state = MutableStateFlow(BluetoothUiState())
    // Exposed StateFlow for observing the UI state
    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            messages = if (state.isConnected) state.messages else emptyList()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    // Job to track the device connection operation
    private var deviceConnectionJob: Job? = null

    private var deviceRegistrationJob: Job? = null

    // Initialize the ViewModel
    init {
        // Observe the isConnected property of the BluetoothController and update the UI state
        bluetoothController.isConnected.onEach { isConnected ->
            _state.update { it.copy(isConnected = isConnected) }
        }.launchIn(viewModelScope)

        //new
        bluetoothController.isRegistered.onEach { isRegistered ->
            _state.update { it.copy(isRegistering= isRegistered) }
        }.launchIn(viewModelScope)


        // Observe the errors property of the BluetoothController and update the UI state
        bluetoothController.errors.onEach { error ->
            _state.update { it.copy(errorMessage = error) }
        }.launchIn(viewModelScope)

        bluetoothController.usermessage.onEach { usermessage ->
            _state.update { it.copy(userMessage = usermessage) }
        }.launchIn(viewModelScope)

    }

    // Function to initiate a connection to a Bluetooth device
    fun connectToDevice(device: BluetoothDeviceDomain) {
        _state.update { it.copy(isConnecting = true) }

        // Start a timer to automatically transition out of the connecting state after 10 seconds
//        val timer = Timer()
//        timer.schedule(5000) {
//                // Check the state and show the connection failed message if needed
//                if (!_state.value.isConnected) {
//                    disconnectFromDevice()
//                }
//
//        }
        deviceConnectionJob = bluetoothController
            .connectToDevice(device)
            .listen()
    }

    fun registerToDevice(device: BluetoothDeviceDomain, additionalData: String) {
        _state.update { it.copy(isRegistering = true) }
        val timer1 = Timer()
        timer1.schedule(5000) {
            viewModelScope.launch {
                // Check the state and show the connection failed message if needed
                if (!_state.value.isRegistering) {
                    endRegistration()
                }
            }
        }
        deviceConnectionJob = bluetoothController
            .registerToDevice(device, additionalData)
            .listenForRegistration()
    }

    fun endRegistration() {
        deviceConnectionJob?.cancel()
        bluetoothController.closeConnection()
        _state.update { it.copy(isConnecting = false, isConnected = false, isRegistering = false, isRegistered = false) }
    }

    // Function to disconnect from a Bluetooth device
    fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        bluetoothController.closeConnection()
        _state.update { it.copy(isConnecting = false, isConnected = false) }
    }

    // Function to initiate a Bluetooth server and wait for incoming connections
    fun waitForIncomingConnections() {
        _state.update { it.copy(isConnecting = true) }
        val timer = Timer()
        val timerTask = timer.schedule(10000) {}
        deviceConnectionJob = bluetoothController
            .startBluetoothServer()
            .listen()
    }

    // Function to send a message over Bluetooth
    fun sendMessage(message: String, devicekey: String, devicename: String) {
        viewModelScope.launch {
            val bluetoothMessage = bluetoothController.trySendMessage(message, devicekey, devicename)
            if (bluetoothMessage != null) {
                _state.update { it.copy(messages = it.messages + bluetoothMessage) }
            }
        }
    }



    // Function to start Bluetooth discovery
    fun startScan() {
        bluetoothController.startDiscovery()
    }

    // Function to stop Bluetooth discovery
    fun stopScan() {
        bluetoothController.stopDiscovery()
    }

    // Extension function to listen for connection results and update the UI state accordingly
    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update { it.copy(isConnected = true, isConnecting = false, errorMessage = null) }
                }
                is ConnectionResult.TransferSucceeded -> {
                    _state.update { it.copy(messages = it.messages + result.message, latestMessage = result.message) }
                }
                is ConnectionResult.Error -> {
                    _state.update { it.copy(isConnected = false, isConnecting = false, errorMessage = result.message) }
                }
            }
        }
            .catch { throwable ->
                bluetoothController.closeConnection()
                _state.update { it.copy(isConnected = false, isConnecting = false) }
            }
            .launchIn(viewModelScope)
    }
    private fun Flow<RegistrationResult>.listenForRegistration(): Job {
        return onEach { result ->
            when (result) {
                RegistrationResult.RegistrationEstablished -> {
                    _state.update { it.copy(isRegistered = true, isRegistering = false, errorMessage = null) }
                }
                is RegistrationResult.RegistrationSucceeded -> {
                    _state.update { it.copy(messages = it.messages + result.message, latestMessage = result.message) }
                }
                is RegistrationResult.Error -> {
                    _state.update { it.copy(isRegistered = false, isRegistering = false, errorMessage = result.message) }
                }
                is RegistrationResult.Usermessage -> {
                    _state.update { it.copy(userMessage = result.message) }
                }
            }
        }.catch { throwable ->
            // Handle errors during registration
            _state.update { it.copy(isRegistering = false, isRegistered= false, errorMessage = throwable.message ?: "Unknown error") }
        }.launchIn(viewModelScope)
    }


    // Release resources when the ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }

}
