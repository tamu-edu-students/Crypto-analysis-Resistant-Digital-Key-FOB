package com.example.digitalkeyfobcomp.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothDevice
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothDeviceDomain
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothUiState
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothViewModel
import com.example.digitalkeyfobcomp.PreferencesManager
import com.example.digitalkeyfobcomp.ProfileEntity
import com.example.digitalkeyfobcomp.ProfileEvent
import com.example.digitalkeyfobcomp.ProfileState
import com.example.digitalkeyfobcomp.ProfileViewModel
import com.example.digitalkeyfobcomp.components.BottomNavigation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    navController: NavController,
    state: ProfileState,
    onEvent:(ProfileEvent) -> Unit,
    profileNamesFlow: Flow<List<String>>,
    viewModel: ProfileViewModel,
    blueViewModel: BluetoothViewModel,
    bluetoothState: BluetoothUiState
                ) {
//    var selectedProfile by remember { mutableStateOf<String?>(null) }
    val preferencesManager = PreferencesManager(LocalContext.current)
    var selectedProfile by remember { mutableStateOf<ProfileEntity?>(null) }
    val context = LocalContext.current
    val profileNames by profileNamesFlow.collectAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(profileNames.firstOrNull() ?: "") }
    var rememberedProfile by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val selectedDevice = BluetoothDeviceDomain("", "")

    LaunchedEffect(Unit) {// reload selected profile when activity is launched
        // This block of code is executed only once when the Composable is initially displayed
        val retrievedProfile: ProfileEntity? =  preferencesManager.getData("selectedProfile", null)
        retrievedProfile?.let { profile ->
            rememberedProfile = profile.name
        }
        selectedText = rememberedProfile
    }
    //added code to pull saved preferences profile into selected profile if activity restarts
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
                // The rest of your content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp), contentAlignment = Alignment.Center
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {
                        TextField(
                            value = selectedText,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            profileNames.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        selectedText = item
                                        expanded = false
                                        Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }

                val openDialog = remember { mutableStateOf(false)  }

                Row(){
                    ElevatedButton(
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        onClick = {
                            blueViewModel.disconnectFromDevice()
                            if (selectedText.isNotBlank()) {
                            // Call the deleteByName function to delete the selected profile
//   moving to profile creation page                             openDialog.value = true
                                preferencesManager.clearData()
                                coroutineScope.launch {
                                    val profile = viewModel.getProfileByName(selectedText)
                                    selectedProfile = profile
                                    selectedProfile?.let { profile -> preferencesManager.saveData("selectedProfile", profile) }
                                    Toast.makeText(context, "Profile selected $selectedText", Toast.LENGTH_SHORT).show()
                                    val selectedDevice = selectedProfile?.let { it1 -> BluetoothDeviceDomain(selectedProfile?.name, it1.address) }
                                    if (selectedDevice != null) {
                                        blueViewModel.connectToDevice(selectedDevice)
                                    }
                                }

                        } else {
                            Toast.makeText(context, "No profile selected", Toast.LENGTH_SHORT).show()
                        } },
                    ) {
                        Text("Select Profile")
                        // check if null before using
//                        selectedProfile?.let { profile ->
//                            Text(profile.name)
//                        }
                    }

//moved to Profile Screen
//                    if (openDialog.value) {
//
//                        AlertDialog(
//                            onDismissRequest = {
//                                // Dismiss the dialog when the user clicks outside the dialog or on the back
//                                // button. If you want to disable that functionality, simply use an empty
//                                // onCloseRequest.
////                                openDialog.value = false
//                            },
//                            title = {
//                                Text(text = "Bluetooth Menu")
//                            },
//                            text = {
//                                BluetoothScreen(
//                                    state = bluetoothState ,
//                                    onStartScan = blueViewModel::startScan,
//                                    onStopScan = blueViewModel::stopScan,
//                                    onStartServer = blueViewModel::waitForIncomingConnections,
//                                    onDeviceClick = blueViewModel::connectToDevice
//                                )
//                            },
//                            confirmButton = {
//                                Button(
//                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
//
//                                    onClick = {
//                                        openDialog.value = false
//                                    }) {
//                                    Text("Dismiss")
//                                }
//                            },
////                            dismissButton = {
////                                Button(
////                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
////                                    onClick = {
////                                        openDialog.value = false
////                                    }) {
////                                    Text("Dismiss")
////                                }
////                            }
//                        )
//                    }
                    Spacer(modifier = Modifier.width(32.dp))

                    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

                    if (showDeleteConfirmationDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showDeleteConfirmationDialog = false
                            },
                            title = {
                                Text(text = "Confirm Deletion")
                            },
                            text = {
                                Text("Are you sure you want to delete the selected profile?")
                            },
                            confirmButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                                    onClick = {
                                        // Call the deleteByName function to delete the selected profile
                                        viewModel.deleteProfileByName(selectedText)
                                        if (selectedText == rememberedProfile) {
                                            preferencesManager.clearData()
                                            blueViewModel.disconnectFromDevice()
                                        }

                                        // Show a Toast message to inform the user
                                        Toast.makeText(context, "Profile deleted: $selectedText", Toast.LENGTH_SHORT).show()


                                        selectedText = "" // Reset the selectedText

                                        showDeleteConfirmationDialog = false // Close the dialog
                                    }
                                ) {
                                    Text("Delete")
                                }
                            },
                            dismissButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                                    onClick = {
                                        showDeleteConfirmationDialog = false // Close the dialog
                                    }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    ElevatedButton(
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        onClick = {
                            if (selectedText.isNotBlank()) {
                                // Show the delete confirmation dialog
                                showDeleteConfirmationDialog = true
                            } else {
                                Toast.makeText(context, "No profile selected for deletion", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Delete Profile")
                    }
                }

            }
        },
        bottomBar = {
            BottomNavigation(navController)
        }
    )
}

