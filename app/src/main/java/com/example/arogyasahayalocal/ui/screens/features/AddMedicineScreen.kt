package com.example.arogyasahayalocal.ui.screens.features

import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.arogyasahayalocal.ui.screens.main.AppTopBar
import androidx.activity.compose.BackHandler
data class Medicine(val name: String, val dose: String, val times: String)
@Composable
fun AddMedicineScreen(onSave: (String, String, String) -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    var morning by remember { mutableStateOf(false) }
    var afternoon by remember { mutableStateOf(false) }
    var evening by remember { mutableStateOf(false) }
    BackHandler {
        onBack()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        AppTopBar("Add New Medicine") { onBack() }

        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Medicine Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = dose,
            onValueChange = { dose = it },
            label = { Text("Dose (e.g., 1 tablet)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "When to take?", style = MaterialTheme.typography.titleMedium)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = morning, onCheckedChange = { morning = it })
            Text("Morning")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = afternoon, onCheckedChange = { afternoon = it })
            Text("Afternoon")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = evening, onCheckedChange = { evening = it })
            Text("Evening")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End // Pushes buttons to the right side
        ) {

            OutlinedButton(
                onClick = { onBack() },
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    val selectedTimes = listOfNotNull(
                        if (morning) "Morning" else null,
                        if (afternoon) "Afternoon" else null,
                        if (evening) "Evening" else null
                    ).joinToString(", ")

                    onSave(name, dose, selectedTimes)
                }
            ) {
                Text("Save")
            }
        }
    }
}

