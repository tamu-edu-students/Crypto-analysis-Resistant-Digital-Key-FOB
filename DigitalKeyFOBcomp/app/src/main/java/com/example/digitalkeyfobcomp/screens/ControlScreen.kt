package com.example.digitalkeyfobcomp.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection.Companion.Content
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothUiState
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothViewModel
import com.example.digitalkeyfobcomp.CarModes
import com.example.digitalkeyfobcomp.PreferencesManager
import com.example.digitalkeyfobcomp.ProfileEntity
import com.example.digitalkeyfobcomp.ProfileEvent
import com.example.digitalkeyfobcomp.components.BottomNavigation
import com.example.digitalkeyfobcomp.R
import com.example.digitalkeyfobcomp.bitmapToHash
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlScreen(
    navController: NavController,
    blueViewModel: BluetoothViewModel,
    bluetoothState: BluetoothUiState
) {
    // Initialize PreferencesManager for managing shared preferences
    val preferencesManager = PreferencesManager(LocalContext.current)

    // Constants for UI styling
    val ledSize = 24.dp
    val imagesize = 30
    var rememberedProfile by remember { mutableStateOf("") }
    val context = LocalContext.current
    var message = 0
    val coroutineScope = rememberCoroutineScope()
    var selectedProfile by remember { mutableStateOf<ProfileEntity?>(null) }
    var selectedCarModes by remember { mutableStateOf(CarModes(locked = false, engine = false)) }

    // LaunchedEffect block to execute code only once when the Composable is initially displayed
    LaunchedEffect(Unit) {
        val retrievedProfile: ProfileEntity? = preferencesManager.getData("selectedProfile", null)
        retrievedProfile?.let { profile ->
            selectedProfile = profile
            rememberedProfile = profile.name
        }
        val retrievedModes: CarModes? = preferencesManager.getData("selectedProfileMode", null)
        if (retrievedModes != null) {
            selectedCarModes = retrievedModes
        }
    }

    // Scaffold for the overall screen structure
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
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFF4F4F4)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Create LED-like indicators for unlocked/locked and engine on/off states
                Row {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.width(300.dp).padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LEDIndicator(isOn = selectedCarModes.locked)
                            Text("Locked", modifier = Modifier
                                .padding(start = 8.dp),
                                fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(20.dp))
                            LEDIndicator(isOn = selectedCarModes.engine)
                            Text("Engine On",
                                modifier = Modifier.padding(start = 8.dp),
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Change Engine Mode button
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.padding(16.dp).clickable {
                        if (bluetoothState.isConnected) {
                            selectedCarModes.engine = !selectedCarModes.engine
                            message = booleanToInt(selectedCarModes.engine)
                            blueViewModel.sendMessage("Mode0$message$rememberedProfile*")
                            Toast
                                .makeText(
                                    context,
                                    "Mode0$message$rememberedProfile",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        } else {
                            Toast
                                .makeText(
                                    context,
                                    "No Device Connected",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                        coroutineScope.launch {
                            selectedCarModes.let { selectedCarModes -> preferencesManager.saveData("selectedProfileMode", selectedCarModes) }
                        }
                    }
                ) {
                    Row (
                        modifier = Modifier.width(300.dp).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Image(
                            painter = painterResource(R.drawable.car_engine_2061910),
                            contentDescription = "Description for accessibility",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(imagesize.dp, imagesize.dp),
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("Change Engine Mode", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                // Change Lock Mode button
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.padding(16.dp)
                        .clickable {
                            if (bluetoothState.isConnected) {
                                selectedCarModes.locked = !selectedCarModes.locked
                                message = booleanToInt(selectedCarModes.locked)
                                blueViewModel.sendMessage("Mode1$message$rememberedProfile*")
                                Toast
                                    .makeText(context, "Mode1$message$rememberedProfile", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "No Device Connected",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                            coroutineScope.launch {
                                selectedCarModes.let { selectedCarModes -> preferencesManager.saveData("selectedProfileMode", selectedCarModes) }
                            }
                        }
                ) {
                    Row (
                        modifier = Modifier.width(300.dp).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Image(
                            painter = painterResource(R.drawable.lock_2913133),
                            contentDescription = "Description for accessibility",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(imagesize.dp, imagesize.dp)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("Change Lock Mode", color = Color.Black, fontWeight = FontWeight.Bold)
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
fun LEDIndicator(isOn: Boolean) {
    val ledColor = if (isOn) Color.Green else Color.Red
    val borderColor = Color.Black
    val borderWidth = 1.dp

    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(ledColor, CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color.Transparent, CircleShape)
                    .border(borderWidth, borderColor, CircleShape)
            )
        }
    }
}

@Composable
fun ExpandableCardControl(question: String, answer: String) {
    // Placeholder for future expansion of the UI
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) ,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
    {
        // Content of the expandable card goes here
    }
}

// Function to convert boolean to integer
fun booleanToInt(b: Boolean): Int {
    return if (b) 1 else 0
}
