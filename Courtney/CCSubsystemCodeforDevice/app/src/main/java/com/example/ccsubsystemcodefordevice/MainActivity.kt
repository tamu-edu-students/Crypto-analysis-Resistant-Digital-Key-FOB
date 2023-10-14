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
                    DHKESetUp()
                }
            }
        }
    }
}

//DHKE Before Being Send to the Vehicle
@Composable
fun DHKESetUp(modifier: Modifier = Modifier) {
    Surface(color = Color.LightGray) {
        //setting P and G:
        val P = BigInteger("13397810928545909101")
        val G = BigInteger("7")
        //Creating the random A for the Vehicle
        val randInt = SecureRandom()
        val A = randInt.nextInt(99)
        //Doing the Diffie-Hellman Process
        var Aprime = BigInteger("2")
        Aprime = (G.pow(A)) % P //mathematics

        Text(text = "Testing: P = $P, G = $G, A Val = $A, Vehicle DHKE Portion = $Aprime", modifier = modifier.padding(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DHKESetUpOutput() {
    CCSubsystemCodeForDeviceTheme {
        DHKESetUp()
    }
}