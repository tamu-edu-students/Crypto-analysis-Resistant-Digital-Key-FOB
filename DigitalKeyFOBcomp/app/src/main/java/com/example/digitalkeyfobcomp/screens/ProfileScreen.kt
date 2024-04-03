package com.example.digitalkeyfobcomp.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothUiState
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothViewModel
import com.example.digitalkeyfobcomp.Encryption.ZHardware
import com.example.digitalkeyfobcomp.ProfileEvent
import com.example.digitalkeyfobcomp.ProfileState
import com.example.digitalkeyfobcomp.ProfileViewModel
import com.example.digitalkeyfobcomp.bitmapToHash
import com.example.digitalkeyfobcomp.components.BottomNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.warting.signaturepad.SignaturePadAdapter
import se.warting.signaturepad.SignaturePadView


private const val SIGNATURE_PAD_HEIGHT = 120
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    state: ProfileState,
    onEvent:(ProfileEvent) -> Unit,
    viewModel: ProfileViewModel,
    blueViewModel: BluetoothViewModel,
    bluetoothState: BluetoothUiState

) {
    val coroutineScope = rememberCoroutineScope()  // use coroutine to perform logic outside of main UI thread
    val context = LocalContext.current // use context to send toast messages to user
    val keyboardController = LocalSoftwareKeyboardController.current
    var profileDuplicateCheck by remember { mutableStateOf("") }
    var showDisconnectionConfirmationDialog by remember { mutableStateOf(false) }

//    val bluetoothState1 by blueViewModel.state.collectAsState()

    // Observing user messages and showing toast
//    LaunchedEffect(key1 = bluetoothState.userMessage) {
//        bluetoothState.userMessage?.let { message ->
//            Toast.makeText(
//                context,
//                message,
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }


    if (showDisconnectionConfirmationDialog) {
        AlertDialog(
            onDismissRequest = {
                showDisconnectionConfirmationDialog = false
            },
            title = {
                Text(text = "Confirm Disconnection")
            },
            text = {
                Text("Are you sure you want to disconnect?")
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    onClick = {
                        blueViewModel.disconnectFromDevice()
                        showDisconnectionConfirmationDialog = false // Close the dialog
                    }
                ) {
                    Text("Disconnect")
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    onClick = {
                        showDisconnectionConfirmationDialog = false // Close the dialog
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Blue, // Set the background color here
            ) {
                TopAppBar(
                    title = {
                        Text(text = "Digital Key FOB", fontWeight = FontWeight.Bold)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    actions = {
                        Button(
                            onClick = {
                                if (bluetoothState.isConnected) {
                                    showDisconnectionConfirmationDialog = true
                                } else {
                                    Toast.makeText(context, "No Device Connected", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text("Disconnect")
                        }
                    }
                )
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFF4F4F4)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // initializing variables
                var currentbitmap: Bitmap?
                var bitmapHash by remember { mutableStateOf("") }
                var signaturePadAdapter: SignaturePadAdapter? = null
                val mutableSvg = remember { mutableStateOf("") }
                var signatureSigned: Boolean
                val openDialog = remember { mutableStateOf(false)  }
                var saveAddress = ""
                var saveName = ""
                var registrationBoolean = false
                TextField( // Profile text input
                    modifier = Modifier
                        .border(width = 2.dp, color = Color.Black)
                        .background(color = Color.White)
                        ,
                    singleLine = true,
                    value = state.name,
                    onValueChange = { onEvent(ProfileEvent.SetName(it)) },
                    placeholder = { Text("Enter New Profile Name") },
                )

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            modifier = Modifier
                                .size(300.dp, SIGNATURE_PAD_HEIGHT.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color.Black,
                                )
                                .background(color = Color.White)
                        )
                {
                    SignaturePadView( // signature pad call
                        onReady = {
                            signaturePadAdapter = it
                                  },
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row {
                    ElevatedButton(
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),

                        onClick = { // clear signature button
                        signaturePadAdapter?.clear()
                    }) {
                        Text("Clear Signature")
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    ElevatedButton(
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        onClick = { // add profile button

                            keyboardController?.hide()
                        coroutineScope.launch {
                            val profile = viewModel.getProfileByName(state.name)

                            if (profile != null) {
                                profileDuplicateCheck = profile.name
                            }
                            if (state.name != "" && state.name != profileDuplicateCheck) {
                                currentbitmap = signaturePadAdapter?.getSignatureBitmap()  // saving bitmap value to variable to pass to hash function

                                signatureSigned = signaturePadAdapter?.isEmpty == false // Check if signature pad is empty, default value = false

                                if (currentbitmap != null) { // Comparing currentbitmap to null to make sure there is bitmaptype value

                                    bitmapHash = ZHardware(bitmapToHash(currentbitmap!!)) // Passing to hash function and checking if null

                                    if(signatureSigned) { // Check for true/false signatureSigned

                                        if(bluetoothState.isConnected) {
                                            Toast.makeText( // user prompt for profile creation if missing signature
                                                context,
                                                "Device Already Connected",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        else {
                                            openDialog.value = true
                                            onEvent(ProfileEvent.Setsigid(bitmapHash)) // setting sigid to bitmaphash for profile creation
                                            signaturePadAdapter?.clear() // clearing signature pad
                                            signatureSigned =
                                                false // resetting signature signed value
                                        }

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
                                    "Please enter a unique and non-duplicate profile name",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }

                    }) {
                        Text("Add Profile")
                    }


                    if (openDialog.value) {

                        AlertDialog(
                            onDismissRequest = {

                            },
                            title = {
                                Text(text = "Bluetooth Menu")
                            },
                            text = {
                                BluetoothScreen(
                                    state = bluetoothState ,
                                    onStartScan = blueViewModel::startScan,
                                    onStopScan = blueViewModel::stopScan,
                                    onStartServer = blueViewModel::waitForIncomingConnections,
                                    onDeviceClick = {selectedDevice ->
                                        // Handle the click event, for example, save the device's address
                                        saveAddress = selectedDevice.address
                                        saveName = selectedDevice.name.toString()

                                        onEvent(ProfileEvent.Setaddress(saveAddress))

                                        // Saving profile
                                        blueViewModel.registerToDevice(selectedDevice, bitmapHash)


//                                        onEvent(ProfileEvent.Setsigid(blueViewModel.state.value.userMessage.toString()))

//                                        if(bluetoothState.userMessage == "Registration Successful"){
//                                            onEvent(ProfileEvent.SaveProfile)
//                                            Toast.makeText(
//                                                context,
//                                                bluetoothState.userMessage,
//                                                Toast.LENGTH_LONG
//                                            ).show()
//                                        }else{
//                                            Toast.makeText(
//                                                context,
//                                                bluetoothState.userMessage,
//                                                Toast.LENGTH_LONG
//                                            ).show()
//                                        }


                                        openDialog.value = false
                                    }
                                )
                            },
                            confirmButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),

                                    onClick = {
                                        openDialog.value = false
                                    }) {
                                    Text("Dismiss")
                                }
                            },
                        )
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
