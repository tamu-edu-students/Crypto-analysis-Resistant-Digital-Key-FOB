package com.example.digitalkeyfobcomp.screens

import android.annotation.SuppressLint
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digitalkeyfobcomp.PreferencesManager
import com.example.digitalkeyfobcomp.ProfileEntity
import com.example.digitalkeyfobcomp.components.BottomNavigation
import com.example.digitalkeyfobcomp.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlScreen(navController: NavController) {
    val preferencesManager = PreferencesManager(LocalContext.current)

    val ledSize = 24.dp
    val imagesize = 60
    var isUnlocked by remember { mutableStateOf(false) }
    var isEngineOn by remember { mutableStateOf(false) }
    var rememberedProfile by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        // This block of code is executed only once when the Composable is initially displayed
        val retrievedProfile: ProfileEntity? =  preferencesManager.getData("selectedProfile", null)
        retrievedProfile?.let { profile ->
            rememberedProfile = profile.name
        }
    }


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
                modifier = Modifier.fillMaxSize().background(color = Color.LightGray),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row{
                    Text("Selected Profile: $rememberedProfile")
                }
                Spacer(modifier = Modifier.height(20.dp))
                // Create LED-like indicators for unlocked/locked and engine on/off states
                Row {
                    LEDIndicator(isOn = isUnlocked)
                    Text("Locked", modifier = Modifier.padding(start = 8.dp))
                    Spacer(modifier = Modifier.width(40.dp))
                    LEDIndicator(isOn = isEngineOn)
                    Text("Engine On", modifier = Modifier.padding(start = 8.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Add the car image
                Box(
                    modifier = Modifier.fillMaxHeight(.5f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.car1),
                        contentDescription = "Car Image",
                        modifier = Modifier.size(400.dp, 400.dp)
                    )
                    Column {
                            Image(
                                painter = painterResource(R.drawable.car_engine_2061910),
                                contentDescription = "Description for accessibility",
                                contentScale = ContentScale.Fit,

                                modifier = Modifier
                                    .size(imagesize.dp, imagesize.dp)
                                    .clickable { isEngineOn = !isEngineOn }
                            )
                            Spacer(modifier = Modifier.height(170.dp))
                            Image(
                                painter = painterResource(R.drawable.lock_2913133),
                                contentDescription = "Description for accessibility",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(imagesize.dp, imagesize.dp)
                                    .clickable { isUnlocked = !isUnlocked }
                            )
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
//fun ControlScreen(navController: NavController) {
//    val imagesize = 60
//    var isUnlocked by remember { mutableStateOf(true) }
//    var isEngineOn by remember { mutableStateOf(false) }
//
//    Scaffold(
//        topBar = {
//            Surface(
//                modifier = Modifier.fillMaxWidth(),
//                color = Color.White, // Set the background color here
//
//            ) {
//                TopAppBar(
//                    title = {
//                        Text(text = "Digital Key FOB", fontWeight = FontWeight.Bold)
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//
//                    )
//            }
//        },
//        content = {
//            Column(
//                modifier = Modifier.fillMaxSize()
//                    .background(color = Color.LightGray),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                // The rest of your content
//                var checked by remember { mutableStateOf(true) }
//
//                Row (
//
//                ){
//                    Text(text = "Unlocked")
//
//                }
//
//                Spacer(modifier = Modifier.height(50.dp))
//                Box (
//                    modifier = Modifier
//                        .fillMaxHeight(.5.toFloat())
//                        .fillMaxWidth(),
//                    contentAlignment = Alignment.Center
//                ){
//
//                        Image(
//                            painter = painterResource(R.drawable.car1),
//                            contentDescription = "Description for accessibility",
//                            modifier = Modifier
//                                .size(400.dp, 400.dp)
//
//                        )
//                        Column {
//                            Image(
//                                painter = painterResource(R.drawable.car_engine_2061910),
//                                contentDescription = "Description for accessibility",
//                                contentScale = ContentScale.Fit,
//
//                                modifier = Modifier
//                                    .size(imagesize.dp, imagesize.dp)
//                                    .clickable { println("Button Clicked!") }
//                            )
//                            Spacer(modifier = Modifier.height(170.dp))
//                            Image(
//                                painter = painterResource(R.drawable.lock_2913133),
//                                contentDescription = "Description for accessibility",
//                                contentScale = ContentScale.Fit,
//                                modifier = Modifier
//                                    .size(imagesize.dp, imagesize.dp)
//                                    .clickable { println("Button Clicked!") }
//                            )
//                        }
//                }
//            }
//        },
//        bottomBar = {
//            BottomNavigation(navController)
//        }
//    )
//}
@Preview
@Composable
fun ControlScreenPreview(){
    val navController = rememberNavController()
    ControlScreen(navController)
}