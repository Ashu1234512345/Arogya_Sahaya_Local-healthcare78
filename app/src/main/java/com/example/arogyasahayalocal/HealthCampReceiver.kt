package com.example.arogyasahayalocal
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class HealthCampReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val campName = intent.getStringExtra("camp_name") ?: "Local Health Camp"
        val campLocation = intent.getStringExtra("camp_location") ?: "Nearby Center"

        Toast.makeText(
            context,
            "📢 HEALTH CAMP ALERT: $campName is happening at $campLocation!",
            Toast.LENGTH_LONG
        ).show()
    }
}