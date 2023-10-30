package com.example.digitalkeyfobcomp.screens

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digitalkeyfobcomp.ProfileEvent
import com.example.digitalkeyfobcomp.ProfileState
import com.example.digitalkeyfobcomp.bitmapToHash
import com.example.digitalkeyfobcomp.components.BottomNavigation
import se.warting.signaturepad.SignaturePadAdapter
import se.warting.signaturepad.SignaturePadView


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    state: ProfileState,
    onEvent:(ProfileEvent) -> Unit

) {

    val context = LocalContext.current
//
//    // Initialize BluetoothManager
//    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//    val bluetoothAdapter = bluetoothManager.adapter
//
//    if (bluetoothAdapter == null) {
//        Toast.makeText(context, "Your Phone Can't Run this application", Toast.LENGTH_LONG).show()
//    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White, // Set the background color here

            ) {
                TopAppBar(
                    title = {
                        Text(text = "Digital Key FOB", fontWeight = FontWeight.Bold)
                    },
                    modifier = Modifier.fillMaxWidth(),

                    )
            }
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(color = Color.LightGray),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // The rest of your content
                var currentbitmap: Bitmap? = null
                var bitmapHash by remember { mutableStateOf("") }
                var signaturePadAdapter: SignaturePadAdapter? = null
                val mutableSvg = remember { mutableStateOf("") }

                OutlinedTextField(
                    value = state.name,
                    onValueChange = { onEvent(ProfileEvent.SetName(it)) },
                    label = { Text("Enter New Profile Name") }
                )

                Spacer(modifier = Modifier.height(32.dp))
                Box (

                    modifier = Modifier
                        .size(300.dp,200.dp)
                        .border(width = 2.dp, color = Color.DarkGray),
                    contentAlignment = Alignment.Center
                ){
                    SignaturePadView(onReady = {
                        signaturePadAdapter = it
                    })
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row {
                    OutlinedButton(onClick = {

                        signaturePadAdapter?.clear()
                    }) {
                        Text("Clear Signature")
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    OutlinedButton(onClick = {
//                        mutableSvg.value = signaturePadAdapter?.getSignatureSvg() ?: ""

                        currentbitmap = signaturePadAdapter?.getSignatureBitmap()

                        if(currentbitmap!=null) {
                            bitmapHash = bitmapToHash(currentbitmap!!)
                            Toast.makeText(context, "Profile selected $bitmapHash", Toast.LENGTH_SHORT).show()

                        }
                        onEvent(ProfileEvent.SaveProfile)


                    }) {
                        Text("Add Profile")
                    }
                }
            }
        },
        bottomBar = {
            BottomNavigation(navController)
        }
    )
}

//@Preview
//@Composable
//fun ProfileScreenPreview(){
//    val navController = rememberNavController()
//    ProfileScreen(navController)
//}

@Composable
fun ProfileInput() {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Enter New Profile Name") }
    )
}