package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travelplanner.irida.ui.theme.*

@Composable
fun PreferencesScreen(
    onNavigate: (String) -> Unit = {}
) {
    // Mock states - only UI, no real logic
    var selectedLanguage by remember { mutableStateOf("Español") }
    var selectedCurrency by remember { mutableStateOf("EUR (€)") }
    var selectedDateFormat by remember { mutableStateOf("DD/MM/AAAA") }
    var darkModeEnabled by remember { mutableStateOf(true) }
    var selectedTextSize by remember { mutableStateOf("Normal") }
    var tripRemindersEnabled by remember { mutableStateOf(true) }
    var weeklyDigestEnabled by remember { mutableStateOf(false) }
    var aiSuggestionsEnabled by remember { mutableStateOf(true) }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showDateFormatDialog by remember { mutableStateOf(false) }
    var showTextSizeDialog by remember { mutableStateOf(false) }

    // Dialogs
    if (showLanguageDialog) {
        OptionsDialog(
            title = "Idioma",
            options = listOf("Español", "English", "Català", "Français", "Deutsch"),
            selected = selectedLanguage,
            onSelect = { selectedLanguage = it; showLanguageDialog = false },
            onDismiss = { showLanguageDialog = false }
        )
    }
    if (showCurrencyDialog) {
        OptionsDialog(
            title = "Moneda",
            options = listOf("EUR (€)", "USD ($)", "GBP (£)", "JPY (¥)"),
            selected = selectedCurrency,
            onSelect = { selectedCurrency = it; showCurrencyDialog = false },
            onDismiss = { showCurrencyDialog = false }
        )
    }
    if (showDateFormatDialog) {
        OptionsDialog(
            title = "Formato de fecha",
            options = listOf("DD/MM/AAAA", "MM/DD/AAAA", "AAAA/MM/DD"),
            selected = selectedDateFormat,
            onSelect = { selectedDateFormat = it; showDateFormatDialog = false },
            onDismiss = { showDateFormatDialog = false }
        )
    }
    if (showTextSizeDialog) {
        OptionsDialog(
            title = "Tamaño de texto",
            options = listOf("Pequeño", "Normal", "Grande"),
            selected = selectedTextSize,
            onSelect = { selectedTextSize = it; showTextSizeDialog = false },
            onDismiss = { showTextSizeDialog = false }
        )
    }

    Scaffold(
        containerColor = NavyDeep,
        bottomBar = {
            BottomNavBar(selectedTab = 3, onTabSelected = { tab ->
                when (tab) {
                    0 -> onNavigate("home")
                    1 -> onNavigate("trips")
                    2 -> onNavigate("gallery")
                    3 -> onNavigate("settings")
                }
            })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyDeep)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = "Preferencias",
                        style = MaterialTheme.typography.headlineLarge,
                        color = White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Personaliza tu experiencia en Irida",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayMid
                    )
                }
            }

            // Language & Region section
            item {
                PreferenceSectionHeader(emoji = "🌐", title = "IDIOMA Y REGIÓN")
            }
            item {
                PreferenceCard {
                    PreferenceDropdownItem(
                        emoji = "🌍",
                        title = "Idioma",
                        subtitle = "Idioma de la interfaz",
                        value = selectedLanguage,
                        onClick = { showLanguageDialog = true }
                    )
                    PreferenceDivider()
                    PreferenceDropdownItem(
                        emoji = "💱",
                        title = "Moneda",
                        subtitle = "Para presupuestos",
                        value = selectedCurrency,
                        onClick = { showCurrencyDialog = true }
                    )
                    PreferenceDivider()
                    PreferenceDropdownItem(
                        emoji = "📅",
                        title = "Formato fecha",
                        subtitle = "Cómo se muestran las fechas",
                        value = selectedDateFormat,
                        onClick = { showDateFormatDialog = true }
                    )
                }
            }

            // Appearance section
            item {
                PreferenceSectionHeader(emoji = "🎨", title = "APARIENCIA")
            }
            item {
                PreferenceCard {
                    PreferenceToggleItem(
                        emoji = "🌙",
                        title = "Modo oscuro",
                        subtitle = "Tema de la aplicación",
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it }
                    )
                    PreferenceDivider()
                    PreferenceDropdownItem(
                        emoji = "🔤",
                        title = "Tamaño de texto",
                        subtitle = "Ajuste de accesibilidad",
                        value = selectedTextSize,
                        onClick = { showTextSizeDialog = true }
                    )
                }
            }

            // Notifications section
            item {
                PreferenceSectionHeader(emoji = "🔔", title = "NOTIFICACIONES")
            }
            item {
                PreferenceCard {
                    PreferenceToggleItem(
                        emoji = "🔔",
                        title = "Recordatorios de viaje",
                        subtitle = "Aviso 24h antes del vuelo",
                        checked = tripRemindersEnabled,
                        onCheckedChange = { tripRemindersEnabled = it }
                    )
                    PreferenceDivider()
                    PreferenceToggleItem(
                        emoji = "📧",
                        title = "Resumen semanal",
                        subtitle = "Email con próximos viajes",
                        checked = weeklyDigestEnabled,
                        onCheckedChange = { weeklyDigestEnabled = it }
                    )
                    PreferenceDivider()
                    PreferenceToggleItem(
                        emoji = "💡",
                        title = "Sugerencias IA",
                        subtitle = "Recomendaciones personalizadas",
                        checked = aiSuggestionsEnabled,
                        onCheckedChange = { aiSuggestionsEnabled = it }
                    )
                }
            }
        }
    }
}

@Composable
fun PreferenceSectionHeader(emoji: String, title: String) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = emoji, fontSize = 16.sp)
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = TurquoisePrimary,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PreferenceCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyLight)
    ) {
        Column(content = content)
    }
}

@Composable
fun PreferenceDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = GrayDark.copy(alpha = 0.5f),
        thickness = 0.5.dp
    )
}

@Composable
fun PreferenceToggleItem(
    emoji: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = White,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayMid
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NavyDeep,
                checkedTrackColor = TurquoisePrimary,
                uncheckedThumbColor = GrayMid,
                uncheckedTrackColor = GrayDark
            )
        )
    }
}

@Composable
fun PreferenceDropdownItem(
    emoji: String,
    title: String,
    subtitle: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = White,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayMid
                )
            }
        }
        TextButton(onClick = onClick) {
            Text(
                text = "$value ▾",
                style = MaterialTheme.typography.bodyMedium,
                color = TurquoisePrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun OptionsDialog(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavyLight,
        title = {
            Text(title, color = White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == selected,
                            onClick = { onSelect(option) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = TurquoisePrimary,
                                unselectedColor = GrayMid
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (option == selected) White else GrayMid
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = TurquoisePrimary)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreferencesScreenPreview() {
    IridaTheme {
        PreferencesScreen()
    }
}