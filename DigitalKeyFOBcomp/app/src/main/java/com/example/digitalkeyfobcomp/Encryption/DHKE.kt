package com.example.digitalkeyfobcomp.Encryption

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.math.BigInteger
import java.security.SecureRandom

fun twoPair(B: BigInteger, a: Int): Pair<BigInteger, Int> = B to a // equivalent to Pair("Ali", 33)

fun DHKEBefore(): Pair<BigInteger, Int> {
//    Surface(color = Color.LightGray) {
        //Setting P and G - P is the prime number and G is the generator (primative root of P)
        val P = BigInteger("13")
        val G = BigInteger("7")

        //Creating the random A for the Vehicle
        val randInt = SecureRandom() //special randomization that follow standard cryptography protocols
        val a = randInt.nextInt(15) // pass to DHKE after

        //Doing the Diffie-Hellman Process (G^a mod P)
        var A = BigInteger("2")
        A = (G.pow(a)) % P //mathematics

        val retunval = twoPair(A,a)
        return retunval
        //Outputs the text to use for demonstration and debugging purposes
//        Text(text = "Before Vehicle: P = $P \n\nG = $G" +
//                "\n\nA Val = $a" +
//                "\n\nVehicle DHKE Portion = $A",
//            modifier = modifier.padding(12.dp))
//    }
}

fun DHKEAfter(B: BigInteger, a: Int): BigInteger {
//    Surface(color = Color.LightGray) {
        //Setting the Values of P, A, and B
        //P is predetermined, A is calculated before, and B is taken from the vehicle.
        val P = BigInteger("13")
//        val a: Int = 1 //This has to be changed with the unique A value from the first part.

//        val B = BigInteger("7") //Add in the value of B from the vehicle here.

        //Calculating the final secret key - B^a mod P
        val sk = (B.pow(a) % P)
        //Text Output for demonstration and debugging.
//        Text(text = "Testing: \nSk = $sk", modifier = modifier.padding(12.dp))
        return sk
}