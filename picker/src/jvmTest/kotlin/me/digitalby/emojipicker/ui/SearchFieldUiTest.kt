package me.digitalby.emojipicker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import me.digitalby.emojipicker.internal.SearchField
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class SearchFieldUiTest {
    @Test
    fun clearButtonHiddenWhenQueryEmpty() = runComposeUiTest {
        setContent {
            SearchField(query = "", onQueryChange = {})
        }
        onAllNodesWithContentDescription(label = "Clear").assertCountEquals(0)
    }

    @Test
    fun clearButtonShownWhenQueryNotEmpty() = runComposeUiTest {
        setContent {
            SearchField(query = "smile", onQueryChange = {})
        }
        onNodeWithContentDescription("Clear").assertExists()
    }

    @Test
    fun typingFiresOnQueryChange() = runComposeUiTest {
        val received = mutableListOf<String>()
        setContent {
            var query by remember { mutableStateOf("") }
            SearchField(
                query = query,
                onQueryChange = {
                    query = it
                    received += it
                },
            )
        }
        onAllNodes(hasSetTextAction()).onFirst().performTextInput("hi")
        assertEquals(listOf("hi"), received)
    }

    @Test
    fun clickingClearFiresEmptyString() = runComposeUiTest {
        val received = mutableListOf<String>()
        setContent {
            SearchField(
                query = "smile",
                onQueryChange = { received += it },
            )
        }
        onNodeWithContentDescription("Clear").performClick()
        assertEquals(listOf(""), received)
    }
}
