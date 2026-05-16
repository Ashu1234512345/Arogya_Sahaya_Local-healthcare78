package com.example.arogyasahayalocal.ui.screens.features


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import android.graphics.Typeface
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.graphics.nativeCanvas

import com.example.arogyasahayalocal.data.entity.VitalLog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalLogScreen(
    vitals: List<VitalLog>,          // CHANGE: Type must be VitalLog
    onSave: (VitalLog) -> Unit,      // CHANGE: Type must be VitalLog
    onBack: () -> Unit
) {
    var sBP by remember { mutableStateOf("") }
    var dBP by remember { mutableStateOf("") }
    var sugar by remember { mutableStateOf("") }
    var temp by remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateText by remember { mutableStateOf("Select Date") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vital Log") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {

            Card(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Add Health Data", style = MaterialTheme.typography.titleMedium)

                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(selectedDateText)
                    }

                    OutlinedTextField(value = sBP, onValueChange = { sBP = it }, label = { Text("BP Systolic") })
                    OutlinedTextField(value = dBP, onValueChange = { dBP = it }, label = { Text("BP Diastolic") })
                    OutlinedTextField(value = sugar, onValueChange = { sugar = it }, label = { Text("Sugar Level") })
                    OutlinedTextField(value = temp, onValueChange = { temp = it }, label = { Text("Temp") })

                    Button(
                        onClick = {
                            val date = datePickerState.selectedDateMillis
                            if (date != null && sBP.isNotEmpty() && dBP.isNotEmpty()) {
                                // FIXED: Creating a VitalLog object instead of VitalEntry
                                onSave(
                                    VitalLog(
                                        dateMillis = date,
                                        dateText = selectedDateText,
                                        systolicBP = sBP.toFloatOrNull() ?: 0f,
                                        diastolicBP = dBP.toFloatOrNull() ?: 0f,
                                        sugarLevel = sugar.toFloatOrNull() ?: 0f,
                                        temperature = temp.toFloatOrNull() ?: 0f
                                    )
                                )

                                sBP = ""; dBP = ""; sugar = ""; temp = ""; selectedDateText = "Select Date"
                            }
                        },
                        enabled = datePickerState.selectedDateMillis != null,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) { Text("Save Records") }
                }
            }

            if (vitals.size >= 2) {
                Text("7-Day Health Trend", style = MaterialTheme.typography.titleSmall)
                HealthTrendGraph(vitals.sortedBy { it.dateMillis }.takeLast(7))
            } else {
                Text("Add data for ${2 - vitals.size} more days to see trends.", color = Color.Gray)
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val formatter = java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault())
                        selectedDateText = formatter.format(java.util.Date(it))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
@Composable
fun HealthTrendGraph(data: List<VitalLog>) {
    Column {
        Text(
            text = "Blood Pressure Trend (mmHg)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .background(Color(0xFFF5F5F5))
        ) {
            val maxVal = 200f
            val graphHeight = size.height - 40f
            val spaceBetween = size.width / (data.size - 1).coerceAtLeast(1)

            val systolicPoints = data.mapIndexed { index, entry ->
                Offset(
                    x = index * spaceBetween,
                    y = graphHeight - (entry.systolicBP / maxVal * graphHeight)
                )
            }

            val diastolicPoints = data.mapIndexed { index, entry ->
                Offset(
                    x = index * spaceBetween,
                    y = graphHeight - (entry.diastolicBP / maxVal * graphHeight)
                )
            }

            val datePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = 28f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }

            val systolicPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#FF5252") // Red
                textSize = 30f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }

            val diastolicPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#2196F3") // Blue
                textSize = 30f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }

            data.forEachIndexed { index, entry ->
                val sysPoint = systolicPoints[index]
                val diaPoint = diastolicPoints[index]

                if (index < data.size - 1) {
                    drawLine(
                        color = Color(0xFFFF5252),
                        start = sysPoint,
                        end = systolicPoints[index + 1],
                        strokeWidth = 6f
                    )
                    drawLine(
                        color = Color(0xFF2196F3),
                        start = diaPoint,
                        end = diastolicPoints[index + 1],
                        strokeWidth = 6f
                    )
                }

                drawCircle(color = Color(0xFFFF5252), radius = 10f, center = sysPoint)
                drawCircle(color = Color(0xFF2196F3), radius = 10f, center = diaPoint)

                drawContext.canvas.nativeCanvas.drawText(
                    entry.systolicBP.toInt().toString(),
                    sysPoint.x,
                    sysPoint.y - 18f,
                    systolicPaint
                )

                drawContext.canvas.nativeCanvas.drawText(
                    entry.diastolicBP.toInt().toString(),
                    diaPoint.x,
                    diaPoint.y + 32f,
                    diastolicPaint
                )

                drawContext.canvas.nativeCanvas.drawText(
                    entry.dateText,
                    sysPoint.x,
                    size.height - 10f,
                    datePaint
                )
            }
        }
    }
}