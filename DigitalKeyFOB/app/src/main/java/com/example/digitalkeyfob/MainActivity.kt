package com.example.digitalkeyfob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newprofileButton = findViewById<Button>(R.id.btneditprofile)
        val commandcenterbutton = findViewById<Button>(R.id.btnProfileSelect)
        val faqpage = findViewById<Button>(R.id.btnfaqselect)
        val spinner1 = findViewById<Spinner>(R.id.spProfileSelect)

        // Set a click listener for the newprofileButton
        newprofileButton.setOnClickListener {
            // Create an Intent to launch the New Profile
            val intent = Intent(this, NewProfileActivity::class.java)

            startActivity(intent)
        }

        // Set a click listener for the commandcenterbutton
        commandcenterbutton.setOnClickListener {
            // Create an Intent to launch the Command Center
            val carname = spinner1.selectedItem.toString()

            Intent(this, CommandCenter::class.java).also{
                it.putExtra("Selected_Profile",carname)
                startActivity(it)
            }

        }

        // Set a click listener for the faqpage
        faqpage.setOnClickListener {
            // Create an Intent to launch the FAQ Page
            val intent = Intent(this, FAQactivity::class.java)

            startActivity(intent)
        }

        val cars = arrayOf("Porche911","Formula1Redbull","MercedesAMG")
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,cars)
        spinner1.adapter = arrayAdapter
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

    }

}