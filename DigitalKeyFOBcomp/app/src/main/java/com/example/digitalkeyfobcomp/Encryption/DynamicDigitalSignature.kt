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

@Composable
fun DDS(fromDevice: String): String {
//    Surface(color = Color.LightGray) {
        //Setting up the SHA-256 Protocol
//        val fromDevice = "ZHardwareProfileandCommandandTimestamp" //Example Text for Testing
        val md = MessageDigest.getInstance("SHA-256") //Setting up the instance for SHA

        //Doing the SHA-256 Protocol - turns the device string into a byte array first.
        val hashText = md.digest(fromDevice.toByteArray())

        //Turning the Hash Text from a Byte Array into Hex
        val hashNumber = BigInteger(1, hashText) //Turing the hash text into a BigInteger
        val hexString = StringBuilder(hashNumber.toString(16)) //turing the Hash into a Hex number in string format

        return hexString.toString() // had to add .toString() (ask Courtney)
//        Text(text = "Testing: \nHash Text Straight from SHA = $hashText" +
//                "\n\nHash Text in Binary = $hashNumber " +
//                "\n\nHash Text in Hex = $hexString ",
//            modifier = modifier.padding(12.dp))
//    }
}