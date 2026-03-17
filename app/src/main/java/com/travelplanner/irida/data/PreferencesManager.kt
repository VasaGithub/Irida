package com.travelplanner.irida.data

import android.content.Context
import android.content.SharedPreferences
import com.travelplanner.irida.utils.LanguageChangeUtil

class PreferencesManager(private val context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("irida_prefs", Context.MODE_PRIVATE)

    // Instanciamos la utilidad siguiendo el patrón de las diapositivas
    private val languageChangeUtil by lazy { LanguageChangeUtil() }

    var selectedLanguageCode: String
        get() = preferences.getString("user_language", "es") ?: "es"
        set(value) {
            // 1. Guardar en memoria
            preferences.edit().putString("user_language", value).apply()
            // 2. Aplicar el cambio en el sistema inmediatamente
            languageChangeUtil.changeLanguage(context, value)
        }
}