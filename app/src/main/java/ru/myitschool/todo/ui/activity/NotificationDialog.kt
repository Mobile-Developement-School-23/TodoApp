package ru.myitschool.todo.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.myitschool.todo.R
import ru.myitschool.todo.ui.compose.Beige
import ru.myitschool.todo.ui.compose.GrayInactiveLight
import ru.myitschool.todo.ui.compose.GrayLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDialog(showDialog: Boolean, onAllow: () -> Unit = {}, onDissAllow: () -> Unit = {}) {
    if (showDialog) {
        AlertDialog(onDismissRequest = { onDissAllow.invoke()}) {
            Surface(
                Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .background(Color(0xFFF2F0F5))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        Icons.Filled.Notifications,
                        contentDescription = "Notification",
                        modifier = Modifier.padding(top = 15.dp),
                        tint = Color(0xFF495E93)
                    )
                    Text(
                        text = "Allow ${stringResource(id = R.string.app_name)} to send you notifications",
                        modifier = Modifier.padding(bottom = 10.dp, top = 10.dp)
                    )
                    Buttons(onAllow, onDissAllow)
                }
            }
        }
    }
}

@Composable
fun Buttons(onAllow: () -> Unit, onDissAllow: () -> Unit) {
    Column {
        Button(
            onClick = { onAllow.invoke()},
            shape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp,
                bottomStart = 4.dp,
                bottomEnd = 4.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
        ) {
            Text(text = "Allow")
        }
        Button(
            onClick = { onDissAllow.invoke() },
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 4.dp,
                bottomStart = 8.dp,
                bottomEnd = 8.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
        ) {
            Text(text = "Don't allow")
        }
    }
}

@Composable
@Preview
fun DialogPreview() {
    NotificationTheme {
        NotificationDialog(true)
    }
}

@Composable
fun NotificationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme =
        lightColorScheme(
            background = Beige,
            primary = Color(0xFFDBE2FF),
            onPrimary = Color.Black,
            secondary = Color.Black,
            surface = Color(0xFFF2F0F5),
            outline = GrayLight,
            surfaceVariant = Color(0xFFF2F0F5),
            outlineVariant = GrayInactiveLight
        ),
        content = content
    )
}