package me.digitalby.emojipicker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import me.digitalby.emojipicker.EmojiPicker
import me.digitalby.emojipicker.EmojiPickerState
import me.digitalby.emojipicker.RECENT_CATEGORY_ID
import me.digitalby.emojipicker.RecentEmojiStore
import org.kodein.emoji.Emoji
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class EmojiPickerFlowUiTest {
    private fun state(initialCategory: String = RECENT_CATEGORY_ID): Pair<EmojiPickerState, RecentEmojiStore> {
        val store = RecentEmojiStore.inMemory()
        val s = EmojiPickerState(
            recentStore = store,
            initialCategory = initialCategory,
            initialQuery = "",
            initialSkinTone = null,
        )
        return s to store
    }

    @Test
    fun initialLoadRendersEmojiCells() = runComposeUiTest {
        val (s, _) = state()
        setContent {
            Box(Modifier.size(420.dp, 640.dp)) {
                EmojiPicker(onEmojiSelected = {}, state = s)
            }
        }
        waitUntil(timeoutMillis = 15_000) { countCells() > 0 }
        assertTrue(countCells() > 0, "expected at least one rendered emoji cell after catalog load")
    }

    @Test
    fun typingQueryFiltersCellsDown() = runComposeUiTest {
        val (s, _) = state(initialCategory = "Smileys & Emotion")
        setContent {
            Box(Modifier.size(420.dp, 640.dp)) {
                EmojiPicker(onEmojiSelected = {}, state = s)
            }
        }
        waitUntil(timeoutMillis = 10_000) {
            countCells() > 0
        }
        val before = countCells()
        onAllNodes(hasSetTextAction()).onFirst().performTextInput("grinning")
        waitUntil(timeoutMillis = 5_000) {
            countCells() < before
        }
        val after = countCells()
        assertTrue(after < before, "expected fewer cells after filtering: before=$before, after=$after")
        assertTrue(after > 0, "expected at least one match for 'grinning'")
    }

    @Test
    fun tappingCellFiresOnEmojiSelectedAndRecordsRecent() = runComposeUiTest {
        val (s, store) = state(initialCategory = "Smileys & Emotion")
        val chosen = mutableListOf<Emoji>()
        setContent {
            Box(Modifier.size(420.dp, 640.dp)) {
                EmojiPicker(
                    onEmojiSelected = { chosen += it },
                    state = s,
                )
            }
        }
        waitUntil(timeoutMillis = 10_000) { countCells() > 0 }
        val grinningDesc = "grinning face"
        onAllNodes(hasSetTextAction()).onFirst().performTextInput("grinning face")
        waitUntil(timeoutMillis = 5_000) {
            onAllNodesWithContentDescription(label = grinningDesc, substring = false)
                .fetchSemanticsNodes().isNotEmpty()
        }
        onAllNodesWithContentDescription(label = grinningDesc, substring = false)
            .onFirst().performClick()
        waitUntil(timeoutMillis = 2_000) { chosen.isNotEmpty() }
        assertEquals(grinningDesc, chosen.first().details.description)
        waitUntil(timeoutMillis = 2_000) { store.recent.value.isNotEmpty() }
        assertEquals(grinningDesc, store.recent.value.first().details.description)
    }

    private fun androidx.compose.ui.test.ComposeUiTest.countCells(): Int = onAllNodesWithContentDescription(label = "face", substring = true, ignoreCase = true)
        .fetchSemanticsNodes().size
}
