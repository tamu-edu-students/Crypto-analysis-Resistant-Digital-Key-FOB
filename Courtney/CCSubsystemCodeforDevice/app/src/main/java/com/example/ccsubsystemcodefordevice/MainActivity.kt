package com.example.ccsubsystemcodefordevice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ccsubsystemcodefordevice.ui.theme.CCSubsystemCodeForDeviceTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
//Needed Imports Below
import java.math.BigInteger
import java.security.SecureRandom


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CCSubsystemCodeForDeviceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DHKEBefore()
                }
            }
        }
    }
}

//DHKE Before Being Send to the Vehicle
@Composable
fun DHKEBefore(modifier: Modifier = Modifier) {
    Surface(color = Color.LightGray) {
        //Setting P and G - P is the prime number and G is the generator (primative root of P)
        val P = BigInteger("653")
        val G = BigInteger("5")

        //Creating the random A for the Vehicle
        val randInt = SecureRandom() //special randomization that follow standard cryptography protocols
        val a = randInt.nextInt(6)

        //Doing the Diffie-Hellman Process (G^a mod P)
        var A = BigInteger("2")
        A = (G.pow(a)) % P //mathematics

        //Outputs the text to use for demonstration and debugging purposes
        Text(text = "Before Vehicle: P = $P \n\nG = $G" +
                "\n\nA Val = $a" +
                "\n\nVehicle DHKE Portion = $A",
            modifier = modifier.padding(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DHKESetUpOutput() {
    CCSubsystemCodeForDeviceTheme {
        DHKEBefore()
    }
}