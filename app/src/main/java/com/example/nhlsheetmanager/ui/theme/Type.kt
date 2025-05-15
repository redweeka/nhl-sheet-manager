package com.example.nhlsheetmanager.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

@Composable
fun typography(defaultColor: Color = MaterialTheme.colorScheme.onBackground): Typography {
    return Typography(
        // Copy pasted
        displayLarge = TextStyle(fontSize = 56.sp, color = defaultColor),
        headlineLarge = TextStyle(fontSize = 36.sp, color = defaultColor),
        titleLarge = TextStyle(fontSize = 26.sp, color = defaultColor),
        bodyLarge = TextStyle(fontSize = 16.sp, color = defaultColor),
        labelLarge = TextStyle(fontSize = 14.sp, color = defaultColor),
        bodyMedium = TextStyle(fontSize = 14.sp, color = defaultColor),
        bodySmall = TextStyle(fontSize = 12.sp, color = defaultColor),
        labelMedium = TextStyle(fontSize = 12.sp, color = defaultColor),
        labelSmall = TextStyle(fontSize = 10.sp, color = defaultColor)
    )
}
