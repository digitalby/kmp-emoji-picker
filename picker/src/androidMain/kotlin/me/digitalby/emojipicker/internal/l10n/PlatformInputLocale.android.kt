package me.digitalby.emojipicker.internal.l10n

import android.content.Context
import android.os.Build
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
internal actual fun rememberCurrentInputLocale(): String {
    val context = LocalContext.current
    var locale by remember { mutableStateOf(readInputLocale(context)) }
    DisposableEffect(context) {
        // Re-read once after composition settles so the first emission also
        // reflects whatever IME subtype is active at the moment the picker opens.
        locale = readInputLocale(context)
        onDispose {}
    }
    return locale
}

private fun readInputLocale(context: Context): String {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    if (imm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val subtype = imm.currentInputMethodSubtype
        val tag = subtype?.languageTag
        if (!tag.isNullOrBlank()) return tag
        @Suppress("DEPRECATION")
        val legacy = subtype?.locale
        if (!legacy.isNullOrBlank()) return legacy.replace('_', '-')
    }
    return Locale.getDefault().toLanguageTag()
}
