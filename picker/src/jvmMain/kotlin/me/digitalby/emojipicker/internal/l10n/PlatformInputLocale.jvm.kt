package me.digitalby.emojipicker.internal.l10n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.im.InputContext
import java.util.Locale

@Composable
internal actual fun rememberCurrentInputLocale(): String = remember {
    // Escape hatch for deterministic tests / screenshot reproducibility.
    System.getProperty("emojipicker.forceLocale")?.takeIf { it.isNotBlank() }?.let { return@remember it }
    val imeLocale = runCatching {
        InputContext.getInstance()?.locale
    }.getOrNull()
    val effective = imeLocale ?: Locale.getDefault()
    effective.toLanguageTag()
}
