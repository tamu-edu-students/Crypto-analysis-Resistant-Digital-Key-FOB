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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.digitalkeyfobcomp.ui.theme.lightblue
import kotlinx.coroutines.launch
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
    val coroutineScope = rememberCoroutineScope()  // use coroutine to perform logic outside of main UI thread
    val context = LocalContext.current // use context to send toast messages to user

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
                    .background(color = lightblue),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // initializing variables
                var currentbitmap: Bitmap?
                var bitmapHash by remember { mutableStateOf("") }
                var signaturePadAdapter: SignaturePadAdapter? = null
                val mutableSvg = remember { mutableStateOf("") }
                var signatureSigned: Boolean

                OutlinedTextField( // Profile text input
                    value = state.name,
                    onValueChange = { onEvent(ProfileEvent.SetName(it)) },
                    label = { Text("Enter New Profile Name") }
                )

                Spacer(modifier = Modifier.height(32.dp))
                Box ( // box to outline signature pad

                    modifier = Modifier
                        .size(300.dp,200.dp)
                        .border(width = 2.dp, color = Color.DarkGray),
                    contentAlignment = Alignment.Center
                ){
                    SignaturePadView( // signature pad call
                        onReady = {
                            signaturePadAdapter = it
                                  },
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row {
                    OutlinedButton(
                        modifier = Modifier
                        .background(Color.White)
                        .border(1.dp, Color.Black)
                        ,

                        onClick = { // clear signature button
                        signaturePadAdapter?.clear()
                    }) {
                        Text("Clear Signature")
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    OutlinedButton(
                        modifier = Modifier
                            .background(color = Color.White),
                        onClick = { // add profile button
//                        mutableSvg.value = signaturePadAdapter?.getSignatureSvg() ?: ""
                        coroutineScope.launch {
                            if (state.name != "") {
                                currentbitmap = signaturePadAdapter?.getSignatureBitmap()  // saving bitmap value to variable to pass to hash function

                                signatureSigned = signaturePadAdapter?.isEmpty == false // Check if signature pad is empty, default value = false

                                if (currentbitmap != null) { // Comparing currentbitmap to null to make sure there is bitmaptype value

                                    bitmapHash = bitmapToHash(currentbitmap!!) // Passing to hash function and checking if null

                                    if(signatureSigned) { // Check for true/false signatureSigned

                                        onEvent(ProfileEvent.Setsigid(bitmapHash)) // setting sigid to bitmaphash for profile creation

                                        Toast.makeText( // Debugging
                                            context,
                                            "Profile created $bitmapHash",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        onEvent(ProfileEvent.SaveProfile) // Saving profile
                                        signaturePadAdapter?.clear() // clearing signature pad
                                        signatureSigned = false // resetting signature signed value

                                    }else{
                                        Toast.makeText( // user prompt for profile creation if missing signature
                                            context,
                                            "Please Sign Signature pad",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                            }else{
                                Toast.makeText( // user prompt for profile creation if missing profile text
                                    context,
                                    "Please Enter Profile Name",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }

                    }) {
                        Text("Add Profile")
                    }
                }
            }
        },
        bottomBar = {
            BottomNavigation(navController) // Navigation bar
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