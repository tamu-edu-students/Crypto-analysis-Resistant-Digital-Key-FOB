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


class TransferFailedException: IOException("Reading incoming data failed")

class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {
    fun listenForIncomingMessages(): Flow<BluetoothMessage> {
        return flow {
            if(!socket.isConnected) {
                return@flow
            }
            val buffer = ByteArray(1024)
            while(true) {
                val byteCount = try {
                    socket.inputStream.read(buffer)
                } catch(e: IOException) {
                    throw TransferFailedException()
                }

                emit(
                    buffer.decodeToString(
                        endIndex = byteCount
                    ).toBluetoothMessage(
                        isFromLocalUser = false
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendMessage(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                socket.outputStream.write(bytes)
            } catch(e: IOException) {
                e.printStackTrace()
                return@withContext false
            }

            true
        }
    }
}