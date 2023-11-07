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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val db by lazy{ // database initialization
        Room.databaseBuilder(
            applicationContext,
            ProfileDatabase::class.java,
            "profiles.db"
        ).build()
    }
    private val viewModel by viewModels<ProfileViewModel>( // profileviewmodel initialization
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(db.dao) as T
                }
            }
        }
    )

    // Bluetooth Initialization

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            if(canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                )
            )
        }

        setContent {
            DigitalKeyFOBCompTheme(darkTheme = true){
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val viewModel1 = hiltViewModel<BluetoothViewModel>()
                    val state1 by viewModel1.state.collectAsState()
                    LaunchedEffect(key1 = state1.errorMessage) {
                        state1.errorMessage?.let { message ->
                            Toast.makeText(
                                applicationContext,
                                message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    LaunchedEffect(key1 = state1.isConnected) {
                        if(state1.isConnected) {
                            Toast.makeText(
                                applicationContext,
                                "You're connected!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    val state by viewModel.state.collectAsState()

                    when {
                        state1.isConnecting -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                                Text(text = "Connecting...")
                            }
                        }
                        else -> {
                            Navigation(state = state, onEvent = viewModel::onEvent, profileNamesFlow = viewModel.profileNames, viewModel = viewModel,  blueViewModel = viewModel1, bluetoothState = state1 )
                        }
                    }

                }
            }
        }
    }
}

