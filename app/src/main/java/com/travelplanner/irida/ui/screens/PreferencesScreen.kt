package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travelplanner.irida.R
import com.travelplanner.irida.ui.theme.*

@Composable
fun PreferencesScreen(
    onNavigate: (String) -> Unit = {}
) {
    // Para simplificar, usamos directamente el texto en el estado,
    // pero idealmente deberías guardar una clave de preferencia en lugar del texto traducido.
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

    if (showLanguageDialog) {
        OptionsDialog(
            title = stringResource(R.string.pref_title_idioma),
            options = listOf(
                stringResource(R.string.lang_es),
                stringResource(R.string.lang_en),
                stringResource(R.string.lang_ca),
                stringResource(R.string.lang_fr),
                stringResource(R.string.lang_de)
            ),
            selected = selectedLanguage,
            onSelect = { selectedLanguage = it; showLanguageDialog = false },
            onDismiss = { showLanguageDialog = false }
        )
    }
    if (showCurrencyDialog) {
        OptionsDialog(
            title = stringResource(R.string.pref_title_moneda),
            options = listOf(
                stringResource(R.string.curr_eur),
                stringResource(R.string.curr_usd),
                stringResource(R.string.curr_gbp),
                stringResource(R.string.curr_jpy)
            ),
            selected = selectedCurrency,
            onSelect = { selectedCurrency = it; showCurrencyDialog = false },
            onDismiss = { showCurrencyDialog = false }
        )
    }
    if (showDateFormatDialog) {
        OptionsDialog(
            title = stringResource(R.string.pref_title_formato_fecha),
            options = listOf(
                stringResource(R.string.date_fmt_dmy),
                stringResource(R.string.date_fmt_mdy),
                stringResource(R.string.date_fmt_ymd)
            ),
            selected = selectedDateFormat,
            onSelect = { selectedDateFormat = it; showDateFormatDialog = false },
            onDismiss = { showDateFormatDialog = false }
        )
    }
    if (showTextSizeDialog) {
        OptionsDialog(
            title = stringResource(R.string.pref_title_tamano_texto),
            options = listOf(
                stringResource(R.string.size_small),
                stringResource(R.string.size_normal),
                stringResource(R.string.size_large)
            ),
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
                    1 -> onNavigate("activities")
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
                        text = stringResource(R.string.screen_title_preferencias),
                        style = MaterialTheme.typography.headlineLarge,
                        color = White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = stringResource(R.string.screen_subtitle_preferencias),
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayMid
                    )
                }
            }

            // Language & Region section
            item { PreferenceSectionHeader(emoji = "🌐", title = stringResource(R.string.section_idioma_region)) }
            item {
                PreferenceCard {
                    PreferenceDropdownItem(
                        emoji = "🌍",
                        title = stringResource(R.string.pref_title_idioma),
                        subtitle = stringResource(R.string.pref_sub_idioma),
                        value = selectedLanguage,
                        onClick = { showLanguageDialog = true }
                    )
                    PreferenceDivider()
                    PreferenceDropdownItem(
                        emoji = "💱",
                        title = stringResource(R.string.pref_title_moneda),
                        subtitle = stringResource(R.string.pref_sub_moneda),
                        value = selectedCurrency,
                        onClick = { showCurrencyDialog = true }
                    )
                    PreferenceDivider()
                    PreferenceDropdownItem(
                        emoji = "📅",
                        title = stringResource(R.string.pref_title_formato_fecha),
                        subtitle = stringResource(R.string.pref_sub_formato_fecha),
                        value = selectedDateFormat,
                        onClick = { showDateFormatDialog = true }
                    )
                }
            }

            // Appearance section
            item { PreferenceSectionHeader(emoji = "🎨", title = stringResource(R.string.section_apariencia)) }
            item {
                PreferenceCard {
                    PreferenceToggleItem(
                        emoji = "🌙",
                        title = stringResource(R.string.pref_title_modo_oscuro),
                        subtitle = stringResource(R.string.pref_sub_modo_oscuro),
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it }
                    )
                    PreferenceDivider()
                    PreferenceDropdownItem(
                        emoji = "🔤",
                        title = stringResource(R.string.pref_title_tamano_texto),
                        subtitle = stringResource(R.string.pref_sub_tamano_texto),
                        value = selectedTextSize,
                        onClick = { showTextSizeDialog = true }
                    )
                }
            }

            // Notifications section
            item { PreferenceSectionHeader(emoji = "🔔", title = stringResource(R.string.section_notificaciones)) }
            item {
                PreferenceCard {
                    PreferenceToggleItem(
                        emoji = "🔔",
                        title = stringResource(R.string.pref_title_recordatorios),
                        subtitle = stringResource(R.string.pref_sub_recordatorios),
                        checked = tripRemindersEnabled,
                        onCheckedChange = { tripRemindersEnabled = it }
                    )
                    PreferenceDivider()
                    PreferenceToggleItem(
                        emoji = "📧",
                        title = stringResource(R.string.pref_title_resumen),
                        subtitle = stringResource(R.string.pref_sub_resumen),
                        checked = weeklyDigestEnabled,
                        onCheckedChange = { weeklyDigestEnabled = it }
                    )
                    PreferenceDivider()
                    PreferenceToggleItem(
                        emoji = "💡",
                        title = stringResource(R.string.pref_title_sugerencias),
                        subtitle = stringResource(R.string.pref_sub_sugerencias),
                        checked = aiSuggestionsEnabled,
                        onCheckedChange = { aiSuggestionsEnabled = it }
                    )
                }
            }

            // About section
            item { PreferenceSectionHeader(emoji = "ℹ️", title = stringResource(R.string.section_acerca_de)) }
            item {
                PreferenceCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate("about") }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = "ℹ️", fontSize = 20.sp)
                            Column {
                                Text(
                                    text = stringResource(R.string.pref_title_acerca),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = White,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = stringResource(R.string.pref_sub_acerca),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = GrayMid
                                )
                            }
                        }
                        Text(text = "›", color = GrayMid, fontSize = 20.sp)
                    }

                    PreferenceDivider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate("terms") }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = "📄", fontSize = 20.sp)
                            Column {
                                Text(
                                    text = stringResource(R.string.pref_title_terminos),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = White,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = stringResource(R.string.pref_sub_terminos),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = GrayMid
                                )
                            }
                        }
                        Text(text = "›", color = GrayMid, fontSize = 20.sp)
                    }
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
                // Aquí usamos %1$s para insertar la variable de valor en el texto formateado
                text = stringResource(R.string.dropdown_value, value),
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
                Text(stringResource(R.string.btn_cerrar), color = TurquoisePrimary)
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