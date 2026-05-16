package com.example.arogyasahayalocal

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.arogyasahayalocal.ui.screens.main.DashboardScreen
import com.example.arogyasahayalocal.ui.screens.main.HomeScreen
import com.example.arogyasahayalocal.ui.screens.main.WelcomeScreen
import com.example.arogyasahayalocal.ui.screens.features.ProfileScreen
import com.example.arogyasahayalocal.ui.screens.features.MedicationsScreen
import com.example.arogyasahayalocal.ui.screens.features.AddMedicineScreen
import com.example.arogyasahayalocal.ui.screens.features.*
import androidx.core.content.edit
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arogyasahayalocal.ui.viewmodels.ProfileViewModel
import com.example.arogyasahayalocal.data.entity.VitalLog
import com.example.arogyasahayalocal.viewmodels.HealthCampViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val prefs = remember { context.getSharedPreferences("app", Context.MODE_PRIVATE) }
            var medicines by remember { mutableStateOf(listOf<Medicine>()) }
            var vitalEntries by remember { mutableStateOf(listOf<VitalLog>()) }
            var screen by remember {
                mutableStateOf(if (prefs.getBoolean("isRegistered", false)) "dashboard" else "home")
            }
            val profileViewModel: ProfileViewModel = viewModel()
            when (screen) {
                "home" -> HomeScreen(
                    onStart = { screen = "register" }
                )

                "register" -> WelcomeScreen(
                    onContinue = {
                        prefs.edit { putBoolean("isRegistered", true) }
                        screen = "dashboard"
                    }
                )

                "dashboard" -> DashboardScreen(
                viewModel = profileViewModel,
                onMedications = { screen = "medications" },
                onVitals = { screen = "vitals" },
                onCamps = { screen = "camps" },
                onProfile = { screen = "profile" },
                onLanguageChange = { lang ->
                    println("Language changed to: $lang")
                }
                )

                "profile" -> ProfileScreen(
                    viewModel = profileViewModel,
                    onBack={screen="dashboard"},
                    onLogout = {
                        screen = "home"
                    }
                )

                "medications" -> MedicationsScreen(
                    medicines = medicines,
                    onAdd = { screen = "addMedicine" },
                    onBack = { screen = "dashboard" },
                    onDelete = { medToDelete ->
                        medicines = medicines.filter { it != medToDelete }
                    },
                    onEdit = {oldMed,newMed->
                        medicines=medicines.map{if(it==oldMed) newMed else it
                        }
                    }
                )
                "addMedicine" -> AddMedicineScreen(
                    onSave = { name, dose, times ->
                        val newMed = Medicine(name, dose, times)
                        medicines = medicines + newMed
                        screen = "medications"
                    },
                    onBack = { screen = "medications" }
                )
                "vitals" -> VitalLogScreen(
                    vitals = vitalEntries,
                    onSave = { newEntry ->
                        vitalEntries = vitalEntries + newEntry
                    },
                    onBack = { screen = "dashboard" }
                )
                "camps" -> HealthCampScreen( onBack = { screen = "dashboard" })
            }
        }
    }
}
