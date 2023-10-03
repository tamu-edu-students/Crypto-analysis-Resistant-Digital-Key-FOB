package com.example.digitalkeyfob


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class NewProfileActivity : AppCompatActivity(){

    val cars = arrayOf("Porche911","Formula1Redbull","MercedesAMG")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.newprofile) // Set the layout for the new profile activity
        val btnprofiletomain = findViewById<Button>(R.id.btnprofiletomain)
        // Set a click listener for the button
        btnprofiletomain.setOnClickListener {
            // Create an Intent to launch the SecondaryActivity
            val intent2 = Intent(this, MainActivity::class.java)
            startActivity(intent2)
        }

        val spinner = findViewById<Spinner>(R.id.spProfileList)
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,cars)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

    }

}
