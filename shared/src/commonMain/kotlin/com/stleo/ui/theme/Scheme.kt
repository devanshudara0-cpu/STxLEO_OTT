package com.stleo.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * Maps to the XML custom attrs declared in attrs.xml:
 * TODO: Remove this comment when we migrate fully
 *  and attrs.xml will no longer be used at all.
 *
 * | XML ?attr                | Here               |
 * |--------------------------|--------------------|
 * | primaryBlackBackground   | [background]       |
 * | primaryGrayBackground    | [surfaceVariant]   |
 * | iconGrayBackground       | [surface]          |
 * | boxItemBackground        | [surfaceContainer] |
 * | textColor                | [onBackground]     |
 * | grayTextColor            | [onSurfaceVariant] |
 * | iconColor                | [icon]             |
 * | colorPrimary             | [primary]          |
 * | colorOngoing             | [ongoing]          |
 *
 * All fields are [MutableState] so Compose recomposes automatically
 * if the scheme is swapped at runtime (e.g. user changes theme without restart).
 */
@Stable
class LeoColorScheme(
    background: Color,
    surfaceVariant: Color,
    surface: Color,
    surfaceContainer: Color,
    onBackground: Color,
    onSurfaceVariant: Color,
    icon: Color,
    primary: Color,
    ongoing: Color,
    isLight: Boolean,
) {
    /** primaryBlackBackground */
    var background by mutableStateOf(background)

    /** primaryGrayBackground */
    var surfaceVariant by mutableStateOf(surfaceVariant)

    /** iconGrayBackground */
    var surface by mutableStateOf(surface)

    /** boxItemBackground */
    var surfaceContainer by mutableStateOf(surfaceContainer)

    /** textColor */
    var onBackground by mutableStateOf(onBackground)

    /** grayTextColor */
    var onSurfaceVariant by mutableStateOf(onSurfaceVariant)

    /** iconColor */
    var icon by mutableStateOf(icon)

    /** colorPrimary */
    var primary by mutableStateOf(primary)

    /** colorOngoing */
    var ongoing by mutableStateOf(ongoing)
    var isLight by mutableStateOf(isLight)

    fun copy(
        background: Color = this.background,
        surfaceVariant: Color = this.surfaceVariant,
        surface: Color = this.surface,
        surfaceContainer: Color = this.surfaceContainer,
        onBackground: Color = this.onBackground,
        onSurfaceVariant: Color = this.onSurfaceVariant,
        icon: Color = this.icon,
        primary: Color = this.primary,
        ongoing: Color = this.ongoing,
        isLight: Boolean = this.isLight,
    ) = LeoColorScheme(
        background, surfaceVariant, surface, surfaceContainer,
        onBackground, onSurfaceVariant, icon, primary, ongoing, isLight,
    )
}

internal fun darkScheme() = LeoColorScheme(
    background = LeoPalette.DarkBlackBg,
    surfaceVariant = LeoPalette.DarkPrimaryGrayBg,
    surface = LeoPalette.DarkIconGrayBg,
    surfaceContainer = LeoPalette.DarkBoxItemBg,
    onBackground = LeoPalette.DarkText,
    onSurfaceVariant = LeoPalette.DarkGrayText,
    icon = LeoPalette.DarkIcon,
    primary = LeoPalette.Primary,
    ongoing = LeoPalette.Ongoing,
    isLight = false,
)


internal fun amoledScheme() = darkScheme().copy(
    background = LeoPalette.AmoledBlack,
    surface = LeoPalette.AmoledBlack,
    surfaceVariant = LeoPalette.AmoledBlack,
    surfaceContainer = LeoPalette.AmoledBlack,
)

internal fun amoledLightScheme() = amoledScheme().copy(
    surfaceVariant = LeoPalette.AmoledNearBlack,
)

internal fun lightScheme() = LeoColorScheme(
    background = LeoPalette.LightBlackBg,
    surfaceVariant = LeoPalette.LightPrimaryGrayBg,
    surface = LeoPalette.LightIconGrayBg,
    surfaceContainer = LeoPalette.LightBoxItemBg,
    onBackground = LeoPalette.LightText,
    onSurfaceVariant = LeoPalette.LightGrayText,
    icon = LeoPalette.LightIcon,
    primary = LeoPalette.Primary,
    ongoing = LeoPalette.Ongoing,
    isLight = true,
)

internal fun draculaScheme() = LeoColorScheme(
    background = LeoPalette.DraculaBlackBg,
    surfaceVariant = LeoPalette.DraculaPrimaryGrayBg,
    surface = LeoPalette.DraculaIconGrayBg,
    surfaceContainer = LeoPalette.DraculaBoxItemBg,
    onBackground = LeoPalette.DraculaText,
    onSurfaceVariant = LeoPalette.DraculaGrayText,
    icon = LeoPalette.DraculaIcon,
    primary = LeoPalette.Primary,
    ongoing = LeoPalette.Ongoing,
    isLight = false,
)

internal fun lavenderScheme() = LeoColorScheme(
    background = LeoPalette.LavenderBlackBg,
    surfaceVariant = LeoPalette.LavenderPrimaryGrayBg,
    surface = LeoPalette.LavenderIconGrayBg,
    surfaceContainer = LeoPalette.LavenderBoxItemBg,
    onBackground = LeoPalette.LavenderText,
    onSurfaceVariant = LeoPalette.LavenderGrayText,
    icon = LeoPalette.LavenderIcon,
    primary = LeoPalette.Primary,
    ongoing = LeoPalette.Ongoing,
    isLight = true,
)

internal fun silentBlueScheme() = LeoColorScheme(
    background = LeoPalette.SilentBlueBlackBg,
    surfaceVariant = LeoPalette.SilentBluePrimaryGrayBg,
    surface = LeoPalette.SilentBlueIconGrayBg,
    surfaceContainer = LeoPalette.SilentBlueBoxItemBg,
    onBackground = LeoPalette.SilentBlueText,
    onSurfaceVariant = LeoPalette.SilentBlueGrayText,
    icon = LeoPalette.SilentBlueIcon,
    primary = LeoPalette.Primary,
    ongoing = LeoPalette.Ongoing,
    isLight = false,
)