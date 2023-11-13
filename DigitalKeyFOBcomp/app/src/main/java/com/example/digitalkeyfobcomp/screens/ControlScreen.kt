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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.digitalkeyfobcomp.PreferencesManager
import com.example.digitalkeyfobcomp.ProfileEntity
import com.example.digitalkeyfobcomp.components.BottomNavigation
import com.example.digitalkeyfobcomp.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlScreen(navController: NavController,
                  blueViewModel: BluetoothViewModel,
                  bluetoothState: BluetoothUiState
) {
    val preferencesManager = PreferencesManager(LocalContext.current)

    val ledSize = 24.dp
    val imagesize = 30
    var isUnlocked by remember { mutableStateOf(false) }
    var isEngineOn by remember { mutableStateOf(false) }
    var rememberedProfile by remember { mutableStateOf("") }
    val context = LocalContext.current
    var message = ""

    LaunchedEffect(Unit) {
        // This block of code is executed only once when the Composable is initially displayed
        val retrievedProfile: ProfileEntity? =  preferencesManager.getData("selectedProfile", null)
        retrievedProfile?.let { profile ->
            rememberedProfile = profile.name
        }
    }
    val messageProfile = rememberedProfile
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White, // Set the background color here

            ) {
                TopAppBar(
                    title = {
                        Text(text = "Digital Key FOB - Current Profile: $rememberedProfile", fontWeight = FontWeight.Bold)
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
                            LEDIndicator(isOn = isUnlocked)
                            Text("Locked", modifier = Modifier
                                .padding(start = 8.dp),
                                fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(20.dp))
                            LEDIndicator(isOn = isEngineOn)
                            Text("Engine On",
                                modifier = Modifier.padding(start = 8.dp),
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.padding(16.dp).clickable {
                        if (bluetoothState.isConnected) {
                            isEngineOn = !isEngineOn
                            message = isEngineOn.toString()
                            blueViewModel.sendMessage("Engine Mode for $messageProfile is $message")
                            Toast
                                .makeText(
                                    context,
                                    "Message sent",
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
                                .size(imagesize.dp, imagesize.dp)
                                ,

                            )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("Change Engine Mode", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.padding(16.dp)
                        .clickable {
                            if (bluetoothState.isConnected) {
                                isUnlocked = !isUnlocked
                                message = isUnlocked.toString()
                                blueViewModel.sendMessage("Unlocked Mode $messageProfile is $message")
                                Toast
                                    .makeText(context, "Message sent", Toast.LENGTH_SHORT)
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

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) ,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
    {

    }
}

//Box(
//modifier = Modifier.fillMaxHeight(.5f).fillMaxWidth(),
//contentAlignment = Alignment.Center
//) {
//    Image(
//        painter = painterResource(R.drawable.car1),
//        contentDescription = "Car Image",
//        modifier = Modifier.size(400.dp, 400.dp)
//    )
//    Column {
//        Image(
//            painter = painterResource(R.drawable.car_engine_2061910),
//            contentDescription = "Description for accessibility",
//            contentScale = ContentScale.Fit,
//            modifier = Modifier
//                .size(imagesize.dp, imagesize.dp)
//                .clickable {
//                    if(bluetoothState.isConnected) {
//                        isEngineOn = !isEngineOn
//                        message = isEngineOn.toString()
//                        blueViewModel.sendMessage("Engine Mode for $messageProfile is $message")
//                        Toast.makeText(
//                            context,
//                            "Message sent",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }else{
//                        Toast.makeText(context, "No Device Connected", Toast.LENGTH_SHORT).show()
//                    }
//                },
//
//            )
//        Spacer(modifier = Modifier.height(170.dp))
//        Image(
//            painter = painterResource(R.drawable.lock_2913133),
//            contentDescription = "Description for accessibility",
//            contentScale = ContentScale.Fit,
//            modifier = Modifier
//                .size(imagesize.dp, imagesize.dp)
//                .clickable {
//                    if(bluetoothState.isConnected) {
//                        isUnlocked = !isUnlocked
//                        message = isUnlocked.toString()
//                        blueViewModel.sendMessage("Unlocked Mode $messageProfile is $message")
//                        Toast.makeText(context, "Message sent", Toast.LENGTH_SHORT).show()
//                    }else{
//                        Toast.makeText(context, "No Device Connected", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        )
//    }