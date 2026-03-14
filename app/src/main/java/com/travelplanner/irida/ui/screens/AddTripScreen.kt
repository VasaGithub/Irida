package com.travelplanner.irida.ui.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelplanner.irida.R
import com.travelplanner.irida.ui.theme.*
import com.travelplanner.irida.ui.viewmodels.TripListViewModel
import com.travelplanner.irida.ui.viewmodels.TripListUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val TAG = "AddTripScreen"
private val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
fun AddTripScreen(
    onNavigateBack: () -> Unit = {},
    onTripAdded: () -> Unit = {},
    viewModel: TripListViewModel = viewModel()
) {
    val context = LocalContext.current
    val validationErrors by viewModel.validationErrors.collectAsState()

    // Campos del formulario
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var emoji by remember { mutableStateOf("✈️") }
    var budgetText by remember { mutableStateOf("") }

    // Limpiar errores al entrar en la pantalla
    LaunchedEffect(Unit) {
        viewModel.clearValidationErrors()
        Log.d(TAG, context.getString(R.string.trip_form))
    }

    // DatePickerDialogs — usamos el DatePickerDialog de Android (T1.3: sin texto libre)
    val startDatePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            startDate = LocalDate.of(year, month + 1, day)
            Log.d(TAG, context.getString(R.string.date_selected, startDate))
        },
        LocalDate.now().year,
        LocalDate.now().monthValue - 1,
        LocalDate.now().dayOfMonth
    )

    val endDatePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            endDate = LocalDate.of(year, month + 1, day)
            Log.d(TAG, context.getString(R.string.date_end_select, endDate))
        },
        LocalDate.now().year,
        LocalDate.now().monthValue - 1,
        LocalDate.now().dayOfMonth
    )

    Scaffold(
        containerColor = NavyDeep,
        topBar = {
            AddTripTopBar(onNavigateBack = onNavigateBack)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyDeep)
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            // Título
            item {
                TripFormField(
                    label = stringResource(R.string.trip_title),
                    value = title,
                    onValueChange = { title = it },
                    placeholder = stringResource(R.string.ej_trip_title),
                    error = validationErrors["title"]
                )
            }

            // Descripción
            item {
                TripFormField(
                    label = stringResource(R.string.trip_desc),
                    value = description,
                    onValueChange = { description = it },
                    placeholder = stringResource(R.string.trip_desc_desc),
                    error = validationErrors["description"],
                    singleLine = false,
                    minLines = 3
                )
            }

            // Destino
            item {
                TripFormField(
                    label = stringResource(R.string.trip_dest),
                    value = destination,
                    onValueChange = { destination = it },
                    placeholder = stringResource(R.string.trip_dest_ex)
                )
            }

            // Fechas — DatePickers (T1.3: no se permite texto libre)
            item {
                FormSectionLabel(text = stringResource(R.string.trip_date))
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Fecha de inicio
                    DatePickerField(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.in_date),
                        date = startDate,
                        error = validationErrors["startDate"],
                        onClick = { startDatePicker.show() }
                    )
                    // Fecha de fin
                    DatePickerField(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.end_date),
                        date = endDate,
                        error = validationErrors["endDate"],
                        onClick = { endDatePicker.show() }
                    )
                }
            }

            // Emoji
            item {
                TripFormField(
                    label = stringResource(R.string.trip_emoji),
                    value = emoji,
                    onValueChange = { if (it.length <= 2) emoji = it },
                    placeholder = "✈️"
                )
            }

            // Presupuesto
            item {
                TripFormField(
                    label = stringResource(R.string.presupuesto),
                    value = budgetText,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) budgetText = it },
                    placeholder = stringResource(R.string.placeholder_presupuesto),
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                )
            }

            // Botón guardar
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        Log.d(TAG, "Guardar viaje pulsado")
                        val success = viewModel.addTrip(
                            title = title,
                            description = description,
                            destination = destination,
                            startDate = startDate,
                            endDate = endDate,
                            emoji = emoji.ifBlank { "✈️" },
                            budget = budgetText.toDoubleOrNull() ?: 0.0
                        )
                        if (success) {
                            Log.i(TAG, "Viaje añadido, navegando atrás")
                            onTripAdded()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TurquoisePrimary,
                        contentColor = NavyDeep
                    )
                ) {
                    Text(
                        text = stringResource(R.string.btn_guardar_viaje),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Botón cancelar
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrayDark),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GrayMid)
                ) {
                    Text(text = stringResource(R.string.btn_cancelar))
                }
            }
        }
    }
}

// ── Componentes compartidos del formulario ─────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.title_nuevo_viaje),
                color = White,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_volver),
                    tint = White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyMid)
    )
}

@Composable
fun FormSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = TurquoisePrimary,
        letterSpacing = androidx.compose.ui.unit.TextUnit(2f, androidx.compose.ui.unit.TextUnitType.Sp),
        fontWeight = FontWeight.Bold
    )
}

/**
 * Campo de texto genérico del formulario con soporte de error.
 */
@Composable
fun TripFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    error: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (error != null) ErrorRed else GrayMid,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(placeholder, color = GrayDark)
            },
            singleLine = singleLine,
            minLines = minLines,
            isError = error != null,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = keyboardType
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = White,
                unfocusedTextColor = White,
                focusedBorderColor = TurquoisePrimary,
                unfocusedBorderColor = GrayDark,
                errorBorderColor = ErrorRed,
                focusedContainerColor = NavyLight,
                unfocusedContainerColor = NavyLight,
                errorContainerColor = NavyLight,
                cursorColor = TurquoisePrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )
        // Mensaje de error visible bajo el campo (T3.1)
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = ErrorRed
            )
        }
    }
}

/**
 * Campo de fecha que abre un DatePickerDialog al pulsarlo.
 * No permite escritura libre (T1.3).
 */
@Composable
fun DatePickerField(
    modifier: Modifier = Modifier,
    label: String,
    date: LocalDate?,
    error: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (error != null) ErrorRed else GrayMid,
            fontWeight = FontWeight.Medium
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyLight, RoundedCornerShape(12.dp))
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date?.format(displayFormatter) ?: stringResource(R.string.formato_fecha),
                    color = if (date != null) White else GrayDark,
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.cd_seleccionar_fecha),
                    tint = if (error != null) ErrorRed else TurquoisePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = ErrorRed
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddTripScreenPreview() {
    IridaTheme {
        AddTripScreen()
    }
}