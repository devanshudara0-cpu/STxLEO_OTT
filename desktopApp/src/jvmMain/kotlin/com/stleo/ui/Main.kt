package com.stleo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.stleo.ui.compose.BlackButton
import com.stleo.ui.compose.LocalFocusOutlineDefault
import com.stleo.ui.compose.WhiteButton
import com.stleo.ui.generated.resources.Res
import com.stleo.ui.generated.resources.app_name
import com.stleo.ui.generated.resources.default_icon
import com.stleo.ui.generated.resources.preview
import com.stleo.ui.theme.LeoTheme
import com.stleo.ui.theme.LeoThemeMode
import com.mihon.presentation.settings.widget.SwitchPreferenceWidget
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.default_icon)
    ) {
        LeoTheme(mode = LeoThemeMode.Dark) {
            CompositionLocalProvider(LocalFocusOutlineDefault provides false) {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    Column {
                        Text("Hello, World!")
                        Row {
                            WhiteButton("Hello in White") {
                            }
                            BlackButton("Hello in Black") {
                            }
                        }
                        var checked by remember { mutableStateOf(false) }
                        SwitchPreferenceWidget(
                            title = "hello", subtitle = "world", icon = painterResource(
                                Res.drawable.preview
                            ),
                            checked = checked,
                            onCheckedChanged = { checked = !checked }
                        )
                    }
                }
            }

        }
    }
}