package com.example.ccsubsystemcodefordevice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ccsubsystemcodefordevice.ui.theme.CCSubsystemCodeForDeviceTheme
//Needed Imports Below
import java.math.BigInteger
import java.security.SecureRandom

class ZHardwareProfile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CCSubsystemCodeForDeviceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ZHardware()
                }
            }
        }
    }
}

@Composable
fun ZHardware(modifier: Modifier = Modifier) {
    Surface(color = Color.LightGray) {
        //Turning the BitMap in Byte Array form (bitMapIn) from Jeremy into a BigInteger
        val bitMapIn = byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
            0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x11, 0x12, 0x13, 0x14,
            0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e,
            0x1f, 0x21)
        val bitMapInt = BigInteger(bitMapIn)

        //Creating the PseudoRandom number to XOR with
        val randomInt = SecureRandom()
        val prnBigInt = BigInteger(256, randomInt)

        //XOR the Pseudo-Random Number and the Byte Array
        val zhp = bitMapInt.xor(prnBigInt)

        Text(text = "Testing: \nBit Map in BigInteger: $bitMapInt " +
                "\n\nPseudo-Random Number: $prnBigInt" +
                "\n\nZ-Hardware Profile: $zhp",
            modifier = modifier.padding(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ZHardwareOutput() {
    CCSubsystemCodeForDeviceTheme {
        ZHardware()
    }
}