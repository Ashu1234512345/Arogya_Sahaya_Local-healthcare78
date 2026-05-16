package com.example.arogyasahayalocal


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MedicationAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra("med_name") ?: "Medicine"

        // This runs when the system clock hits your scheduled alarm time
        Toast.makeText(
            context,
            "⏰ TIME TO TAKE YOUR MEDICINE: $medicineName",
            Toast.LENGTH_LONG
        ).show()


    }
}