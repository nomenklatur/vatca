package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppThemeMode
import com.example.ui.navigation.VatcaNavGraph
import com.example.ui.theme.VatcaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as VatcaApplication
        val preferencesManager = app.preferencesManager

        setContent {
            val userPreferences by preferencesManager.userPreferencesFlow.collectAsStateWithLifecycle(
                initialValue = com.example.data.model.UserPreferences()
            )

            val isDark = when (userPreferences.appTheme) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            VatcaTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VatcaNavGraph()
                }
            }
        }
    }
}
