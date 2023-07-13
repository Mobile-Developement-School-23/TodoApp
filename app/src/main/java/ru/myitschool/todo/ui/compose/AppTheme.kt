package ru.myitschool.todo.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme (
        colorScheme = if (isSystemInDarkTheme()) {
            darkColorScheme(
                background = BlackBackground,
                primary = Blue,
                onPrimary = BlackItemBackground,
                secondary = Color.White,
                surface = BlackBackground,
                outline = GrayDark,
                surfaceVariant = BlueTransparent,
                outlineVariant = GrayInactiveDark
            )
        }
        else {
            lightColorScheme(
                background = Beige,
                primary = Blue,
                onPrimary = Color.White,
                secondary = Color.Black,
                surface = Beige,
                outline = GrayLight,
                surfaceVariant = BlueTransparent,
                outlineVariant = GrayInactiveLight
            )
        },
        content=content
    )
}