package com.example.digitalkeyfob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newprofileButton = findViewById<Button>(R.id.AddProfile)

        // Set a click listener for the button
        newprofileButton.setOnClickListener {
            // Create an Intent to launch the SecondaryActivity
            val intent = Intent(this, NewProfileActivity::class.java)
            startActivity(intent)
        }
    }
}