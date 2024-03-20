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
import java.security.MessageDigest
import java.math.BigInteger
import java.nio.charset.StandardCharsets



class DynamicDigitalSignature : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CCSubsystemCodeForDeviceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DDS()
                }
            }
        }
    }
}

@Composable
fun DDS(modifier: Modifier = Modifier) {
    Surface(color = Color.LightGray) {
        //Setting up the SHA-256 Protocol
        val fromDevice = "Demonstration" //Example Text for Testing
        val md = MessageDigest.getInstance("SHA-256") //Setting up the instance for SHA

        //Doing the SHA-256 Protocol - turns the device string into a byte array first.
        val hashText = md.digest(fromDevice.toByteArray())

        //Turning the Hash Text from a Byte Array into Hex
        val hashNumber = BigInteger(1, hashText) //Turing the hash text into a BigInteger
        val hexString = StringBuilder(hashNumber.toString(16)) //turing the Hash into a Hex number in string format
        val hexStringUpper = hexString.toString().uppercase()

        Text(text = "Testing: \nPlain Text = $fromDevice" +
                "\n\nHash Text in Hex = $hexStringUpper ",
            modifier = modifier.padding(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DDSOutput() {
    CCSubsystemCodeForDeviceTheme {
        DDS()
    }
}