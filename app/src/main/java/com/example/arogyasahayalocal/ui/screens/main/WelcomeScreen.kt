package com.example.arogyasahayalocal.ui.screens.main

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color // So you can use Color.Red
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(onContinue:() -> Unit){
    var name by remember{ mutableStateOf("") }
    var age by remember{ mutableStateOf("") }
    var error by remember{ mutableStateOf("") }
    val context= LocalContext.current
    val prefs=context.getSharedPreferences("app", Context.MODE_PRIVATE)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text="Arogya-Sahaya Local",
            style= MaterialTheme.typography.displayMedium,
            color= MaterialTheme.colorScheme.primary

        )
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value=name,
            onValueChange = {name=it
                error = ""},
            label={Text("Enter Your Name",fontSize = 18.sp)},
            modifier = Modifier.fillMaxWidth(),
            isError = error.isNotEmpty()
        )
//        if (error.isNotEmpty()) {
//            Text("Name is required!",text = error, color = Color.Red, style = MaterialTheme.typography.bodySmall)
//        }
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value=age,
            onValueChange = { age=it},
            label={Text("Enter Your Age", fontSize = 18.sp)},
            modifier = Modifier.fillMaxWidth(),keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if(error.isNotEmpty()){
            Text(text=error, color= Color.Red)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                val ageValue = age.toIntOrNull()
                if (name.isBlank()) {
                    error = "Please enter your name!"
                } else if (ageValue == null || ageValue !in 0..110) {
                    error = "Enter valid Age!"
                } else {
                    error = ""
                    prefs.edit().apply {
                        putString("user_name", name)
                        putString("user_age", age)
                        putString("user_gender","")
                        putString("user_blood", "")
                        putString("user_conditions", "None")
                        putString("name_emergency", "")
                        putString("phone_emergency", "")
                        putBoolean("isRegistered", true)
                        apply()
                    }
                    onContinue()
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Save and Continue")
        }
    }
}


