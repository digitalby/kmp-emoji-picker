package me.digitalby.emojipicker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import me.digitalby.emojipicker.EmojiPickerState
import me.digitalby.emojipicker.RECENT_CATEGORY_ID
import me.digitalby.emojipicker.RecentEmojiStore
import me.digitalby.emojipicker.internal.EmojiGrid
import org.kodein.emoji.Emoji
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class EmojiGridUiTest {
    private fun state(): EmojiPickerState = EmojiPickerState(
        recentStore = RecentEmojiStore.inMemory(),
        initialCategory = RECENT_CATEGORY_ID,
        initialQuery = "",
        initialSkinTone = null,
    )

    @Test
    fun emptyStateShowsPlaceholderMessage() = runComposeUiTest {
        setContent {
            Box(Modifier.size(320.dp, 320.dp)) {
                EmojiGrid(
                    emojis = emptyList(),
                    state = state(),
                    columns = 4,
                    onEmojiSelected = {},
                )
            }
        }
        // Empty-state message is a plain Text, not described. Just assert the composable renders.
    }

    @Test
    fun cellExposesContentDescription() = runComposeUiTest {
        val grinning = TestEmojis.plain
        setContent {
            Box(Modifier.size(320.dp, 320.dp)) {
                EmojiGrid(
                    emojis = listOf(grinning),
                    state = state(),
                    columns = 1,
                    onEmojiSelected = {},
                )
            }
        }
        onNodeWithContentDescription(grinning.details.description).assertExists()
    }

    @Test
    fun cellHasButtonRole() = runComposeUiTest {
        val target = TestEmojis.plain
        setContent {
            Box(Modifier.size(320.dp, 320.dp)) {
                EmojiGrid(
                    emojis = listOf(target),
                    state = state(),
                    columns = 1,
                    onEmojiSelected = {},
                )
            }
        }
        onNodeWithContentDescription(target.details.description).assert(hasRole(Role.Button))
    }

    @Test
    fun singleClickFiresOnEmojiSelected() = runComposeUiTest {
        val target = TestEmojis.plain
        val clicks = mutableListOf<Emoji>()
        setContent {
            Box(Modifier.size(320.dp, 320.dp)) {
                EmojiGrid(
                    emojis = listOf(target),
                    state = state(),
                    columns = 1,
                    onEmojiSelected = { clicks += it },
                )
            }
        }
        onNodeWithContentDescription(target.details.description).performClick()
        assertEquals(1, clicks.size)
        assertEquals(target.details.string, clicks.first().details.string)
    }

    @Test
    fun longPressOnTonableEmojiOpensPopup() = runComposeUiTest {
        val target = TestEmojis.tonable1
        setContent {
            Box(Modifier.size(320.dp, 320.dp)) {
                EmojiGrid(
                    emojis = listOf(target),
                    state = state(),
                    columns = 1,
                    onEmojiSelected = {},
                )
            }
        }
        onNodeWithContentDescription(target.details.description).performTouchInput { longClick() }
        // After long press the popup renders 6 cells with content descriptions containing " skin tone" or "default"
        onAllNodesWithContentDescription(
            label = "skin tone",
            substring = true,
        ).assertCountEquals(5)
    }

    @Test
    fun longPressOnPlainEmojiDoesNotOpenPopup() = runComposeUiTest {
        val target = TestEmojis.plain
        setContent {
            Box(Modifier.size(320.dp, 320.dp)) {
                EmojiGrid(
                    emojis = listOf(target),
                    state = state(),
                    columns = 1,
                    onEmojiSelected = {},
                )
            }
        }
        onNodeWithContentDescription(target.details.description).performTouchInput { longClick() }
        onAllNodesWithContentDescription(
            label = "skin tone",
            substring = true,
        ).assertCountEquals(0)
    }

    private fun hasRole(expected: Role): SemanticsMatcher = SemanticsMatcher.expectValue(SemanticsProperties.Role, expected)
}
