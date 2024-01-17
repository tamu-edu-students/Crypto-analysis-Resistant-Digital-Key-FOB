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

class DHKEPt2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CCSubsystemCodeForDeviceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DHKEAfter()
                }
            }
        }
    }
}

//DHKE Return From Vehicle
@Composable
fun DHKEAfter(modifier: Modifier = Modifier) {
    Surface(color = Color.LightGray) {
        //Setting the Values of P, A, and B
        //P is predetermined, A is calculated before, and B is taken from the vehicle.
        val P = BigInteger("653")
        val a: Int = 1 //This has to be changed with the unique A value from the first part.
        val B = BigInteger("513") //Add in the value of B from the vehicle here.

        //Calculating the final secret key - B^a mod P
        val sk = (B.pow(a) % P)

        //Text Output for demonstration and debugging.
        Text(text = "Testing: \nSk = $sk", modifier = modifier.padding(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DHKEAfterOutput() {
    CCSubsystemCodeForDeviceTheme {
        DHKEAfter()
    }
}