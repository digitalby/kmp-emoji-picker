package me.digitalby.emojipicker.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

internal interface Haptics {
    fun performLongPress()
}

@Composable
internal fun rememberPlatformHaptics(): Haptics {
    val feedback = LocalHapticFeedback.current
    return remember(feedback) {
        object : Haptics {
            override fun performLongPress() {
                feedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }
}
