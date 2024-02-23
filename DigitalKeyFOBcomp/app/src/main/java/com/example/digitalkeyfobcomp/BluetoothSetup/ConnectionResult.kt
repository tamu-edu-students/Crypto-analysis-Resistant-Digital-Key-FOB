package com.example.digitalkeyfobcomp.BluetoothSetup

// Sealed interface representing different outcomes of a Bluetooth connection
sealed interface ConnectionResult {

    // Sealed class indicating that a connection has been successfully established
    object ConnectionEstablished : ConnectionResult

    // Sealed class indicating that a data transfer was successful, carrying the transferred BluetoothMessage
    data class TransferSucceeded(val message: BluetoothMessage) : ConnectionResult

    // Sealed class indicating an error during the Bluetooth connection, carrying an error message
    data class Error(val message: String) : ConnectionResult
}

sealed interface RegistrationResult {

    // Sealed class indicating that a connection has been successfully established
    object RegistrationEstablished : RegistrationResult

    // Sealed class indicating that a data transfer was successful, carrying the transferred BluetoothMessage
    data class RegistrationSucceeded(val message: BluetoothMessage) : RegistrationResult

    // Sealed class indicating an error during the Bluetooth connection, carrying an error message
    data class Error(val message: String) : RegistrationResult
}
