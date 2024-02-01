package com.example.digitalkeyfobcomp.Encryption

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.math.BigInteger
import java.security.SecureRandom


fun ZHardware(bitMapIn: String): String {
//    Surface(color = Color.LightGray) {
        // Turning the BitMap in Byte Array form (bitMapIn) from Jeremy into a BigInteger
        val bitMapInt = BigInteger(bitMapIn, 16)

        // Creating the PseudoRandom number to XOR with
        val randomInt = SecureRandom()
        val prnBigInt = BigInteger(256, randomInt)

        // XOR the Pseudo-Random Number and the BitMap
        val zhp = bitMapInt.xor(prnBigInt)

        // Turning the Z-Hardware Profile into a string for encryption
        val zhpArray = zhp.toString()

        // Uncomment the following lines if you want to display information using Text()
        /*
        Text(text = "Testing: \nBit Map in BigInteger: $bitMapInt " +
            "\n\nPseudo-Random Number: $prnBigInt" +
            "\n\nZ-Hardware Profile: $zhpArray",
            modifier = modifier.padding(12.dp))
        */

        // Return the Z-Hardware Profile
        return zhpArray
//    }
}