package com.almahmoudApp.al_mahmoudapp.core.ui.liquid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.almahmoudApp.al_mahmoudapp.core.theme.AlMahmoudTheme

/**
 * Example usage for the reusable Liquid Glass API.
 */
@Composable
fun ExampleScreen(modifier: Modifier = Modifier) {
    LiquidHost(modifier = modifier.fillMaxSize()) {
        ExampleBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            LiquidGlass(
                style = LiquidGlassDefaults.Frosted,
                shape = LiquidGlassDefaults.RoundedShape,
            ) {
                Text(
                    text = "Hello",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            LiquidGlass(style = LiquidGlassDefaults.Crystal) {
                Button(onClick = {}) {
                    Text(text = "Login")
                }
            }
        }
    }
}

@Composable
private fun ExampleBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .liquidSource()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2FA084),
                        Color(0xFFE7F5EF),
                        Color(0xFFD8B86A),
                    ),
                ),
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun ExampleScreenPreview() {
    AlMahmoudTheme(dynamicColor = false) {
        ExampleScreen()
    }
}
