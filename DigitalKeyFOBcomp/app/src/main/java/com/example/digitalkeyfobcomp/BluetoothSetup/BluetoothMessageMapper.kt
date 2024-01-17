package com.example.digitalkeyfobcomp.BluetoothSetup


// Extension function to convert a formatted String to a BluetoothMessage
fun String.toBluetoothMessage(isFromLocalUser: Boolean): BluetoothMessage {
    // Extract sender's name and message from the formatted String
    val name = substringBeforeLast("#")
    val message = substringAfter("#")

    // Create and return a BluetoothMessage
    return BluetoothMessage(
        message = message,         // Assign the extracted message
        senderName = name,          // Assign the extracted sender's name
        isFromLocalUser = isFromLocalUser  // Assign the provided flag indicating the origin
    )
}

// Extension function to convert a BluetoothMessage to a byte array
fun BluetoothMessage.toByteArray(): ByteArray {
    // Concatenate sender's name and message with a '#' separator and convert to byte array
    return "$senderName#$message".encodeToByteArray()
}
