package com.example.digitalkeyfobcomp.BluetoothSetup


import android.bluetooth.BluetoothSocket
//import com.plcoding.bluetoothchat.domain.chat.BluetoothMessage
//import com.plcoding.bluetoothchat.domain.chat.ConnectionResult
//import com.plcoding.bluetoothchat.domain.chat.TransferFailedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException


// Custom exception to indicate a failure in reading incoming Bluetooth data
class TransferFailedException : IOException("Reading incoming data failed")

// Service class for handling Bluetooth data transfer
class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {
    // Function to listen for incoming Bluetooth messages and emit them as a Flow
    fun listenForIncomingMessages(): Flow<BluetoothMessage> {
        return flow {
            // Check if the socket is connected
            if (!socket.isConnected) {
                return@flow
            }

            // Buffer to hold incoming data
            val buffer = ByteArray(1024)

            // Continuously read incoming data from the Bluetooth socket
            while (true) {
                val byteCount = try {
                    // Read data into the buffer
                    socket.inputStream.read(buffer)
                } catch (e: IOException) {
                    // Throw a custom exception if reading data fails
                    throw TransferFailedException()
                }

                // Emit a BluetoothMessage constructed from the received data
                emit(
                    buffer.decodeToString(
                        endIndex = byteCount
                    ).toBluetoothMessage(
                        isFromLocalUser = false
                    )
                )
            }
        }.flowOn(Dispatchers.IO) // Perform the flow operations on the IO dispatcher
    }

    // Function to send a message over Bluetooth
    suspend fun sendMessage(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Write the provided bytes to the Bluetooth socket output stream
                socket.outputStream.write(bytes)
            } catch (e: IOException) {
                e.printStackTrace()
                // Return false if sending the message fails
                return@withContext false
            }

            // Return true if the message is sent successfully
            true
        }
    }
}


