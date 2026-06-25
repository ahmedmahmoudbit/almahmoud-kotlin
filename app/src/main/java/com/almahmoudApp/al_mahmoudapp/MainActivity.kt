package com.almahmoudApp.al_mahmoudapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.almahmoudApp.al_mahmoudapp.core.navigation.App
import com.almahmoudApp.al_mahmoudapp.core.theme.AlMahmoudTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AlMahmoudTheme {
                App()
            }
        }
    }
}
