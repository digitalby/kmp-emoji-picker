package me.digitalby.emojipicker.internal.l10n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.UIKit.UITextInputMode

@Composable
internal actual fun rememberCurrentInputLocale(): String = remember {
    val activeMode = UITextInputMode.activeInputModes.firstOrNull() as? UITextInputMode
    val imeLanguage = activeMode?.primaryLanguage
    if (!imeLanguage.isNullOrBlank()) {
        imeLanguage
    } else {
        NSLocale.currentLocale.languageCode
    }
}
