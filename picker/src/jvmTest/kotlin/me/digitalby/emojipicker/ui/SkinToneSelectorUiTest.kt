package me.digitalby.emojipicker.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import me.digitalby.emojipicker.EmojiPickerState
import me.digitalby.emojipicker.RECENT_CATEGORY_ID
import me.digitalby.emojipicker.RecentEmojiStore
import me.digitalby.emojipicker.internal.SkinTonePopup
import org.kodein.emoji.Emoji
import org.kodein.emoji.SkinTone
import org.kodein.emoji.Toned1Emoji
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalTestApi::class)
class SkinToneSelectorUiTest {
    private fun state(): EmojiPickerState = EmojiPickerState(
        recentStore = RecentEmojiStore.inMemory(),
        initialCategory = RECENT_CATEGORY_ID,
        initialQuery = "",
        initialSkinTone = null,
    )

    @Test
    fun popupRendersDefaultPlusFiveTones() = runComposeUiTest {
        val base = TestEmojis.tonable1
        setContent {
            SkinTonePopup(
                base = base,
                state = state(),
                onDismissRequest = {},
                onToneChosen = {},
            )
        }
        // Six clickable cells total: 1 "default" + 5 tones
        onAllNodesWithContentDescription(
            label = "skin tone",
            substring = true,
        ).assertCountEquals(5)
        onNodeWithContentDescription(
            label = "default",
            substring = true,
            ignoreCase = true,
        ).assertExists()
    }

    @Test
    fun eachToneCellIsClickable() = runComposeUiTest {
        val base = TestEmojis.tonable1
        setContent {
            SkinTonePopup(
                base = base,
                state = state(),
                onDismissRequest = {},
                onToneChosen = {},
            )
        }
        val clickable = onAllNodes(hasClickAction())
        clickable.assertCountEquals(6)
    }

    @Test
    fun clickingLightToneFiresToned1() = runComposeUiTest {
        val base = TestEmojis.tonable1
        var chosen: Emoji? = null
        setContent {
            SkinTonePopup(
                base = base,
                state = state(),
                onDismissRequest = {},
                onToneChosen = { chosen = it },
            )
        }
        onNodeWithContentDescription(
            label = ", light skin tone",
            substring = true,
            ignoreCase = true,
        ).performClick()
        assertNotNull(chosen)
        val toned = chosen
        check(toned is Toned1Emoji) { "expected Toned1Emoji, got ${toned?.let { it::class.simpleName }}" }
        assertEquals(SkinTone.Light, toned.tone1)
    }

    @Test
    fun clickingDefaultFiresBaseAndClearsPreferredTone() = runComposeUiTest {
        val base = TestEmojis.tonable1
        val st = state()
        st.preferredSkinTone = SkinTone.Dark
        var chosen: Emoji? = null
        setContent {
            SkinTonePopup(
                base = base,
                state = st,
                onDismissRequest = {},
                onToneChosen = { chosen = it },
            )
        }
        onNodeWithContentDescription(
            label = "default",
            substring = true,
            ignoreCase = true,
        ).performClick()
        assertEquals(base.details.string, chosen?.details?.string)
        assertNull(st.preferredSkinTone)
    }

    @Test
    fun clickingToneSetsPreferredOnState() = runComposeUiTest {
        val base = TestEmojis.tonable1
        val st = state()
        setContent {
            SkinTonePopup(
                base = base,
                state = st,
                onDismissRequest = {},
                onToneChosen = {},
            )
        }
        onNodeWithContentDescription(
            label = "medium dark skin tone",
            substring = true,
            ignoreCase = true,
        ).performClick()
        assertEquals(SkinTone.MediumDark, st.preferredSkinTone)
    }
}
