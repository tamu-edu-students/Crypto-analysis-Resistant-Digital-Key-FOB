package com.example.digitalkeyfobcomp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digitalkeyfobcomp.components.BottomNavigation
import com.example.digitalkeyfobcomp.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlScreen(navController: NavController) {
    val imagesize = 60
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
                var checked by remember { mutableStateOf(true) }

                Row (

                ){
                    Text(text = "Unlocked")
                    Text(text = "On")
                }

                Box (
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){

                        Image(
                            painter = painterResource(R.drawable.car1),
                            contentDescription = "Description for accessibility",
                            modifier = Modifier
                                .size(400.dp, 400.dp)

                        )
                        Column {
                            Image(
                                painter = painterResource(R.drawable.car_engine_2061910),
                                contentDescription = "Description for accessibility",
                                contentScale = ContentScale.Fit,

                                modifier = Modifier
                                    .size(imagesize.dp, imagesize.dp)
                                    .clickable { println("Button Clicked!") }
                            )
                            Spacer(modifier = Modifier.height(170.dp))
                            Image(
                                painter = painterResource(R.drawable.lock_2913133),
                                contentDescription = "Description for accessibility",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(imagesize.dp, imagesize.dp)
                                    .clickable { println("Button Clicked!") }
                            )
                        }


                }





                //content

            }
        },
        bottomBar = {
            BottomNavigation(navController)
        }
    )
}
@Preview
@Composable
fun ControlScreenPreview(){
    val navController = rememberNavController()
    ControlScreen(navController)
}