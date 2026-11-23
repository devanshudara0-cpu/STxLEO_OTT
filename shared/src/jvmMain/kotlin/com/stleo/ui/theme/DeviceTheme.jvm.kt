package com.stleo.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

internal actual object DeviceTheme {
    @Composable
    @ReadOnlyComposable
    actual fun resolveDynamicTheme(): LeoColorScheme = darkScheme()

    @Composable
    @ReadOnlyComposable
    actual fun resolveDynamicPrimaryColor(): Color = LeoPrimaryColor.NORMAL.color

    @Composable
    @ReadOnlyComposable
    actual fun resolveDynamicSecondaryColor(): Color = LeoPrimaryColor.NORMAL.color
}