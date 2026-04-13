package me.digitalby.emojipicker.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import me.digitalby.emojipicker.EmojiPickerState
import org.kodein.emoji.Emoji
import org.kodein.emoji.SkinTone
import org.kodein.emoji.SkinTone1Emoji
import org.kodein.emoji.SkinTone2Emoji
import org.kodein.emoji.compose.m3.TextWithPlatformEmoji

internal fun Emoji.supportsSkinTone(): Boolean = this is SkinTone1Emoji || this is SkinTone2Emoji

internal fun Emoji.applyTone(tone: SkinTone): Emoji = when (this) {
    is SkinTone2Emoji -> withSkinTone(tone, tone)
    is SkinTone1Emoji -> withSkinTone(tone)
    else -> this
}

@Composable
internal fun SkinTonePopup(
    base: Emoji,
    state: EmojiPickerState,
    onDismissRequest: () -> Unit,
    onToneChosen: (Emoji) -> Unit,
) {
    Popup(
        alignment = Alignment.Center,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 6.dp,
            shadowElevation = 6.dp,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ToneCell(base, null) {
                    state.preferredSkinTone = null
                    onToneChosen(base)
                }
                SkinTone.entries.forEach { tone ->
                    val toned = base.applyTone(tone)
                    ToneCell(toned, tone) {
                        state.preferredSkinTone = tone
                        onToneChosen(toned)
                    }
                }
            }
        }
    }
}

private fun SkinTone.label(): String = when (this) {
    SkinTone.Light -> "light"
    SkinTone.MediumLight -> "medium light"
    SkinTone.Medium -> "medium"
    SkinTone.MediumDark -> "medium dark"
    SkinTone.Dark -> "dark"
}

@Composable
private fun ToneCell(emoji: Emoji, tone: SkinTone?, onClick: () -> Unit) {
    val description = when (tone) {
        null -> "${emoji.details.description}, default"
        else -> "${emoji.details.description}, ${tone.label()} skin tone"
    }
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (tone == null) {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                },
            )
            .semantics {
                this.role = Role.Button
                this.contentDescription = description
            }
            .clickable(onClickLabel = "Apply skin tone", onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        TextWithPlatformEmoji(text = emoji.details.string, fontSize = 22.sp)
    }
}
