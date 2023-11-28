package com.example.digitalkeyfobcomp.BluetoothSetup

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
// Install the module in the SingletonComponent, meaning the provided dependencies are singletons
@InstallIn(SingletonComponent::class)
object AppModule {

    // Provides a singleton instance of BluetoothController
    @Provides
    @Singleton
    fun provideBluetoothController(@ApplicationContext context: Context): BluetoothController {
        // Create and return an instance of AndroidBluetoothController, passing the application context
        return AndroidBluetoothController(context)
    }
}
