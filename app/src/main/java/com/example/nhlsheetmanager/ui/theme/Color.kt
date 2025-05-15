package com.example.nhlsheetmanager.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Purple20 = Color(0xFFBB86FC)
val Purple35 = Color(0xFF6666FF)
val Purple50 = Color(0xFF6200EE)
val Purple70 = Color(0xFF3700B3)
val Teal20 = Color(0xFF03DAC5)
val Teal70 = Color(0xFF03DAC5)
val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val Red = Color(0xFFFF0000)
val Green = Color(0xFF00FF00)
val Blue = Color(0xFF0000FF)
val Pinky = Color(0xFFFF6666)
val MyGray = Color(0xFF666666)
val Grey = Color(0x33CCCCCC)
val GreenY = Color(0xFF66FF66)

// Light theme color scheme
val LightColors = lightColorScheme(
    primary = Purple70,
    onPrimary = White,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black
)

// Dark theme color scheme
val DarkColors = darkColorScheme(
    primary = Purple20,
    onPrimary = Black,
    background = Black,
    onBackground = White,
    surface = Grey,
    onSurface = White
)