package com.allantoledo.nextclass.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColorPalette = darkColors(
    primary = GhostWhite,
    primaryVariant = Saffron,
    secondary = DarkOrange,
    secondaryVariant = Icterine,
    surface = GhostWhite,
    background = GhostWhite,
    onPrimary = BlackChocolate,
    onSecondary = GhostWhite,
    onSurface = BlackChocolate,
    onBackground = BlackChocolate
)

private val DarkColorPalette = lightColors(
    primary = BlackChocolate,
    primaryVariant = DarkOrange,
    secondary = Saffron,
    onPrimary = GhostWhite,
    onSecondary = BlackChocolate

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun NextClassTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}