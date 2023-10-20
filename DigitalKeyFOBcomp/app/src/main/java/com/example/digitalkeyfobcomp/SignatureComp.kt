package com.example.digitalkeyfobcomp.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import se.warting.signaturepad.SignaturePadAdapter
import se.warting.signaturepad.SignaturePadView

//@Composable
//fun SignatureComp(){
//    var signaturePadAdapter: SignaturePadAdapter? = null
//    val mutableSvg = remember { mutableStateOf("") }
//    Box (
//
//        modifier = Modifier
//            .size(300.dp,200.dp)
//            .border(width = 2.dp, color = Color.DarkGray),
//        contentAlignment = Alignment.Center
//    ){
//        SignaturePadView(onReady = {
//            signaturePadAdapter = it
//        })
//    }
//    Row {
//        Button(onClick = {
//            mutableSvg.value = signaturePadAdapter?.getSignatureSvg() ?: ""
//        }) {
//            Text("Save")
//        }
//        Button(onClick = {
//            mutableSvg.value = ""
//            signaturePadAdapter?.clear()
//        }) {
//            Text("Clear")
//        }
//    }
//}