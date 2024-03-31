package com.example.digitalkeyfobcomp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.digitalkeyfobcomp.BluetoothSetup.BluetoothViewModel
import com.example.digitalkeyfobcomp.ui.theme.DigitalKeyFOBCompTheme
import com.example.digitalkeyfobcomp.components.Navigation
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text

import androidx.compose.ui.Alignment
import com.example.digitalkeyfobcomp.screens.ChatScreen
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Lazy initialization of the Room database for profiles
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ProfileDatabase::class.java,
            "profiles.db"
        ).build()
    }

    // Lazy initialization of the ProfileViewModel using Hilt's viewModels delegate
    private val viewModel by viewModels<ProfileViewModel>(
        factoryProducer = {
            // Custom ViewModelProvider.Factory to provide the ProfileViewModel with the DAO
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(db.dao) as T
                }
            }
        }
    )

    // Bluetooth Initialization

    // Lazy initialization of BluetoothManager
    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }

    // Lazy initialization of BluetoothAdapter
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    // Property to check if Bluetooth is enabled
    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activity result launcher to enable Bluetooth
        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        // Activity result launcher to request Bluetooth-related permissions
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            // Check if Bluetooth-related permissions are granted
            val canEnableBluetooth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            // If permissions are granted and Bluetooth is not enabled, launch the enable Bluetooth activity
            if (canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }

        // Request Bluetooth-related permissions (BLUETOOTH_SCAN and BLUETOOTH_CONNECT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                )
            )
        }

        setContent {
            DigitalKeyFOBCompTheme(darkTheme = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Hilt ViewModel for Bluetooth functionality
                    val viewModel1 = hiltViewModel<BluetoothViewModel>()

                    // Collect BluetoothViewModel state as a State
                    val state1 by viewModel1.state.collectAsState()

                    // Display a toast message for any error message in the BluetoothViewModel state
                    LaunchedEffect(key1 = state1.errorMessage) {
                        state1.errorMessage?.let { message ->
                            Toast.makeText(
                                applicationContext,
                                message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    LaunchedEffect(key1 = state1.userMessage) {
                        state1.userMessage?.let { message ->
                            Toast.makeText(
                                applicationContext,
                                message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    // Display a toast message when the Bluetooth connection is established
                    LaunchedEffect(key1 = state1.isConnected) {
                        if (state1.isConnected) {
                            Toast.makeText(
                                applicationContext,
                                "You're connected!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    // Collect the state from the ProfileViewModel
                    val state by viewModel.state.collectAsState()

                    // Choose the appropriate UI based on the profile and Bluetooth states
                    when {
                        state1.isConnecting -> {
                            // Display a progress indicator and "Connecting..." text
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                                Text(text = "Connecting...")
                            }
                        }
                        //new state
                        state1.isRegistering -> {
                            // Display a progress indicator and "Connecting..." text
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                                Text(text = "Registering your device...")
                            }
                        }
                        else -> {
                            // Navigate to the appropriate screen based on the ProfileViewModel state
                            Navigation(
                                state = state,
                                onEvent = viewModel::onEvent,
                                profileNamesFlow = viewModel.profileNames,
                                viewModel = viewModel,
                                blueViewModel = viewModel1,
                                bluetoothState = state1
                            )
                        }
                    }
                }
            }
        }
    }
}
