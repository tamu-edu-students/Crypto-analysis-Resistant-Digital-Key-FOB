package com.example.digitalkeyfobcomp.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digitalkeyfobcomp.PreferencesManager
import com.example.digitalkeyfobcomp.ProfileEntity
import com.example.digitalkeyfobcomp.ProfileEvent
import com.example.digitalkeyfobcomp.ProfileState
import com.example.digitalkeyfobcomp.ProfileViewModel
import com.example.digitalkeyfobcomp.components.BottomNavigation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(navController: NavController,
                state: ProfileState,
                onEvent:(ProfileEvent) -> Unit,
                profileNamesFlow: Flow<List<String>>,
                viewModel: ProfileViewModel
                ) {
//    var selectedProfile by remember { mutableStateOf<String?>(null) }
    val preferencesManager = PreferencesManager(LocalContext.current)

    var selectedProfile by remember { mutableStateOf<ProfileEntity?>(null) }


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
                    .background(color = Color.LightGray),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // The rest of your content


                val context = LocalContext.current
                val profileNames by profileNamesFlow.collectAsState(initial = emptyList())
                var expanded by remember { mutableStateOf(false) }
                var selectedText by remember { mutableStateOf(profileNames.firstOrNull() ?: "") }
                var rememberedProfile by remember { mutableStateOf("") }
                LaunchedEffect(Unit) {
                    // This block of code is executed only once when the Composable is initially displayed
                    val retrievedProfile: ProfileEntity? =  preferencesManager.getData("selectedProfile", null)
                    retrievedProfile?.let { profile ->
                        rememberedProfile = profile.name
                    }
                    selectedText = rememberedProfile
                }



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



                val coroutineScope = rememberCoroutineScope()

                Row(){
                    OutlinedButton(
                        onClick = {
                            if (selectedText.isNotBlank()) {
                            // Call the deleteByName function to delete the selected profile
                                preferencesManager.clearData()
                                coroutineScope.launch {
                                    val profile = viewModel.getProfileByName(selectedText)
                                    selectedProfile = profile
                                    selectedProfile?.let { profile -> preferencesManager.saveData("selectedProfile", profile) }
                                    Toast.makeText(context, "Profile selected $selectedText", Toast.LENGTH_SHORT).show()
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
                    Spacer(modifier = Modifier.width(32.dp))
                    OutlinedButton(
                        onClick = {

                            if (selectedText.isNotBlank()) {
                                // Call the deleteByName function to delete the selected profile
                                viewModel.deleteProfileByName(selectedText)
                                if(selectedText==rememberedProfile){
                                    preferencesManager.clearData()
                                }

                            } else {
                                Toast.makeText(context, "No profile selected for deletion", Toast.LENGTH_SHORT).show()
                            }
                            selectedText = ""
                             },
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