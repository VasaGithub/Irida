package com.travelplanner.irida.ui.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelplanner.irida.ui.theme.*
import com.travelplanner.irida.ui.viewmodels.TripListViewModel
import com.travelplanner.irida.ui.viewmodels.TripListUiState
import java.time.LocalDate

private const val EDIT_TAG = "EditTripScreen"

@Composable
fun EditTripScreen(
    tripId: String,
    onNavigateBack: () -> Unit = {},
    onTripUpdated: () -> Unit = {},
    viewModel: TripListViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val validationErrors by viewModel.validationErrors.collectAsState()

    // Busca el viaje a editar en el estado actual del ViewModel
    val tripToEdit = remember(uiState) {
        (uiState as? TripListUiState.Success)?.trips?.find { it.id == tripId }
    }

    // Campos del formulario — se prerellenan con los datos del viaje
    var title by remember(tripToEdit) { mutableStateOf(tripToEdit?.title ?: "") }
    var description by remember(tripToEdit) { mutableStateOf(tripToEdit?.description ?: "") }
    var destination by remember(tripToEdit) { mutableStateOf(tripToEdit?.destination ?: "") }
    var startDate by remember(tripToEdit) { mutableStateOf<LocalDate?>(tripToEdit?.startDate) }
    var endDate by remember(tripToEdit) { mutableStateOf<LocalDate?>(tripToEdit?.endDate) }
    var emoji by remember(tripToEdit) { mutableStateOf(tripToEdit?.emoji ?: "✈️") }
    var budgetText by remember(tripToEdit) { mutableStateOf(tripToEdit?.budget?.toString() ?: "") }

    LaunchedEffect(Unit) {
        viewModel.clearValidationErrors()
        Log.d(EDIT_TAG, "EditTripScreen: abierto para tripId=$tripId")
    }

    // DatePickerDialogs (T1.3: sin texto libre)
    val startDatePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            startDate = LocalDate.of(year, month + 1, day)
            Log.d(EDIT_TAG, "startDate actualizada: $startDate")
        },
        startDate?.year ?: LocalDate.now().year,
        (startDate?.monthValue ?: LocalDate.now().monthValue) - 1,
        startDate?.dayOfMonth ?: LocalDate.now().dayOfMonth
    )

    val endDatePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            endDate = LocalDate.of(year, month + 1, day)
            Log.d(EDIT_TAG, "endDate actualizada: $endDate")
        },
        endDate?.year ?: LocalDate.now().year,
        (endDate?.monthValue ?: LocalDate.now().monthValue) - 1,
        endDate?.dayOfMonth ?: LocalDate.now().dayOfMonth
    )

    // Si el viaje no existe mostramos un error
    if (tripToEdit == null && uiState is TripListUiState.Success) {
        Log.e(EDIT_TAG, "Viaje con id=$tripId no encontrado")
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyDeep),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("Viaje no encontrado", color = ErrorRed)
        }
        return
    }

    Scaffold(
        containerColor = NavyDeep,
        topBar = {
            EditTripTopBar(onNavigateBack = onNavigateBack)
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
            item {
                TripFormField(
                    label = "Título del viaje *",
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "Ej: Aventura en Tokio",
                    error = validationErrors["title"]
                )
            }

            item {
                TripFormField(
                    label = "Descripción *",
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Describe brevemente el viaje",
                    error = validationErrors["description"],
                    singleLine = false,
                    minLines = 3
                )
            }

            item {
                TripFormField(
                    label = "Destino",
                    value = destination,
                    onValueChange = { destination = it },
                    placeholder = "Ej: Tokio, Japón"
                )
            }

            item { FormSectionLabel(text = "FECHAS DEL VIAJE") }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DatePickerField(
                        modifier = Modifier.weight(1f),
                        label = "Fecha inicio *",
                        date = startDate,
                        error = validationErrors["startDate"],
                        onClick = { startDatePicker.show() }
                    )
                    DatePickerField(
                        modifier = Modifier.weight(1f),
                        label = "Fecha fin *",
                        date = endDate,
                        error = validationErrors["endDate"],
                        onClick = { endDatePicker.show() }
                    )
                }
            }

            item {
                TripFormField(
                    label = "Emoji del viaje",
                    value = emoji,
                    onValueChange = { if (it.length <= 2) emoji = it },
                    placeholder = "✈️"
                )
            }

            item {
                TripFormField(
                    label = "Presupuesto (€)",
                    value = budgetText,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) budgetText = it },
                    placeholder = "0.00",
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        Log.d(EDIT_TAG, "Guardar cambios pulsado para tripId=$tripId")
                        val success = viewModel.editTrip(
                            id = tripId,
                            title = title,
                            description = description,
                            destination = destination,
                            startDate = startDate,
                            endDate = endDate,
                            emoji = emoji.ifBlank { "✈️" },
                            budget = budgetText.toDoubleOrNull() ?: 0.0,
                            budgetSpent = tripToEdit?.budgetSpent ?: 0.0
                        )
                        if (success) {
                            Log.i(EDIT_TAG, "Viaje actualizado, navegando atrás")
                            onTripUpdated()
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
                        text = "Guardar cambios",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

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
                    Text("Cancelar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Editar viaje",
                color = White,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyMid)
    )
}

@Preview(showBackground = true)
@Composable
fun EditTripScreenPreview() {
    IridaTheme {
        EditTripScreen(tripId = "1")
    }
}