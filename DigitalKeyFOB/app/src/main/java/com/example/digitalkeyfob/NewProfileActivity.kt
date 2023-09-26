package com.example.digitalkeyfob

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class NewProfileActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.newprofile) // Set the layout for the secondary activity

        val backtomainfromnewprofile = findViewById<Button>(R.id.backmainfromprofile)
        // Set a click listener for the button
        backtomainfromnewprofile.setOnClickListener {
            // Create an Intent to launch the SecondaryActivity
            val intent2 = Intent(this, MainActivity::class.java)

            startActivity(intent2)
        }
    }
}