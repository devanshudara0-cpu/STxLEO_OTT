package com.stleo.ui.theme

import android.content.Context
import android.os.Build
import androidx.preference.PreferenceManager

/** TODO: This should be replaced with a better system */

fun perfToMode(perf: String?) =
    when (perf) {
        "System" -> LeoThemeMode.FollowSystem
        "Black" -> LeoThemeMode.Dark
        "Light" -> LeoThemeMode.Light
        "Amoled" -> LeoThemeMode.Amoled
        "AmoledLight" -> LeoThemeMode.AmoledLight
        "Dracula" -> LeoThemeMode.Dracula
        "Lavender" -> LeoThemeMode.Lavender
        "SilentBlue" -> LeoThemeMode.SilentBlue
        "Monet" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            LeoThemeMode.Dynamic
        } else {
            LeoThemeMode.Dark
        }

        else -> LeoThemeMode.Dark
    }

fun perfToColor(perf : String?) = when (perf) {
    "Normal" -> LeoPrimaryColor.NORMAL
    "Blue" -> LeoPrimaryColor.BLUE
    "Purple" -> LeoPrimaryColor.PURPLE
    "Green" -> LeoPrimaryColor.GREEN
    "GreenApple" -> LeoPrimaryColor.GREEN_APPLE
    "Red" -> LeoPrimaryColor.RED
    "Banana" -> LeoPrimaryColor.BANANA
    "Party" -> LeoPrimaryColor.PARTY
    "Pink" -> LeoPrimaryColor.PINK
    "CarnationPink" -> LeoPrimaryColor.CARNATION_PINK
    "Maroon" -> LeoPrimaryColor.MAROON
    "DarkGreen" -> LeoPrimaryColor.DARK_GREEN
    "NavyBlue" -> LeoPrimaryColor.NAVY_BLUE
    "Grey" -> LeoPrimaryColor.GREY
    "White" -> LeoPrimaryColor.WHITE
    "Brown" -> LeoPrimaryColor.BROWN
    "Orange" -> LeoPrimaryColor.ORANGE
    "DandelionYellow" -> LeoPrimaryColor.DANDELION_YELLOW
    "CoolBlue" -> LeoPrimaryColor.COOL_BLUE
    "Lavender" -> LeoPrimaryColor.LAVENDER
    "Monet" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        LeoPrimaryColor.DYNAMIC
    } else {
        LeoPrimaryColor.NORMAL
    }

    "Monet2" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        LeoPrimaryColor.DYNAMIC_TWO
    } else {
        LeoPrimaryColor.NORMAL
    }

    else -> LeoPrimaryColor.NORMAL
}

fun Context.loadThemeMode(): LeoThemeMode {
    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    return perfToMode(prefs.getString("theme_key", "AmoledLight"))
}

fun Context.loadPrimaryColor(): LeoPrimaryColor {
    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    return perfToColor(prefs.getString("primary_color_key", "Normal"))
}