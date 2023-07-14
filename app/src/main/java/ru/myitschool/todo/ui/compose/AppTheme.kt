package ru.myitschool.todo.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
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
        } else {
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
        content = content
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewAppThemeLight() {
    AppTheme {
        Column {
            Row {
                Text(
                    text = "Background",
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(10.dp)
                )
                Text(
                    text = "Primary",
                    Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(10.dp)
                )
                Text(
                    text = "OnPrimary",
                    Modifier
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(10.dp)
                )
                Text(
                    text = "Secondary",
                    color = Color.White,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(10.dp)
                )
            }
            Row {
                Text(
                    text = "Surface",
                    Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(10.dp)
                )
                Text(
                    text = "Outline",
                    color = Color.White,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.outline)
                        .padding(10.dp)
                )
                Text(
                    text = "SurfaceVariant",
                    color = Color.White,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(10.dp)
                )
                Text(
                    text = "OutlineVariant",
                    color = Color.White,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .padding(10.dp)
                )
            }
        }
    }
}
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppThemeDark() {
    AppTheme {
        Column {
            Row {
                Text(
                    text = "Background",
                    color = Color.White,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(10.dp)
                )
                Text(
                    text = "Primary",
                    Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(10.dp)
                )
                Text(
                    text = "OnPrimary",
                    color = Color.White,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(10.dp)
                )
                Text(
                    text = "Secondary",
                    color = Color.Black,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(10.dp)
                )
            }
            Row {
                Text(
                    text = "Surface",
                    color = Color.White,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(10.dp)
                )
                Text(
                    text = "Outline",
                    color = Color.White,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.outline)
                        .padding(10.dp)
                )
                Text(
                    text = "SurfaceVariant",
                    color = Color.White,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(10.dp)
                )
                Text(
                    text = "OutlineVariant",
                    color = Color.White,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .padding(10.dp)
                )
            }
        }
    }
}