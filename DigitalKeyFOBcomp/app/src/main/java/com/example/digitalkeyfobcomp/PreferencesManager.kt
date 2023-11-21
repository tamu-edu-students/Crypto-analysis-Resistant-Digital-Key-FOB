package com.example.digitalkeyfobcomp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class PreferencesManager(context: Context) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val gson = Gson()

    fun saveData(key: String, value: Any) {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(value)
        editor.putString(key, json)
        editor.apply()
    }

    inline fun <reified T> getData(key: String, defaultValue: T): T {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            gson.fromJson(json, T::class.java)
        } else {
            defaultValue
        }
    }
    fun removeKey(key: String) {
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }
    fun clearData(){
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}