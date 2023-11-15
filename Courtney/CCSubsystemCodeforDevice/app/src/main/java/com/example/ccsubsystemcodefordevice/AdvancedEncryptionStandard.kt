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
import javax.crypto.Cipher
import java.util.Base64
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.SecretKeySpec
import java.math.BigInteger


class AdvancedEncryptionStandard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CCSubsystemCodeForDeviceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AESEncryption()
                }
            }
        }
    }
}

@Composable
fun AESEncryption(modifier: Modifier = Modifier) {
    Surface(color = Color.LightGray) {
        //Setting up variables for the AES Encryption
        val SecretKeyInt = BigInteger("221") //SecretKey from the Diffie-Hellman Key Exchange
        val SecretKey = SecretKeyInt.toString()
        val ivbytes = byteArrayOf(0x64, 0x7b, 0x5e, 0x57, 0x67, 0x1f, 0x2c, 0x10, 0x43, 0x25, 0x0a, 0x25, 0x72, 0x12, 0x49, 0x03)
        val salt = "CrytoAnalysisDigtialKeyFOB"
        val zHardware = byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f)

        //IV Random Generator (May need to fix this if it messes with the IV value)
        val ivspec = IvParameterSpec(ivbytes);

        //Generating a secret key from Diffie-Hellman Output
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(SecretKey.toCharArray(), salt.toByteArray(), 65536, 256)
        val sk_temp = factory.generateSecret(spec)
        val symmetrickey = SecretKeySpec(sk_temp.getEncoded(), "AES")

        //Doing the encryption
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, symmetrickey, ivspec)
        val cipherText = cipher.doFinal(zHardware)
        val CipherTextString = Base64.getEncoder().encodeToString(cipherText)

        Text(text = "Testing: \nKey = $symmetrickey" +
                "\n\nPlain Text: $zHardware" +
                "\n\nCipher Text = $CipherTextString",
            modifier = modifier.padding(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun AESEncryptionOutput() {
    CCSubsystemCodeForDeviceTheme {
        AESEncryption()
    }
}