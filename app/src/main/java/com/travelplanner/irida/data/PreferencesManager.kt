package com.travelplanner.irida.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.travelplanner.irida.utils.LanguageChangeUtil

class PreferencesManager(private val context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences("irida_prefs", Context.MODE_PRIVATE)
    private val languageChangeUtil by lazy { LanguageChangeUtil() }

    // ─── IDIOMA  ───────────────────────────────────────────────────────────

    var selectedLanguageCode: String
        get() = preferences.getString("user_language", "es") ?: "es"
        set(value) {
            // 1. Guardar en memoria usando la extensión KTX
            preferences.edit { putString("user_language", value) }
            // 2. Aplicar el cambio en el sistema inmediatamente
            languageChangeUtil.changeLanguage(context, value)
        }

    // ─── APARIENCIA Y PANTALLA ──────────────────────────────────────────────

    fun saveDarkMode(isDark: Boolean) {
        preferences.edit { putBoolean("dark_mode", isDark) }
    }
    fun getDarkMode(): Boolean {
        return preferences.getBoolean("dark_mode", false) // false por defecto
    }

    fun saveTextSize(size: String) {
        preferences.edit { putString("text_size", size) }
    }
    fun getTextSize(): String {
        return preferences.getString("text_size", "Mediano") ?: "Mediano"
    }

    // ─── REGIONAL Y FORMATOS ────────────────────────────────────────────────

    fun saveCurrency(currency: String) {
        preferences.edit { putString("currency", currency) }
    }
    fun getCurrency(): String {
        return preferences.getString("currency", "EUR") ?: "EUR"
    }

    fun saveDateFormat(format: String) {
        preferences.edit { putString("date_format", format) }
    }
    fun getDateFormat(): String {
        return preferences.getString("date_format", "dd/MM/yyyy") ?: "dd/MM/yyyy"
    }

    // ─── NOTIFICACIONES Y PRIVACIDAD ────────────────────────────────────────

    fun saveTripReminders(enabled: Boolean) {
        preferences.edit { putBoolean("trip_reminders", enabled) }
    }
    fun getTripReminders(): Boolean {
        return preferences.getBoolean("trip_reminders", true) // true por defecto
    }

    fun saveWeeklySummary(enabled: Boolean) {
        preferences.edit { putBoolean("weekly_summary", enabled) }
    }
    fun getWeeklySummary(): Boolean {
        return preferences.getBoolean("weekly_summary", true)
    }

    fun saveAiSuggestions(enabled: Boolean) {
        preferences.edit { putBoolean("ai_suggestions", enabled) }
    }
    fun getAiSuggestions(): Boolean {
        return preferences.getBoolean("ai_suggestions", true)
    }

    // ─── PERFIL DE USUARIO ──────────────────────────────────────────────────

    fun saveUsername(username: String) {
        preferences.edit { putString("username", username) }
    }
    fun getUsername(): String {
        return preferences.getString("username", "") ?: ""
    }

    fun saveBirthdate(birthdate: String) {
        preferences.edit { putString("birthdate", birthdate) }
    }
    fun getBirthdate(): String {
        return preferences.getString("birthdate", "") ?: ""
    }
}