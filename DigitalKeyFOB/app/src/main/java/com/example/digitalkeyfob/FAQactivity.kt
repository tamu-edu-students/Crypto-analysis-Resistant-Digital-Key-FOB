package com.example.digitalkeyfob

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class FAQactivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.faq) // Set the layout for the secondary activity

        val faqtomain = findViewById<Button>(R.id.btnfaqtomain)
        // Set a click listener for the button
        faqtomain.setOnClickListener {
            // Create an Intent to return to main
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }
    }
}