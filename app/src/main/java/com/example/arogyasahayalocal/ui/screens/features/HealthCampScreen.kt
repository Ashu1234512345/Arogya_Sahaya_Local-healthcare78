package com.example.arogyasahayalocal.ui.screens.features
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.core.content.ContextCompat
import com.example.arogyasahayalocal.data.entity.HealthCamp
import com.example.arogyasahayalocal.viewmodels.HealthCampViewModel
import com.google.gson.reflect.TypeToken
import kotlin.collections.emptyList
import com.google.gson.Gson
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthCampScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var showAddCampDialog by remember { mutableStateOf(false) }

    var campTitle by remember { mutableStateOf("") }
    var locationInput by remember { mutableStateOf("") }

    val reminderCalendar = remember { Calendar.getInstance() }
    var displayDate by remember { mutableStateOf("Select Date") }
    var displayTime by remember { mutableStateOf("Select Time") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showAddCampDialog = true
        } else {
            Toast.makeText(context, "Notification permission is required for reminders!", Toast.LENGTH_LONG).show()
        }
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            reminderCalendar.set(Calendar.YEAR, year)
            reminderCalendar.set(Calendar.MONTH, month)
            reminderCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            displayDate = "$dayOfMonth/${month + 1}/$year"
        },
        reminderCalendar.get(Calendar.YEAR),
        reminderCalendar.get(Calendar.MONTH),
        reminderCalendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            reminderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            reminderCalendar.set(Calendar.MINUTE, minute)
            reminderCalendar.set(Calendar.SECOND, 0)

            val formattedMinute = if (minute < 10) "0$minute" else minute.toString()
            displayTime = "$hourOfDay:$formattedMinute"
        },
        reminderCalendar.get(Calendar.HOUR_OF_DAY),
        reminderCalendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Camps & Checkups", fontSize = 26.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", modifier = Modifier.size(30.dp))
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            showAddCampDialog = true
                        } else {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        showAddCampDialog = true
                    }
                },
                icon = { Icon(Icons.Default.Add, null, modifier = Modifier.size(26.dp)) },
                text = { Text("Remind Me", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Upcoming in Your Village", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            CampReminderCard(
                title = "Monthly Checkup",
                date = "17 May 2026",
                location = "PHC",
                type = "Govt"
            )

            Spacer(Modifier.height(24.dp))
            Text("My Next Checkups", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Add custom village/clinic visits using the button below.", fontSize = 18.sp, color = Color.Gray)
        }
    }

    if (showAddCampDialog) {
        AlertDialog(
            onDismissRequest = { showAddCampDialog = false },
            title = { Text("Add Visit Reminder", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = campTitle,
                        onValueChange = { campTitle = it },
                        label = { Text("Purpose ", fontSize = 16.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = locationInput,
                        onValueChange = { locationInput = it },
                        label = { Text("Location Name", fontSize = 16.sp) },
                        placeholder = { Text("Enter location name only", fontSize = 16.sp, color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = { datePickerDialog.show() },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    ) {
                        Text(displayDate, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = { timePickerDialog.show() },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    ) {
                        Text(displayTime, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (campTitle.isNotEmpty() && displayDate != "Select Date" && displayTime != "Select Time") {

                           scheduleHealthCampAlarms(context, campTitle, locationInput, reminderCalendar.timeInMillis)

                            val newCampRecord = HealthCamp(
                                campName = campTitle,
                                location = locationInput,
                                dateText = "$displayDate at $displayTime", // Combines date and time for clean list rendering
                                isReminderOnly = false
                            )

                            //healthCampViewModel.saveCamp(newCampRecord)
                            campTitle = ""
                            locationInput = ""
                            displayDate = "Select Date"
                            displayTime = "Select Time"
                            showAddCampDialog = false

                            Toast.makeText(context, "Camp Saved & Alarm Scheduled!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.height(50.dp)
                ) {
                    Text("SAVE & SET ALARM", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCampDialog = false }) {
                    Text("Cancel", fontSize = 18.sp)
                }
            }
        )
    }
}

@Composable
fun CampReminderCard(title: String, date: String, location: String, type: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(text = date, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(4.dp))
            Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = location, fontSize = 18.sp, color = Color.DarkGray)
            Spacer(Modifier.height(8.dp))
            SuggestionChip(onClick = {}, label = { Text(type, fontSize = 14.sp) })
        }
    }
}


fun scheduleHealthCampAlarms(context: Context, title: String, location: String, targetTimeMs: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(
                context,
                "Exact alarm permission missing. Please enable it in system settings.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
    }

    val notificationId24h = targetTimeMs.hashCode()
    val notificationId1h = targetTimeMs.hashCode() + 1

    try {
        // 1. One Day Before Alarm
        val time24hBefore = targetTimeMs - 86400000
        if (time24hBefore > System.currentTimeMillis()) {
            val intent24 = Intent(context, HealthCampReceiver::class.java).apply {
                putExtra("camp_title", title)
                putExtra("msg", "Tomorrow: $title at $location")
            }
            val pendingIntent24 = PendingIntent.getBroadcast(
                context, notificationId24h, intent24, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time24hBefore, pendingIntent24)
        }

        // 2. One Hour Before Alarm
        val time1hBefore = targetTimeMs - 3600000
        if (time1hBefore > System.currentTimeMillis()) {
            val intent1 = Intent(context, HealthCampReceiver::class.java).apply {
                putExtra("camp_title", title)
                putExtra("msg", "In 1 Hour: $title at $location")
            }
            val pendingIntent1 = PendingIntent.getBroadcast(
                context, notificationId1h, intent1, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time1hBefore, pendingIntent1)
        }
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}