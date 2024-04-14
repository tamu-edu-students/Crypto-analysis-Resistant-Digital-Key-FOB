package com.example.digitalkeyfobcomp.Encryption

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.math.BigInteger
import java.security.MessageDigest


fun DDS(fromDevice: String): String {
        val md = MessageDigest.getInstance("SHA-256") //Setting up the instance for SHA

        //Doing the SHA-256 Protocol - turns the device string into a byte array first.
        val hashText = md.digest(fromDevice.toByteArray())

        val charset = Charsets.UTF_8 // or any other character encoding you expect
        val string = String(hashText, charset).uppercase()

        //Turning the Hash Text from a Byte Array into Hex
        val hashNumber = BigInteger(1, hashText) //Turing the hash text into a BigInteger
        val hexString = StringBuilder(hashNumber.toString(16)) //turing the Hash into a Hex number in string format

//        return hexString.toString().uppercase() // had to add .toString() (ask Courtney)
        return string
}