package me.digitalby.emojipicker.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.digitalby.emojipicker.EmojiPickerState
import org.kodein.emoji.Emoji
import org.kodein.emoji.compose.m3.TextWithPlatformEmoji

@Composable
internal fun EmojiGrid(
    emojis: List<Emoji>,
    state: EmojiPickerState,
    columns: Int,
    onEmojiSelected: (Emoji) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (emojis.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "No emoji found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }
    var toneFor by remember { mutableStateOf<Emoji?>(null) }
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(6.dp),
    ) {
        items(items = emojis, key = { it.details.string }) { baseEmoji ->
            val resolved = remember(baseEmoji, state.preferredSkinTone) {
                state.resolveWithSkinTone(baseEmoji)
            }
            EmojiCell(
                displayed = resolved,
                onTap = { onEmojiSelected(resolved) },
                onLongPress = {
                    if (baseEmoji.supportsSkinTone()) toneFor = baseEmoji
                },
            )
        }
    }
    toneFor?.let { base ->
        SkinTonePopup(
            base = base,
            state = state,
            onDismissRequest = { toneFor = null },
            onToneChosen = { chosen ->
                toneFor = null
                onEmojiSelected(chosen)
            },
        )
    }
}

@Composable
private fun EmojiCell(
    displayed: Emoji,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(displayed.details.string) {
                detectTapGestures(
                    onTap = { onTap() },
                    onLongPress = { onLongPress() },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        TextWithPlatformEmoji(text = displayed.details.string, fontSize = 24.sp)
    }
}
