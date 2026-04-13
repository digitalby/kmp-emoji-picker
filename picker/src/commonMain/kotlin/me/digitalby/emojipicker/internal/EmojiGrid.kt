package me.digitalby.emojipicker.internal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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
    haptics: Haptics = rememberPlatformHaptics(),
) {
    if (emojis.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth().padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
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
        modifier = modifier.fillMaxWidth().focusGroup(),
        contentPadding = PaddingValues(6.dp),
    ) {
        items(items = emojis, key = { it.details.string }) { baseEmoji ->
            val resolved = remember(baseEmoji, state.preferredSkinTone) {
                state.resolveWithSkinTone(baseEmoji)
            }
            val supportsTones = baseEmoji.supportsSkinTone()
            EmojiCell(
                displayed = resolved,
                supportsTones = supportsTones,
                onTap = { onEmojiSelected(resolved) },
                onLongPress = {
                    if (supportsTones) {
                        haptics.performLongPress()
                        toneFor = baseEmoji
                    }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EmojiCell(
    displayed: Emoji,
    supportsTones: Boolean,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val description = if (supportsTones) {
        "${displayed.details.description}, has skin tone variants"
    } else {
        displayed.details.description
    }
    val primary = MaterialTheme.colorScheme.primary
    val indicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
            )
            .onPreviewKeyEvent { event ->
                val isAltEnter = supportsTones &&
                    event.type == KeyEventType.KeyDown &&
                    (event.key == Key.Enter || event.key == Key.NumPadEnter) &&
                    event.isAltPressed
                if (isAltEnter) {
                    onLongPress()
                    true
                } else {
                    false
                }
            }
            .semantics {
                this.contentDescription = description
                this.role = Role.Button
            }
            .combinedClickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onTap,
                onLongClick = if (supportsTones) onLongPress else null,
                onClickLabel = "Insert emoji",
                onLongClickLabel = if (supportsTones) "Choose skin tone" else null,
            ),
        contentAlignment = Alignment.Center,
    ) {
        TextWithPlatformEmoji(text = displayed.details.string, fontSize = 24.sp)
        if (supportsTones) {
            Canvas(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 4.dp, bottom = 4.dp)
                    .size(6.dp),
            ) {
                val path = Path().apply {
                    moveTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(path = path, color = indicatorColor)
            }
        }
    }
}
