package com.stleo.ui.compose
import androidx.compose.runtime.Composable

// https://github.com/adrielcafe/voyager
interface Screen {
    @Composable
    fun Content()
}