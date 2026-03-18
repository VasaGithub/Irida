package com.travelplanner.irida.ui.viewmodels // Ajusta tu paquete si es necesario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.travelplanner.irida.data.PreferencesManager // Ajusta el import según dónde guardaste el manager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {

    // ─── ESTADOS DE LA UI (Se inicializan leyendo de disco) ─────────────────

    private val _isDarkMode = MutableStateFlow(preferencesManager.getDarkMode())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _textSize = MutableStateFlow(preferencesManager.getTextSize())
    val textSize: StateFlow<String> = _textSize.asStateFlow()

    private val _currency = MutableStateFlow(preferencesManager.getCurrency())
    val currency: StateFlow<String> = _currency.asStateFlow()

    private val _dateFormat = MutableStateFlow(preferencesManager.getDateFormat())
    val dateFormat: StateFlow<String> = _dateFormat.asStateFlow()

    private val _tripReminders = MutableStateFlow(preferencesManager.getTripReminders())
    val tripReminders: StateFlow<Boolean> = _tripReminders.asStateFlow()

    private val _weeklySummary = MutableStateFlow(preferencesManager.getWeeklySummary())
    val weeklySummary: StateFlow<Boolean> = _weeklySummary.asStateFlow()

    private val _aiSuggestions = MutableStateFlow(preferencesManager.getAiSuggestions())
    val aiSuggestions: StateFlow<Boolean> = _aiSuggestions.asStateFlow()


    // ─── FUNCIONES PARA CAMBIAR LOS AJUSTES ─────────────────────────────────
    // Estas funciones actualizan la pantalla al instante y guardan en disco

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        preferencesManager.saveDarkMode(enabled)
    }

    fun setTextSize(size: String) {
        _textSize.value = size
        preferencesManager.saveTextSize(size)
    }

    fun setCurrency(newCurrency: String) {
        _currency.value = newCurrency
        preferencesManager.saveCurrency(newCurrency)
    }

    fun setDateFormat(format: String) {
        _dateFormat.value = format
        preferencesManager.saveDateFormat(format)
    }

    fun setTripReminders(enabled: Boolean) {
        _tripReminders.value = enabled
        preferencesManager.saveTripReminders(enabled)
    }

    fun setWeeklySummary(enabled: Boolean) {
        _weeklySummary.value = enabled
        preferencesManager.saveWeeklySummary(enabled)
    }

    fun setAiSuggestions(enabled: Boolean) {
        _aiSuggestions.value = enabled
        preferencesManager.saveAiSuggestions(enabled)
    }
}

// ─── FACTORY (¡Muy Importante!) ─────────────────────────────────────────────
// Como nuestro ViewModel necesita recibir el PreferencesManager por parámetro,
// Android necesita esta "Fábrica" para saber cómo construirlo.
class SettingsViewModelFactory(private val preferencesManager: PreferencesManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(preferencesManager) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}