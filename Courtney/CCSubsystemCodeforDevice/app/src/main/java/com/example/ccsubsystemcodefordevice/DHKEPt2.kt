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
        val P = BigInteger("1339781092854590957")
        val a: Int = 45959
        val B = BigInteger("71702249265423832172")
        val sk = (B.pow(a) % P)
        Text(text = "Testing: Sk = $sk", modifier = modifier.padding(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DHKEAfterOutput() {
    CCSubsystemCodeForDeviceTheme {
        DHKEAfter()
    }
}