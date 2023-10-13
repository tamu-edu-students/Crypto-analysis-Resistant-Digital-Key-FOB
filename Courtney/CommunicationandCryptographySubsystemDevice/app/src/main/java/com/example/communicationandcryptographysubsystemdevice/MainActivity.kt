package com.example.communicationandcryptographysubsystemdevice

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
import com.example.communicationandcryptographysubsystemdevice.ui.theme.CommunicationAndCryptographySubsystemDeviceTheme
import java.math.BigInteger
import javax.crypto.Cipher

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CommunicationAndCryptographySubsystemDeviceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DHKESetUp()
                    DHKEFinish()
                    AESEncrypt()
                    DigitalSign()
                    ZHP()
                }
            }
        }
    }
}

//Diffie-Hellman Key Exchange Pre-Send to Vehicle
@Composable
fun DHKESetUp(modifier: Modifier = Modifier) {
    Surface(color = Color.LightGray) {
        val P = BigInteger("13397810928545909101")
        val G = BigInteger("5667934")
        Text(text = "Vehicle DHKE Portion: $G", modifier = modifier.padding(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DHKESetUpOutput() {
    CommunicationAndCryptographySubsystemDeviceTheme {
        DHKESetUp()
    }
}

//Diffie-Hellman Key Exchange Post-Send to Vehicle
@Composable
fun DHKEFinish(modifier: Modifier = Modifier) {
    Surface(color = Color.LightGray) {
        val PublicKey: Int = 438

        Text(text = "AES Key: $PublicKey", modifier = modifier.padding(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DHKEFinishOutput() {
    CommunicationAndCryptographySubsystemDeviceTheme {
        DHKEFinish()
    }
}

//AES Encryption
@Composable
fun AESEncrypt(modifier: Modifier = Modifier) {
    Surface(color = Color.LightGray) {
        Text(text = "AES Encrypted Data: Data Here", modifier = modifier.padding(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun AESEncryptOutput() {
    CommunicationAndCryptographySubsystemDeviceTheme {
        AESEncrypt()
    }
}

//Digital Dynamic Signature Creation with SHA
@Composable
fun DigitalSign(modifier: Modifier = Modifier) {
    Surface(color = Color.LightGray) {
        Text(text = "SHA Digital Dynamic Signature: Data Here", modifier = modifier.padding(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DigitalSignOutput() {
    CommunicationAndCryptographySubsystemDeviceTheme {
        DigitalSign()
    }
}

//Z-Hardware Profile Creation
@Composable
fun ZHP(modifier: Modifier = Modifier) {
    Surface(color = Color.LightGray) {
        Text(text = "Z-Hardware Profile: Data Here", modifier = modifier.padding(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ZHPOutput() {
    CommunicationAndCryptographySubsystemDeviceTheme {
        ZHP()
    }
}