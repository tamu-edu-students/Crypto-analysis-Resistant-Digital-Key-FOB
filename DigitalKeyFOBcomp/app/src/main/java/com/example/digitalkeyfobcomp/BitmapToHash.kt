package com.example.digitalkeyfobcomp

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.security.MessageDigest

fun bitmapToHash(bitmap: Bitmap): String {
    // Convert the bitmap to a byte array
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()

    // Calculate the SHA-256 hash of the byte array
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashBytes = messageDigest.digest(byteArray)

    // Convert the hash bytes to a hexadecimal string
    val hexStringBuilder = StringBuilder()
    for (byte in hashBytes) {
        val hex = Integer.toHexString(0xFF and byte.toInt())
        if (hex.length == 1) {
            hexStringBuilder.append('0')
        }
        hexStringBuilder.append(hex)
    }

    return hexStringBuilder.toString()
}