package me.digitalby.emojipicker.android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.Test
import me.digitalby.emojipicker.EmojiPickerState
import me.digitalby.emojipicker.RECENT_CATEGORY_ID
import me.digitalby.emojipicker.RecentEmojiStore
import me.digitalby.emojipicker.internal.SearchField
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Smoke coverage for the Android Compose rendering path.
 *
 * These intentionally avoid mounting the full [me.digitalby.emojipicker.EmojiPicker]
 * composable because the live picker now pulls in `compose.resources` for emoji
 * localization, which requires an initialized Android asset context that Robolectric
 * does not provide by default. Instead we exercise isolated internal composables and
 * the state holder — enough to catch Android-specific regressions in the Compose
 * layer without dragging in the resource loader.
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class EmojiPickerAndroidSmokeTest {
    @Test
    fun trivialComposableRendersUnderAndroidRuntime() = runComposeUiTest {
        setContent {
            MaterialTheme {
                Box(Modifier.size(200.dp, 100.dp)) {
                    Text("kmp-emoji-picker")
                }
            }
        }
        waitForIdle()
        onNodeWithText("kmp-emoji-picker").assertExists()
    }

    @Test
    fun searchFieldRendersUnderAndroidRuntime() = runComposeUiTest {
        setContent {
            MaterialTheme {
                Box(Modifier.size(320.dp, 80.dp)) {
                    SearchField(query = "", onQueryChange = {})
                }
            }
        }
        waitForIdle()
        onNodeWithText("Search emoji").assertExists()
    }

    @Test
    fun stateHolderMutationsPersistUnderAndroidRuntime() {
        val state = EmojiPickerState(
            recentStore = RecentEmojiStore.inMemory(),
            initialCategory = RECENT_CATEGORY_ID,
            initialQuery = "",
            initialSkinTone = null,
        )
        state.query = "smile"
        state.selectedCategory = "Smileys & Emotion"
        kotlin.test.assertEquals("smile", state.query)
        kotlin.test.assertEquals("Smileys & Emotion", state.selectedCategory)
    }
}
