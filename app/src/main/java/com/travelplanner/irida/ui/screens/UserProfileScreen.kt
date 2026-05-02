package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.travelplanner.irida.data.local.entity.UserEntity
import com.travelplanner.irida.ui.theme.ErrorRed
import com.travelplanner.irida.ui.theme.GrayDark
import com.travelplanner.irida.ui.theme.GrayMid
import com.travelplanner.irida.ui.theme.NavyDeep
import com.travelplanner.irida.ui.theme.NavyLight
import com.travelplanner.irida.ui.theme.NavyMid
import com.travelplanner.irida.ui.theme.SuccessGreen
import com.travelplanner.irida.ui.theme.TurquoiseLight
import com.travelplanner.irida.ui.theme.TurquoisePrimary
import com.travelplanner.irida.ui.theme.White
import com.travelplanner.irida.ui.viewmodels.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var editMode by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf("") }
    var saveError by remember { mutableStateOf("") }

    // Campos editables — se inicializan cuando llegan los datos
    var username by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var acceptEmails by remember { mutableStateOf(false) }

    // Rellenar campos cuando los datos están disponibles
    LaunchedEffect(uiState) {
        if (uiState is UserProfileViewModel.ProfileUiState.Success) {
            val user = (uiState as UserProfileViewModel.ProfileUiState.Success).user
            username = user.username
            birthdate = user.birthdate
            phone = user.phone
            address = user.address
            country = user.country
            acceptEmails = user.acceptEmails
        }
        if (uiState is UserProfileViewModel.ProfileUiState.SaveSuccess) {
            saveMessage = "Perfil guardado correctamente"
            saveError = ""
            editMode = false
            viewModel.reloadAfterSave()
        }
        if (uiState is UserProfileViewModel.ProfileUiState.Error) {
            saveError = (uiState as UserProfileViewModel.ProfileUiState.Error).message
            saveMessage = ""
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = TurquoisePrimary,
        unfocusedBorderColor = GrayDark,
        disabledBorderColor = GrayDark.copy(alpha = 0.4f),
        focusedTextColor = White,
        unfocusedTextColor = White,
        disabledTextColor = GrayMid,
        cursorColor = TurquoisePrimary,
        focusedContainerColor = NavyLight,
        unfocusedContainerColor = NavyLight,
        disabledContainerColor = NavyLight.copy(alpha = 0.5f),
        focusedLabelColor = TurquoisePrimary,
        unfocusedLabelColor = GrayMid,
        disabledLabelColor = GrayMid.copy(alpha = 0.5f)
    )
    val fieldShape = RoundedCornerShape(12.dp)

    Scaffold(
        containerColor = NavyDeep,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mi perfil",
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = White
                        )
                    }
                },
                actions = {
                    if (uiState is UserProfileViewModel.ProfileUiState.Success ||
                        uiState is UserProfileViewModel.ProfileUiState.SaveSuccess
                    ) {
                        IconButton(onClick = {
                            if (editMode) {
                                saveError = ""
                                viewModel.updateProfile(username, birthdate, phone, address, country, acceptEmails)
                            } else {
                                editMode = true
                                saveMessage = ""
                            }
                        }) {
                            Icon(
                                imageVector = if (editMode) Icons.Filled.Save else Icons.Filled.Edit,
                                contentDescription = if (editMode) "Guardar" else "Editar",
                                tint = TurquoisePrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyMid)
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is UserProfileViewModel.ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NavyDeep),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = TurquoisePrimary) }
            }

            is UserProfileViewModel.ProfileUiState.Error -> {
                // Si el error ocurre mientras cargamos (no al guardar),
                // mostramos mensaje centrado
                if (!editMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NavyDeep)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(state.message, color = ErrorRed, style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    ProfileForm(
                        user = null,
                        username = username, onUsernameChange = { username = it; saveError = "" },
                        birthdate = birthdate, onBirthdateChange = { birthdate = it },
                        phone = phone, onPhoneChange = { phone = it },
                        address = address, onAddressChange = { address = it },
                        country = country, onCountryChange = { country = it },
                        acceptEmails = acceptEmails, onAcceptEmailsChange = { acceptEmails = it },
                        editMode = editMode,
                        saveMessage = saveMessage,
                        saveError = saveError,
                        isLoading = false,
                        fieldColors = fieldColors,
                        fieldShape = fieldShape,
                        padding = padding,
                        onCancelEdit = {
                            editMode = false
                            saveError = ""
                            viewModel.loadUser()
                        }
                    )
                }
            }

            is UserProfileViewModel.ProfileUiState.Success,
            is UserProfileViewModel.ProfileUiState.SaveSuccess -> {
                val user = (state as? UserProfileViewModel.ProfileUiState.Success)?.user
                ProfileForm(
                    user = user,
                    username = username, onUsernameChange = { username = it; saveError = "" },
                    birthdate = birthdate, onBirthdateChange = { birthdate = it },
                    phone = phone, onPhoneChange = { phone = it },
                    address = address, onAddressChange = { address = it },
                    country = country, onCountryChange = { country = it },
                    acceptEmails = acceptEmails, onAcceptEmailsChange = { acceptEmails = it },
                    editMode = editMode,
                    saveMessage = saveMessage,
                    saveError = saveError,
                    isLoading = false,
                    fieldColors = fieldColors,
                    fieldShape = fieldShape,
                    padding = padding,
                    onCancelEdit = {
                        editMode = false
                        saveError = ""
                        viewModel.loadUser()
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileForm(
    user: UserEntity?,
    username: String, onUsernameChange: (String) -> Unit,
    birthdate: String, onBirthdateChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    country: String, onCountryChange: (String) -> Unit,
    acceptEmails: Boolean, onAcceptEmailsChange: (Boolean) -> Unit,
    editMode: Boolean,
    saveMessage: String,
    saveError: String,
    isLoading: Boolean,
    fieldColors: androidx.compose.material3.TextFieldColors,
    fieldShape: androidx.compose.ui.graphics.Shape,
    padding: androidx.compose.foundation.layout.PaddingValues,
    onCancelEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Avatar con iniciales
        if (user != null) {
            ProfileAvatar(username = user.username)
        }

        // Email (solo lectura siempre)
        OutlinedTextField(
            value = user?.email ?: "",
            onValueChange = {},
            label = { Text("Correo electrónico") },
            singleLine = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors,
            shape = fieldShape,
            supportingText = { Text("El correo no se puede cambiar", color = GrayMid, fontSize = 11.sp) }
        )

        // Username
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Nombre de usuario") },
            singleLine = true,
            enabled = editMode,
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors,
            shape = fieldShape
        )

        // Fecha de nacimiento
        OutlinedTextField(
            value = birthdate,
            onValueChange = onBirthdateChange,
            label = { Text("Fecha de nacimiento") },
            placeholder = { Text("dd/MM/aaaa", color = GrayMid) },
            singleLine = true,
            enabled = editMode,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors,
            shape = fieldShape
        )

        // Teléfono
        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Teléfono") },
            singleLine = true,
            enabled = editMode,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors,
            shape = fieldShape
        )

        // Dirección
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text("Dirección") },
            singleLine = true,
            enabled = editMode,
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors,
            shape = fieldShape
        )

        // País
        OutlinedTextField(
            value = country,
            onValueChange = onCountryChange,
            label = { Text("País") },
            singleLine = true,
            enabled = editMode,
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors,
            shape = fieldShape
        )

        // Aceptar emails
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyLight, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Recibir novedades por email",
                    color = if (editMode) White else GrayMid,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Ofertas, nuevas funciones y actualizaciones",
                    color = GrayMid,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(
                checked = acceptEmails,
                onCheckedChange = onAcceptEmailsChange,
                enabled = editMode,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NavyDeep,
                    checkedTrackColor = TurquoisePrimary,
                    uncheckedThumbColor = GrayMid,
                    uncheckedTrackColor = GrayDark,
                    disabledCheckedTrackColor = TurquoisePrimary.copy(alpha = 0.4f),
                    disabledUncheckedTrackColor = GrayDark.copy(alpha = 0.4f)
                )
            )
        }

        // Mensajes de estado
        if (saveMessage.isNotEmpty()) {
            Text(
                text = "✓ $saveMessage",
                color = SuccessGreen,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        if (saveError.isNotEmpty()) {
            Text(
                text = saveError,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Botones cuando está en modo edición
        if (editMode) {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onCancelEdit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = TurquoisePrimary, strokeWidth = 2.dp)
                } else {
                    Text("Cancelar", color = GrayMid, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun ProfileAvatar(username: String) {
    val initials = username
        .trim()
        .split(" ")
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifEmpty { "?" }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Brush.linearGradient(listOf(TurquoisePrimary, TurquoiseLight)),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = NavyDeep,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
