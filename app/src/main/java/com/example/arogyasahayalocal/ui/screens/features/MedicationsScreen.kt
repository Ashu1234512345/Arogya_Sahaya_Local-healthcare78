package com.example.arogyasahayalocal.ui.screens.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import android.widget.Toast
import android.os.Build
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.arogyasahayalocal.MedicationAlarmReceiver
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent

import androidx.compose.ui.Alignment

import androidx.compose.ui.platform.LocalContext


import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MedicationsScreen(
    medicines: List<Medicine>,
    onAdd: () -> Unit,
    onBack: () -> Unit,
    onDelete: (Medicine) -> Unit,
    onEdit: (Medicine, Medicine) -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("med_reminder_prefs", Context.MODE_PRIVATE) }
    var isAddScreenOpen by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Medicine?>(null) }
    var showEditDialog by remember { mutableStateOf<Medicine?>(null) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Medicines", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        if (medicines.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No medicines added yet.", fontSize = 18.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(medicines) { med ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            val savedAlarmActive = sharedPrefs.getBoolean("${med.name}_active", false)
                            val savedAlarmTime = sharedPrefs.getString("${med.name}_time", "") ?: ""
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = med.name.uppercase(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Doses Per Day: ${med.dose}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Row {
                                    IconButton(onClick = { showEditDialog = med }) {
                                        Icon(Icons.Default.Edit, "Edit", tint = Color.Blue)
                                    }
                                    IconButton(onClick = { showDeleteDialog = med }) {
                                        Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Scheduled Timings:", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val timingList = if (med.times.isBlank()) emptyList() else med.times.split(",")
                                timingList.forEach { timeLabel ->
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(timeLabel, fontSize = 14.sp) }
                                    )
                                }
                            }

                            if (savedAlarmActive && savedAlarmTime.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "🔔 Active Alarm: $savedAlarmTime",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }


        showDeleteDialog?.let { med ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Confirm Delete") },
                text = { Text("Are you sure you want to delete ${med.name}?") },
                confirmButton = {
                    TextButton(onClick = {
                        onDelete(med)
                        cancelMedicineAlarm(context, med.name)
                        showDeleteDialog = null
                    }) { Text("Delete", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel") }
                }
            )
        }


        showEditDialog?.let { med ->
            var tempName by remember { mutableStateOf(med.name) }
            var tempDoseText by remember { mutableStateOf(med.dose) }
            var tempIsReminderOn by remember { mutableStateOf(sharedPrefs.getBoolean("${med.name}_active", false)) }
            var tempAlarmTime by remember { mutableStateOf(sharedPrefs.getString("${med.name}_time", "") ?: "") }

            val availableIntervals = listOf("Morning", "Afternoon", "Evening", "Night")
            val selectedIntervals = remember {
                mutableStateListOf<String>().apply {
                    if (med.times.isNotEmpty()) addAll(med.times.split(","))
                }
            }

            var validationError by remember { mutableStateOf("") }

            val timePicker = TimePickerDialog(context, { _, hour, minute ->
                val formattedMinute = if (minute < 10) "0$minute" else minute.toString()
                tempAlarmTime = "$hour:$formattedMinute"
            }, 8, 0, true)

            AlertDialog(
                onDismissRequest = { showEditDialog = null },
                title = { Text("Edit Medicine Details") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            label = { Text("Medicine Name (Letters Only)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = tempDoseText,
                            onValueChange = { tempDoseText = it },
                            label = { Text("Doses Per Day (Integer Max 5)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Dose Timings:", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        availableIntervals.forEach { interval ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = selectedIntervals.contains(interval),
                                    onCheckedChange = { checked ->
                                        if (checked) selectedIntervals.add(interval)
                                        else selectedIntervals.remove(interval)
                                    }
                                )
                                Text(interval, fontSize = 16.sp)
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Set Reminder Alarm?", modifier = Modifier.weight(1f), fontSize = 16.sp)
                            Switch(
                                checked = tempIsReminderOn,
                                onCheckedChange = { isChecked -> tempIsReminderOn = isChecked }
                            )
                        }

                        if (tempIsReminderOn) {
                            Button(onClick = { timePicker.show() }, modifier = Modifier.fillMaxWidth()) {
                                Text(if (tempAlarmTime.isEmpty()) "Select Alarm Time" else "Alarm: $tempAlarmTime")
                            }
                        }

                        if (validationError.isNotEmpty()) {
                            Text(validationError, color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val parsedDoseInt = tempDoseText.toIntOrNull()
                        val lettersOnlyRegex = "^[a-zA-Z\\s]+$".toRegex()

                        if (tempName.isBlank() || !tempName.matches(lettersOnlyRegex)) {
                            validationError = "Error: Medicine name must be text only."
                        } else if (parsedDoseInt == null || parsedDoseInt <= 0 || parsedDoseInt > 5) {
                            validationError = "Enter correct value: Doses must be an integer between 1 and 5."
                        } else if (selectedIntervals.isEmpty()) {
                            validationError = "Error: Please check at least one interval checkbox."
                        } else if (tempIsReminderOn && tempAlarmTime.isEmpty()) {
                            validationError = "Error: Please pick an alarm trigger time."
                        } else {
                            validationError = ""
                            val commaSeparatedTimes = selectedIntervals.joinToString(",")


                            val updatedMedicine = Medicine(
                                name = tempName,
                                dose = parsedDoseInt.toString(),
                                times = commaSeparatedTimes
                            )

                            if (tempIsReminderOn) {
                                scheduleMedicineAlarm(context, updatedMedicine, tempAlarmTime) // ✅ FIXED: Added tempAlarmTime
                            } else {
                                cancelMedicineAlarm(context, med.name)
                            }

                            onEdit(med, updatedMedicine)
                            showEditDialog = null
                        }
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = null }) { Text("Cancel") }
                }
            )
        }
    }
}

fun scheduleMedicineAlarm(context: Context, medicine: Medicine, alarmTime: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= 31) {
        if (!alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(context, "Please grant Exact Alarm permission in system settings.", Toast.LENGTH_LONG).show()
            return
        }
    }

    val timeParts = alarmTime.split(":")
    if (timeParts.size != 2) return

    val hour = timeParts[0].toInt()
    val minute = timeParts[1].toInt()

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        if (timeInMillis <= System.currentTimeMillis()) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    val intent = Intent(context, MedicationAlarmReceiver::class.java).apply {
        putExtra("med_id", medicine.name.hashCode().toString())
        putExtra("med_name", medicine.name)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        medicine.name.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    try {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}
fun cancelMedicineAlarm(context: Context, medicineName: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, MedicationAlarmReceiver::class.java)

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        medicineName.hashCode(),
        intent,
        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    )
    if (pendingIntent != null) {
        alarmManager.cancel(pendingIntent)
    }
}




//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MedicationsScreen(medicines: List<Medicine>,
//                      onAdd:()->Unit,
//                      onBack:()-> Unit,
//                      onDelete: (Medicine) -> Unit,
//                      onEdit: (Medicine, Medicine) -> Unit)
//{
//    var showDeleteDialog by remember { mutableStateOf<Medicine?>(null) }
//    var showEditDialog by remember { mutableStateOf<Medicine?>(null) }
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(20.dp)
//    )
//    {
//        Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = { Text("My Medicines") },
//                    navigationIcon = {
//                        IconButton(onClick = onBack) {
//                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                        }
//                    }
//                )
//            },
//            floatingActionButton = {
//                FloatingActionButton(onClick = onAdd) {
//                    Icon(Icons.Default.Add, contentDescription = "Add")
//                }
//            }
//        ) { padding ->
//            if (medicines.isEmpty()) {
//                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
//                    Text("No medicines added yet.")
//                }
//            } else {
//                LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
//                    items(medicines) { med ->
//                        Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
//                            Row(
//                                Modifier.padding(16.dp),
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Column(Modifier.weight(1f)) {
//                                    Text(
//                                        text = med.name,
//                                        style = MaterialTheme.typography.titleMedium
//                                    )
//                                    Text(text = "${med.dose} - ${med.times}")
//                                }
//                                Row {
//                                    IconButton(onClick = { showEditDialog = med }) {
//                                        Icon(
//                                            Icons.Default.Edit,
//                                            contentDescription = "Edit",
//                                            tint = Color.Blue
//                                        )
//                                    }
//                                    IconButton(onClick = { showDeleteDialog = med }) {
//                                        Icon(
//                                            Icons.Default.Delete,
//                                            contentDescription = "Delete",
//                                            tint = Color.Red
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        showDeleteDialog?.let { med ->
//            AlertDialog(
//                onDismissRequest = { showDeleteDialog = null },
//                title = { Text("Confirm Delete") },
//                text = { Text("Are you sure you want to delete ${med.name}?") },
//                confirmButton = {
//                    TextButton(onClick = {
//                        onDelete(med)
//                        showDeleteDialog = null
//                    }) { Text("Delete", color = Color.Red) }
//                },
//                dismissButton = {
//                    TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel") }
//                }
//            )
//        }
//        Spacer(modifier = Modifier.height(20.dp))
//        if (medicines.isEmpty())
//        {
//            Button(
//                onClick = onAdd, modifier = Modifier
//                    .fillMaxWidth()
//                    .height(70.dp)
//            ) {
//                Text("Add Medicine")
//            }
//
//        }
//        else {
//
//            LazyColumn(modifier = Modifier.weight(1f),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(medicines) { medicine ->
//                    MedicineCard(medicine)
//                }
//
//            }
//
//            Spacer(modifier = Modifier.height(20.dp))
//            Button(onClick = onAdd, modifier = Modifier.fillMaxWidth().height(70.dp)) {
//                Text("Add Medicine")
//            }
//        }
//
//
//        showEditDialog?.let { med ->
//            var tempName by remember { mutableStateOf(med.name) }
//            var tempDose by remember { mutableStateOf(med.dose) }
//            var tempTimes by remember { mutableStateOf(med.times) }
//
//            AlertDialog(
//                onDismissRequest = { showEditDialog = null },
//                title = { Text("Edit Medicine") },
//                text = {
//                    Column {
//                        OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text("Name") })
//                        OutlinedTextField(value = tempDose, onValueChange = { tempDose = it }, label = { Text("Dose") })
//                        OutlinedTextField(value = tempTimes, onValueChange = { tempTimes = it }, label = { Text("Time") })
//                    }
//                },
//                confirmButton = {
//                    Button(onClick = {
//                        onEdit(med, Medicine(tempName, tempDose, tempTimes))
//                        showEditDialog = null
//                    }) { Text("Save") }
//                },
//                dismissButton = {
//                    TextButton(onClick = { showEditDialog = null }) { Text("Cancel") }
//                }
//            )
//        }
//    }
//}
//
//
//
//@Composable
//fun MedicineCard(medicine: Medicine) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surfaceVariant
//        )
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(20.dp)
//                .fillMaxWidth()
//        ) {
//            // FIX for "Unresolved reference 'medicine'"
//            Text(
//                text = medicine.name.uppercase(),
//                fontSize = 24.sp,
//                fontWeight = FontWeight.ExtraBold,
//                color = MaterialTheme.colorScheme.primary
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = "Dose: ${medicine.dose}",
//                fontSize = 22.sp,
//                fontWeight = FontWeight.Medium
//            )
//
//            Text(
//                text = "Time: ${medicine.times}",
//                fontSize = 20.sp,
//                color = Color.DarkGray
//            )
//        }
//    }
//}