package me.digitalby.emojipicker.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import me.digitalby.emojipicker.EmojiPickerState
import me.digitalby.emojipicker.RECENT_CATEGORY_ID
import me.digitalby.emojipicker.RecentEmojiStore
import me.digitalby.emojipicker.ui.TestEmojis
import kotlin.test.Test
import kotlin.test.assertEquals

private class FakeHaptics : Haptics {
    var longPressCount: Int = 0
    override fun performLongPress() {
        longPressCount += 1
    }
}

@OptIn(ExperimentalTestApi::class)
class HapticsTest {
    private fun state(): EmojiPickerState = EmojiPickerState(
        recentStore = RecentEmojiStore.inMemory(),
        initialCategory = RECENT_CATEGORY_ID,
        initialQuery = "",
        initialSkinTone = null,
    )

    @Test
    fun longPressOnTonableEmojiTriggersHaptics() = runComposeUiTest {
        val target = TestEmojis.tonable1
        val fake = FakeHaptics()
        setContent {
            Box(Modifier.size(320.dp, 320.dp)) {
                EmojiGrid(
                    emojis = listOf(target),
                    state = state(),
                    columns = 1,
                    onEmojiSelected = {},
                    haptics = fake,
                )
            }
        }
        onNodeWithContentDescription(
            label = target.details.description,
            substring = true,
        ).performTouchInput { longClick() }
        assertEquals(1, fake.longPressCount)
    }

    @Test
    fun longPressOnPlainEmojiDoesNotTriggerHaptics() = runComposeUiTest {
        val target = TestEmojis.plain
        val fake = FakeHaptics()
        setContent {
            Box(Modifier.size(320.dp, 320.dp)) {
                EmojiGrid(
                    emojis = listOf(target),
                    state = state(),
                    columns = 1,
                    onEmojiSelected = {},
                    haptics = fake,
                )
            }
        }
        onNodeWithContentDescription(
            label = target.details.description,
            substring = true,
        ).performTouchInput { longClick() }
        assertEquals(0, fake.longPressCount)
    }
}
