package me.digitalby.emojipicker.internal.l10n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.im.InputContext
import java.util.Locale

@Composable
internal actual fun rememberCurrentInputLocale(): String = remember {
    val imeLocale = runCatching {
        InputContext.getInstance()?.locale
    }.getOrNull()
    val effective = imeLocale ?: Locale.getDefault()
    effective.toLanguageTag()
}
