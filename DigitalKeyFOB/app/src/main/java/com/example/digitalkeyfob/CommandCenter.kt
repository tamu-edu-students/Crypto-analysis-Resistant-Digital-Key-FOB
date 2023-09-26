package com.example.digitalkeyfob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button

class CommandCenter : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.command_center)


        val backbutton = findViewById<Button>(R.id.back2main)

        // Set a click listener for the button
        backbutton.setOnClickListener {
            // Create an Intent to launch the SecondaryActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}