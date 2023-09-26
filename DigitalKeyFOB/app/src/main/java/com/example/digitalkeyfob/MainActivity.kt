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
            // Create an Intent to launch the New Profile
            val intent = Intent(this, NewProfileActivity::class.java)

            startActivity(intent)
        }

        val commandcenterbutton = findViewById<Button>(R.id.ProfileSelect)

        // Set a click listener for the button
        commandcenterbutton.setOnClickListener {
            // Create an Intent to launch the Command Center
            val intent = Intent(this, CommandCenter::class.java)

            startActivity(intent)
        }

    }
}