package com.example.arogyasahayalocal.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.widget.Toast

object SosHelper {

    /**
     * Sends an emergency text message to a list of registered contact numbers.
     */
    fun sendEmergencySms(context: Context, contactNumbers: List<String>, message: String) {
        if (contactNumbers.isEmpty()) {
            Toast.makeText(context, "No emergency contacts found in profile!", Toast.LENGTH_LONG).show()
            return
        }

        try {
            val smsManager = context.getSystemService(SmsManager::class.java)
            for (number in contactNumbers) {
                // Sends standard text in background
                smsManager.sendTextMessage(number, null, message, null, null)
            }
            Toast.makeText(context, "Alert SMS sent to family members!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to send SMS automatically.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens the system dialer with the primary contact number pre-filled.
     * Using ACTION_DIAL is safer because it doesn't instantly drop the call if misclicked,
     * allowing the user to hit the green call button themselves.
     */
    fun initiateEmergencyCall(context: Context, primaryNumber: String) {
        if (primaryNumber.isEmpty()) return

        val phoneIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$primaryNumber")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(phoneIntent)
    }
}