package com.stleo.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

internal expect object DeviceTheme {
    @Composable
    @ReadOnlyComposable
    fun resolveDynamicTheme(): LeoColorScheme
    @Composable
    @ReadOnlyComposable
    fun resolveDynamicPrimaryColor(): Color
    @Composable
    @ReadOnlyComposable
    fun resolveDynamicSecondaryColor(): Color
}