package me.digitalby.emojipicker.internal.l10n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.window

@Composable
internal actual fun rememberCurrentInputLocale(): String = remember {
    val nav = window.navigator.language
    nav.ifBlank { "en" }
}
