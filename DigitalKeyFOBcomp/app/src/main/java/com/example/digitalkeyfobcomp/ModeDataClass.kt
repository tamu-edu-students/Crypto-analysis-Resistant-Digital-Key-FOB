package com.example.digitalkeyfobcomp



data class CarModes(
    var locked: Boolean = false, // Represents the locked state of the car. Default is unlocked (false).
    var engine: Boolean = false  // Represents the engine state of the car. Default is off (false).
)
