package com.example.digitalkeyfobcomp.BluetoothSetup

sealed interface ConnectionResult {
    object ConnectionEstablished: ConnectionResult
    data class Error(val message: String): ConnectionResult
}