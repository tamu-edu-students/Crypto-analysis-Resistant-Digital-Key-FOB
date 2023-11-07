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
        //SHA-256 Protocol
        val fromDevice = "CommandAndZHardwareProfileAndTimeStamp"
        val md = MessageDigest.getInstance("SHA-256")
        val hashText = md.digest(fromDevice.toByteArray(StandardCharsets.UTF_8))

        //Turning the Hash Text from a Byte Array into Hex
        val hashNumber = BigInteger(1, hashText)
        val hexString = StringBuilder(hashNumber.toString(16))

        Text(text = "Testing: \nHash Text from SHA = $hashText \n\nHash Text in Binary = $hashNumber " +
                "\n\nHash Text in Hex = $hexString ",
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