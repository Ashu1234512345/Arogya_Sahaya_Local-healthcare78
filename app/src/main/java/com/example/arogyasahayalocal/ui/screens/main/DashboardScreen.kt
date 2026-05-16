package com.example.arogyasahayalocal.ui.screens.main
//import com.example.arogyasahayalocal.viewmodels.ProfileViewModel
import android.content.Context
import com.example.arogyasahayalocal.data.entity.UserProfile
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.R.attr.contentDescription
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import android.widget.Toast
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arogyasahayalocal.ui.viewmodels.ProfileViewModel
import com.example.arogyasahayalocal.utils.SosHelper
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier,
    userName: String = "",
    onMedications: () -> Unit,
    onVitals: () -> Unit,
    onCamps: () -> Unit,
    onProfile: () -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)
    val name = prefs.getString("name", "User") ?: "User"
    var showLanguageMenu by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf("English") }
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Top
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement= Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text="Dashboard",
                fontWeight = FontWeight.Bold,
                style= MaterialTheme.typography.headlineLarge
            )
            Row {
                IconButton(onClick = {showLanguageMenu=true}) {
                    Icon(Icons.Default.Language,contentDescription="Language")
                }
                DropdownMenu(
                    expanded = showLanguageMenu,
                    onDismissRequest = {showLanguageMenu=false}
                ) {
                    DropdownMenuItem(text={Text("English")}, onClick = {onLanguageChange("en");showLanguageMenu=false})
                    DropdownMenuItem(text={Text("ಕನ್ನಡ (Kannada)")}, onClick = {onLanguageChange("kn");showLanguageMenu=false})

                }
                profileIcon(
                    onClick = onProfile
                )
            }
        }


        Spacer(modifier = Modifier.height(12.dp))
        val displayedName = userName.ifBlank { profileState?.name ?: "User" }
        Text("Welcome, $displayedName",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))


        //Buttons section
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            DashboardButton("Medications",onMedications, Modifier.weight(1f).padding(8.dp))
            DashboardButton("Vital Logs",onVitals, Modifier.weight(1f).padding(8.dp))
            DashboardButton("Health Camps",onCamps, Modifier.weight(1f))
            DashboardButton("History",{}, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier)

        val currentProfile = UserProfile(
            name = "User",
            age = 0,
            gender = "",
            emergencyPhone = ""
        )
        EmergencySosWidget(currentProfile = currentProfile)
        Spacer(modifier = Modifier.height(20.dp))
    }
}
@Composable
fun DashboardButton(text:String,onClick:()->Unit, modifier: Modifier){
    Card(
        onClick =onClick,
        modifier= Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(6.dp)
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier=Modifier.fillMaxSize())
        {
            Text(text=text,
                style=MaterialTheme.typography.headlineSmall,
                fontWeight=FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title:String,onBack:()->Unit){
    TopAppBar(
        title={Text(title)},
        navigationIcon = {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
    }
    )
}

@Composable
fun profileIcon(onClick: () -> Unit){
    IconButton(
        onClick=onClick,
        modifier = Modifier
            .size(48.dp)
            .background(
                color=Color(0xFFE0E0E0),
                shape = CircleShape
            )
    ) {
        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
    }
}

@Composable
fun EmergencySosWidget(currentProfile: UserProfile?) {
    val context = LocalContext.current
    val targetPhone = currentProfile?.emergencyPhone ?: ""

    Button(
        onClick = {
            if (targetPhone.isBlank()) {
                Toast.makeText(context, "Please set up your Emergency Profile Contact first!", Toast.LENGTH_LONG).show()
                return@Button
            }

            val smsMsg = "Emergency alert from ${currentProfile?.name ?: "User"}. I need help immediately!"
            SosHelper.sendEmergencySms(context, listOf(targetPhone), smsMsg)


            SosHelper.initiateEmergencyCall(context, targetPhone)
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text("EMERGENCY SOS", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}