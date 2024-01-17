package com.example.digitalkeyfobcomp.BluetoothSetup

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
// Application class for the Bluetooth application, enabling Dagger Hilt for dependency injection
class BluetoothApp: Application()