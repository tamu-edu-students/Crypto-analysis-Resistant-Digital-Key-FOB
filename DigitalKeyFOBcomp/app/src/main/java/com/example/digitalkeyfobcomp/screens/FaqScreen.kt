package com.example.digitalkeyfobcomp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digitalkeyfobcomp.components.BottomNavigation

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun FaqScreen(navController: NavController) {
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
            ) {
                TopAppBar(
                    title = {
                        Text(text = "Digital Key FOB", fontWeight = FontWeight.Bold)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        content = {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(color = Color(0xFFF4F4F4)), // Light Gray background color
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(top = 56.dp) // Add top padding
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Frequently Asked Questions",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF6200EE) // Dark Purple text color
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Boilerplate FAQ questions
                    item {
                        FAQQuestion("What is the purpose of this app?", "This app is designed to...")
                    }
                    item {
                        FAQQuestion("How do I get started?", "To get started with the app, follow these steps...")
                    }
                    item {
                        FAQQuestion("Is my data secure?", "We take data security seriously...")
                    }
                    item {
                        FAQQuestion("What devices are supported?", "Our app is available on...")
                    }
                    item {
                        FAQQuestion("How can I contact support?", "You can reach our support team at support@example.com.")
                    }

                    // Add more FAQ questions as needed

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        },
        bottomBar = {
            BottomNavigation(navController)
        }
    )
}

//@Composable
//fun FaqScreen(navController: NavController) {
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
//
//
//                Spacer(modifier = Modifier.height(16.dp))
//                Text(text = "Contact Jeremy Hein for assistance")
//                //content
//
//            }
//        },
//        bottomBar = {
//            BottomNavigation(navController)
//        }
//    )
//}
@Preview
@Composable
fun FaqScreenPreview(){
    val navController = rememberNavController()
    FaqScreen(navController)
}
@Composable
fun FAQQuestion(question: String, answer: String) {
    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text = question,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF333333) // Dark Gray text color
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = answer,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color(0xFF333333) // Dark Gray text color
            )
        )
    }
}