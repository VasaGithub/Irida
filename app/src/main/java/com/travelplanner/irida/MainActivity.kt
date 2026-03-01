package com.travelplanner.irida

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.travelplanner.irida.ui.screens.SplashScreen
import com.travelplanner.irida.ui.theme.IridaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IridaTheme {
                SplashScreen()
            }
        }
    }
}