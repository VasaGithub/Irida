package com.travelplanner.irida.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.travelplanner.irida.data.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

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

    fun setDarkMode(enabled: Boolean) { _isDarkMode.value = enabled; preferencesManager.saveDarkMode(enabled) }
    fun setTextSize(size: String) { _textSize.value = size; preferencesManager.saveTextSize(size) }
    fun setCurrency(newCurrency: String) { _currency.value = newCurrency; preferencesManager.saveCurrency(newCurrency) }
    fun setDateFormat(format: String) { _dateFormat.value = format; preferencesManager.saveDateFormat(format) }
    fun setTripReminders(enabled: Boolean) { _tripReminders.value = enabled; preferencesManager.saveTripReminders(enabled) }
    fun setWeeklySummary(enabled: Boolean) { _weeklySummary.value = enabled; preferencesManager.saveWeeklySummary(enabled) }
    fun setAiSuggestions(enabled: Boolean) { _aiSuggestions.value = enabled; preferencesManager.saveAiSuggestions(enabled) }
}
