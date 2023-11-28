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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
                        Text(text = "Digital Key FOB - FAQ", fontWeight = FontWeight.Bold)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFF4F4F4)), // Light Gray background color
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 70.dp) // Add top padding
                ) {

                    // Boilerplate FAQ questions
                    item {
                       ExpandableCard("What is the purpose of this app?", "This app is designed " +
                               "to streamline your daily tasks by providing an intuitive " +
                               "interface for managing your vehicles.")
                    }
                    item {
                        ExpandableCard("How do I get started?", "Register your vehicle and pair it with the app by following the provided instructions. Once linked, simply approach your vehicle and use the app to unlock or start it.")
                    }
                    item {
                        ExpandableCard("Is my data secure?", "We prioritize the security of your vehicles. The app implements robust encryption methods to ensure that your vehicle access data remains secure and inaccessible to unauthorized parties.")
                    }
                    item {
                        ExpandableCard("What devices are supported?", "The app is compatible with Android operating systems. It's optimized for the latest smartphone models to ensure a reliable and consistent experience.")
                    }
                    item {
                        ExpandableCard("How can I contact support?", "For any assistance, questions, or concerns, you can contact our support team at support@example.com. Our dedicated support staff is ready to assist with any issues you might encounter while using the app.")
                    }

                    // Add more FAQ questions as needed

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        },
        bottomBar = {
            BottomNavigation(navController)
        }
    )
}
@Preview
@Composable
fun FaqScreenPreview(){
    val navController = rememberNavController()
    FaqScreen(navController)
}


//@Preview
//@Composable
//fun FaqScreenPreview(){
//    ExpandableCard(title = "Hello")
//}
@Composable
fun ExpandableCard(question: String, answer: String) {

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) ,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
    {
        FAQQuestion(question = question, answer = answer)
    }
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