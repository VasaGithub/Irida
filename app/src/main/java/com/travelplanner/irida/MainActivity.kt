package com.travelplanner.irida

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.travelplanner.irida.data.PreferencesManager
import com.travelplanner.irida.utils.LanguageChangeUtil
import com.travelplanner.irida.ui.AppNavigation
import com.travelplanner.irida.ui.theme.IridaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Leemos el idioma guardado usando el manager
        val prefs = PreferencesManager(this)
        val savedLanguage = prefs.selectedLanguageCode

        // 2. Aplicamos el idioma al arrancar la app usando la utilidad
        LanguageChangeUtil().changeLanguage(this, savedLanguage)

        enableEdgeToEdge()
        setContent {
            IridaTheme {
                AppNavigation()
            }
        }
    }
}