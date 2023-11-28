package com.example.digitalkeyfobcomp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
// Class responsible for managing shared preferences using Gson for serialization/deserialization
class PreferencesManager(context: Context) {
    // Instance of SharedPreferences for storing key-value pairs
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    // Gson instance for converting objects to JSON and vice versa
    val gson = Gson()

    // Function to save data of any type in shared preferences
    fun saveData(key: String, value: Any) {
        // Obtain the SharedPreferences editor for making modifications
        val editor = sharedPreferences.edit()

        // Convert the value to JSON using Gson
        val json = gson.toJson(value)

        // Save the JSON representation of the value with the corresponding key
        editor.putString(key, json)

        // Apply the changes to the SharedPreferences
        editor.apply()
    }

    // Inline function to retrieve data of a specific type from shared preferences
    inline fun <reified T> getData(key: String, defaultValue: T): T {
        // Retrieve the JSON string representation of the value stored with the key
        val json = sharedPreferences.getString(key, null)

        // If JSON is not null, deserialize it to the specified type using Gson
        return if (json != null) {
            gson.fromJson(json, T::class.java)
        } else {
            // If JSON is null, return the default value
            defaultValue
        }
    }

    // Function to remove a specific key and its associated value from shared preferences
    fun removeKey(key: String) {
        // Obtain the SharedPreferences editor for making modifications
        val editor = sharedPreferences.edit()

        // Remove the key and its associated value
        editor.remove(key)

        // Apply the changes to the SharedPreferences
        editor.apply()
    }

    // Function to clear all data from shared preferences
    fun clearData() {
        // Obtain the SharedPreferences editor for making modifications
        val editor = sharedPreferences.edit()

        // Clear all data from shared preferences
        editor.clear()

        // Apply the changes to the SharedPreferences
        editor.apply()
    }
}
