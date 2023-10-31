package com.example.digitalkeyfobcomp.components

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import se.warting.signaturepad.SignaturePadAdapter
import se.warting.signaturepad.SignaturePadView

private const val SIGNATURE_PAD_HEIGHT = 120

@Suppress("LongMethod")
@Composable
fun SignaturePad() {
    MaterialTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = Color.LightGray) {

            val mutableSvg = remember { mutableStateOf("") }
            Column {

                var signaturePadAdapter: SignaturePadAdapter? = null
                val penColor = remember { mutableStateOf(Color.Black) }

                Box(
                    modifier = Modifier
                        .height(SIGNATURE_PAD_HEIGHT.dp)
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = Color.Red,
                        )
                ) {
                    SignaturePadView(
                        onReady = {
                            signaturePadAdapter = it
                        },
                        penColor = penColor.value,

                        onStartSigning = {
                            Log.d("SignedListener", "OnStartSigning")
                        },
                        onSigning = {
                            Log.d("SignedListener", "onSigning")
                        },
                        onSigned = {
                            Log.d("SignedListener", "onSigned")
                        },
                        onClear = {
                            Log.d(
                                "ComposeActivity",
                                "onClear isEmpty:" + signaturePadAdapter?.isEmpty
                            )
                        },
                    )
                }
                Row {
                    Button(onClick = {
                        mutableSvg.value = signaturePadAdapter?.getSignatureSvg() ?: ""
                    }) {
                        Text("Save")
                    }

                    Button(onClick = {
                        mutableSvg.value = ""
                        signaturePadAdapter?.clear()
                    }) {
                        Text("Clear")
                    }

                    Button(onClick = {
                        penColor.value = Color.Red
                    }) {
                        Text("Red")
                    }

                    Button(onClick = {
                        penColor.value = Color.Black
                    }) {
                        Text("Black")
                    }
                }

                Text(text = "SVG: " + mutableSvg.value)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        SignaturePadView()
    }
}
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