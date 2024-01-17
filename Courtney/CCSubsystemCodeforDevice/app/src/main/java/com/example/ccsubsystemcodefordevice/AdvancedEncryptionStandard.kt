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
        /* In general commands with spec in them confine the data going in by the specifications of
        the given algorithm or key. It essentially turns the original key data into a key that is
        of use and type safe for the algorithum. */

        //Setting up variables for the AES Encryption
        val SecretKeyInt = BigInteger("3874") //SecretKey from the Diffie-Hellman Key Exchange
        val SecretKey = SecretKeyInt.toString() //Turning that to a string to output
        //Below: Index Vector - used to initialize AES - this was chosen at random, eventually must match vehicle.
        val ivbytes = byteArrayOf(0x64, 0x7b, 0x5e, 0x57, 0x67, 0x1f, 0x2c, 0x10, 0x43, 0x25, 0x0a, 0x25, 0x72, 0x12, 0x49, 0x03)
        val salt = "CrytoAnalysisDigtialKeyFOB" //This is used in the generation to turn the DHKE result into a key
        val zHardware = "This will later be replaced with the Z-Hardware Profile" //Text to encrypt

        //IV Spec - used in the initiation of the AES - confines the IV to the necessary specification for AES
        val ivspec = IvParameterSpec(ivbytes);

        //Chaning the resulting key from the Diffie-Hellman Output into a Password Based Encryption Key
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256") //setting up the algorithm to turn the DHKE output into a key
        //Setting up the specifics for the specification of the PBE key
        val spec = PBEKeySpec(SecretKey.toCharArray(), salt.toByteArray(), 65536, 256)
        val sk_temp = factory.generateSecret(spec) //generating the key based on the specification
        val symmetricKeySpec = SecretKeySpec(sk_temp.getEncoded(), "AES") //generating the final key spec

        //Doing the encryption
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding") //getting the instance of AES - i.e. setting it up
        cipher.init(Cipher.ENCRYPT_MODE, symmetricKeySpec, ivspec) //initializing the cipher and the specific mod needed.
        val cipherText = cipher.doFinal(zHardware.toByteArray()) //Performs the encryption
        //Turning the cipherText from binary into a string through Base64 encoding so it can be outputted.
        val cipherTextString = Base64.getEncoder().encodeToString(cipherText)

        //Doing the Decryption (For Demo Purposes - will be taken out later when integrated)
        val dataEncryptedAbove = Base64.getDecoder().decode(cipherTextString) //Decrypting the string back to binary.
        cipher.init(Cipher.DECRYPT_MODE, symmetricKeySpec, ivspec) //setting up the decrypt instance of AES
        val returnText = cipher.doFinal(dataEncryptedAbove) //doing the decryption
        val returnTextString = String(returnText) //turning the final output from a Byte Array into a string that can be outputted.

        Text(text = "Testing: \nPlain Text: $zHardware" +
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