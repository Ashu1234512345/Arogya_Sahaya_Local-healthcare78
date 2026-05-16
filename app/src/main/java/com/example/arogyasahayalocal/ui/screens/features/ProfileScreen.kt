package com.example.arogyasahayalocal.ui.screens.features


import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.arogyasahayalocal.ui.viewmodels.ProfileViewModel


data class ProfileData(
    val name: String,
    val age: Int,
    val gender:String,
    val bloodGroup: String,
    val chronicConditions: String,
    val emergencyName: String,
    val emergencyPhone: String
)




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel, onBack: () -> Unit, onLogout: () -> Unit) {
    val profile by viewModel.profileState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var name by remember(profile) { mutableStateOf(profile?.name ?: "") }
    var age by remember { mutableStateOf(0) }
    var gender by remember(profile) { mutableStateOf(profile?.gender ?: "Select Gender") }
    var blood by remember(profile) { mutableStateOf(profile?.bloodGroup ?: "Select Blood Group") }
    var conditions by remember(profile) { mutableStateOf(profile?.chronicConditions ?: "None") }
    var otherCondition by remember { mutableStateOf("") }
    var emergencyName by remember(profile) { mutableStateOf(profile?.emergencyName ?: "") }
    var emergencyPhone by remember(profile) { mutableStateOf(profile?.emergencyPhone ?: "") }
    var isEditing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontSize = 28.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(30.dp))
                    }
                },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = { showLogoutDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.Red,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(12.dp)

        ) {

            if (isEditing) {
                Text("Editing: Please update your details and press Save",
                    color = Color(0xFFD81B60), fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(14.dp))
            ProfileTextField(name, "Full Name", isEditing) { name = it }
            ProfileTextField(
                value = age.toString(),
                label = "Age",
                isEditing = isEditing
            ) { newValue ->
                age = newValue.toIntOrNull() ?: 0
            }
            ProfileDropdown(gender, "Gender", listOf( "Male", "Female"), isEditing) { gender = it }
            ProfileDropdown(blood, "Blood Group", listOf( "A+", "B+", "O+", "AB+", "A-", "B-", "O-", "AB-"), isEditing) { blood = it }
            ProfileDropdown(conditions, "Chronic Condition", listOf("None", "Diabetes", "BP", "Heart", "Other"), isEditing) { conditions = it }

            if (isEditing && conditions == "Other") {
                OutlinedTextField(
                    value = otherCondition,
                    onValueChange = { otherCondition = it },
                    label = { Text("Specify Other Condition") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))


            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(2.dp, Color.Red)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "EMERGENCY CONTACT",
                        color = Color.Red,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ProfileTextField(emergencyName, "Contact Name", isEditing) { emergencyName = it }

                     Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        if (isEditing) {
                            OutlinedTextField(
                                value = emergencyPhone,
                                onValueChange = { emergencyPhone = it },
                                label = { Text("Mobile Number", fontSize = 16.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                                )
                            )
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Mobile Number",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = if (emergencyPhone.isEmpty()) "Not Set" else emergencyPhone,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SAVE/EDIT BUTTON
            Button(
                onClick = {
                    if (isEditing) {
                        val finalCondition = if (conditions == "Other") otherCondition else conditions
                        viewModel.saveProfile(name, age, gender, blood, finalCondition, emergencyName, emergencyPhone)
                    }
                    isEditing = !isEditing
                },
                modifier = Modifier.fillMaxWidth().height(65.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(if (isEditing) Icons.Default.Save else Icons.Default.Edit, null)
                Spacer(Modifier.width(8.dp))
                Text(if (isEditing) "SAVE DETAILS" else "EDIT PROFILE", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LOGOUT BUTTON
            TextButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
            ) {
                Text("Logout", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // CONFIRM LOGOUT POPUP
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout?") },
            text = { Text("Are you sure you want to logout? You will need to register again.") },
            confirmButton = {
                Button(onClick = {
                    showLogoutDialog = false
                    viewModel.logout()
                    onLogout()
                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ProfileTextField(
    value: String,
    label: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label, fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (value.isEmpty()) "Not Set" else value,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDropdown(
    selected: String,
    label: String,
    options: List<String>,
    isEditing: Boolean,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    if (isEditing) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                label = { Text(label, fontSize = 18.sp) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 18.sp) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    } else {
        ProfileTextField(selected, label, false) {}
    }
}


