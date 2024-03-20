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
import java.security.SecureRandom
import javax.crypto.SecretKey
import java.security.spec.KeySpec


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
        /* In general, commands with spec in them confine the data going in by the specifications of
        the given algorithm or key. It essentially turns the original key data into a key that is
        of use and type safe for the algorithum. */

        //Pre-Set variables (intake) for the AES Encryption (Key and IV)
        val keyStr = "AESEncrpytionKey" //SecretKey from the Diffie-Hellman Key Exchange
        val ivStr = "InitalizationVec" //byteArrayOf(0x09, 0x1b, 0x17, 0x02, 0x6e, 0x24, 0x23, 0x08, 0x19, 0x0d, 0x4a, 0x10, 0x77, 0x46, 0x7e, 0x32)
        val zHardware = "DemonstrationTxt" //Text to encrypt
        val zHardwareByte = zHardware.toByteArray(Charsets.UTF_8)

        //Specs - used in the initiation of the AES - confines the IV and key to the necessary specification for AES
        val iv = IvParameterSpec(ivStr.toByteArray(Charsets.UTF_8))
        val key = SecretKeySpec(keyStr.toByteArray(Charsets.UTF_8), "AES") //generating the final key spec

        //Doing the encryption
        val cipher = Cipher.getInstance("AES/CBC/NoPadding") //getting the instance of AES - i.e. setting it up
        cipher.init(Cipher.ENCRYPT_MODE, key, iv) //initializing the cipher and the specific mod needed.
        val cipherText = cipher.doFinal(zHardwareByte) //Performs the encryption

        //Turning the cipherText from binary into a string through Base64 encoding so it can be outputted.
        val cipherTextString = Base64.getEncoder().encodeToString(cipherText)

        //Doing the Decryption (For Demo Purposes - will be taken out later when integrated)
        val dataEncryptedAbove = Base64.getDecoder().decode(cipherTextString) //Decrypting the string back to binary.
        cipher.init(Cipher.DECRYPT_MODE, key, iv) //setting up the decrypt instance of AES
        val returnText = cipher.doFinal(dataEncryptedAbove) //doing the decryption
        val returnTextString = String(returnText) //turning the final output from a Byte Array into a string that can be outputted.

        Text(text = "Testing: \nPlain Text: $zHardware" +
                "\n\nKey (String): $keyStr" +
                "\n\nKey (Byte Array): $key" +
                "\n\nIV: $iv" +
                "\n\nCipher Text: $cipherTextString" +
                "\n\nReturn Text: $returnTextString",
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