@Composable
fun BluetoothScreen(
    state: BluetoothUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onStartServer: () -> Unit,
    onDeviceClick: (BluetoothDevice) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BluetoothDeviceList(
            pairedDevices = state.pairedDevices,
            scannedDevices = state.scannedDevices,
            onClick = onDeviceClick,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                onClick = onStartScan) {
                Text(text = "Start scan")
            }
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                onClick = onStopScan) {
                Text(text = "Stop scan")
            }
//            Button(onClick = onStartServer) {
//                Text(text = "Start server")
//            }
        }
    }
}
@Composable
fun BluetoothDeviceList(
    pairedDevices: List<BluetoothDevice>,
    scannedDevices: List<BluetoothDevice>,
    onClick: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            Text(
                text = "Paired Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(pairedDevices) { device ->
            Text(
                text = device.name ?: "(No name)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )
        }

        item {
            Text(
                text = "Scanned Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(scannedDevices) { device ->
            Text(
                text = device.name ?: "(No name)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )
        }
    }
}
//@Preview
//@Composable
//fun StartScreenPreview(){
//    val navController = rememberNavController()
//    StartScreen(navController = navController)
//}


@Composable
fun BackButton(){
    OutlinedButton(onClick = {  }) {
        Text("Back")
    }
}



//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ExposedDropdownMenuBox(
//    state: ProfileState,
//    onEvent:(ProfileEvent) -> Unit,
//    profileNamesFlow: Flow<List<String>>
//) {
//
//}
//





//{
//    val context = LocalContext.current
//    val carTypes = arrayOf("cars")
//    var expanded by remember { mutableStateOf(false) }
//    var selectedText by remember { mutableStateOf(carTypes[0]) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(32.dp), contentAlignment = Alignment.Center
//    ) {
//        ExposedDropdownMenuBox(
//            expanded = expanded,
//            onExpandedChange = {
//                expanded = !expanded
//            }
//        ) {
//            TextField(
//                value = selectedText,
//                onValueChange = {},
//                readOnly = true,
//                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//                modifier = Modifier.menuAnchor()
//            )
//
//            ExposedDropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false }
//            ) {
//                carTypes.forEach { item ->
//                    DropdownMenuItem(
//                        text = { Text(text = item) },
//                        onClick = {
//                            selectedText = item
//                            expanded = false
//                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
//                        }
//                    )
//                }
//            }
//        }
//    }
//}