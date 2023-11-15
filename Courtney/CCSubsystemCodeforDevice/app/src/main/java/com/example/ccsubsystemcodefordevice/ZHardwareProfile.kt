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
        val bitMapIn = "ef72b1d67e57f2"
        val bitMapInt = BigInteger(bitMapIn, 16)

        //Creating the PseudoRandom number to XOR with
        val randomInt = SecureRandom()
        val prnBigInt = BigInteger(256, randomInt)

        //XOR the Pseudo-Random Number and the Byte Array
        val zhp = bitMapInt.xor(prnBigInt)

        //Turning the Z-Hardware Profile back into a Byte Array
        val zhpArray = zhp.toByteArray()

        Text(text = "Testing: \nBit Map in BigInteger: $bitMapInt " +
                "\n\nPseudo-Random Number: $prnBigInt" +
                "\n\nZ-Hardware Profile (BigInteger): $zhp" +
                "\n\nZ-Hardware Profile (Byte-Array): $zhpArray",
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