package me.digitalby.emojipicker.sample

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication

fun main() = singleWindowApplication(
    title = "kmp-emoji-picker sample",
    state = WindowState(width = 480.dp, height = 720.dp),
) { App() }
