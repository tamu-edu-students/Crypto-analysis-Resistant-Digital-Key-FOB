package com.example.digitalkeyfob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.TextView

class CommandCenter : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.command_center)


        val commandtomain = findViewById<Button>(R.id.btncommandtomain)
        val commandtitle = findViewById<TextView>(R.id.tvCommandTitle)
        // Set a click listener for the button
        commandtomain.setOnClickListener {
            // Create an Intent to launch the SecondaryActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val name = intent.getStringExtra("Selected_Profile")
        commandtitle.text = name
    }
}