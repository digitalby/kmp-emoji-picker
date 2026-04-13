package me.digitalby.emojipicker.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.takahirom.roborazzi.captureRoboImage
import kotlinx.coroutines.runBlocking
import me.digitalby.emojipicker.EmojiPicker
import me.digitalby.emojipicker.EmojiPickerState
import me.digitalby.emojipicker.RECENT_CATEGORY_ID
import me.digitalby.emojipicker.RecentEmojiStore
import org.kodein.emoji.compose.EmojiService
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class PickerScreenshotTest {
    private val snapshotDir = "src/jvmTest/snapshots"

    private fun state(
        category: String = RECENT_CATEGORY_ID,
        query: String = "",
    ): EmojiPickerState = EmojiPickerState(
        recentStore = RecentEmojiStore.inMemory(),
        initialCategory = category,
        initialQuery = query,
        initialSkinTone = null,
    )

    init {
        // Warm the emoji service before any screenshot test runs so we don't
        // screenshot an empty grid while loading.
        runBlocking { EmojiService.await() }
    }

    @Test
    fun emptySearchState() = runComposeUiTest {
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(
                    modifier = Modifier
                        .size(360.dp, 420.dp)
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    EmojiPicker(
                        onEmojiSelected = {},
                        state = state(category = "Smileys & Emotion", query = "zzzzzzzz"),
                    )
                }
            }
        }
        waitForIdle()
        onRoot().captureRoboImage("$snapshotDir/empty_state.png")
    }

    @Test
    fun idleSmileysGrid() = runComposeUiTest {
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(
                    modifier = Modifier
                        .size(360.dp, 480.dp)
                        .background(Color.White),
                ) {
                    EmojiPicker(
                        onEmojiSelected = {},
                        state = state(category = "Smileys & Emotion"),
                    )
                }
            }
        }
        waitForIdle()
        onRoot().captureRoboImage("$snapshotDir/idle_smileys.png")
    }

    @Test
    fun searchActiveFiltered() = runComposeUiTest {
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(
                    modifier = Modifier
                        .size(360.dp, 480.dp)
                        .background(Color.White),
                ) {
                    EmojiPicker(
                        onEmojiSelected = {},
                        state = state(category = "Smileys & Emotion", query = "smile"),
                    )
                }
            }
        }
        waitForIdle()
        onRoot().captureRoboImage("$snapshotDir/search_active.png")
    }

    @Test
    fun darkTheme() = runComposeUiTest {
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .size(360.dp, 480.dp),
                ) {
                    EmojiPicker(
                        onEmojiSelected = {},
                        state = state(category = "Smileys & Emotion"),
                    )
                }
            }
        }
        waitForIdle()
        onRoot().captureRoboImage("$snapshotDir/dark_theme.png")
    }
}
