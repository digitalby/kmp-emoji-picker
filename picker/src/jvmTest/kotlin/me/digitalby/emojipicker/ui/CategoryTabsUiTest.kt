package me.digitalby.emojipicker.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import me.digitalby.emojipicker.internal.CategoryEntry
import me.digitalby.emojipicker.internal.CategoryTabs
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class CategoryTabsUiTest {
    private val entries = listOf(
        CategoryEntry("smileys", "Smileys"),
        CategoryEntry("animals", "Animals"),
        CategoryEntry("food", "Food"),
    )

    @Test
    fun selectedChipReportsSelectedSemantics() = runComposeUiTest {
        setContent {
            CategoryTabs(
                categories = entries,
                selected = "animals",
                onSelect = {},
            )
        }
        onNodeWithText("Animals").assertIsSelected()
    }

    @Test
    fun clickingUnselectedChipFiresCallback() = runComposeUiTest {
        val clicks = mutableListOf<String>()
        setContent {
            CategoryTabs(
                categories = entries,
                selected = "smileys",
                onSelect = { clicks += it },
            )
        }
        onNodeWithText("Food").performClick()
        assertEquals(listOf("food"), clicks)
    }

    @Test
    fun clickingAlreadySelectedChipStillFiresCallback() = runComposeUiTest {
        val clicks = mutableListOf<String>()
        setContent {
            CategoryTabs(
                categories = entries,
                selected = "smileys",
                onSelect = { clicks += it },
            )
        }
        onNodeWithText("Smileys").performClick()
        assertEquals(listOf("smileys"), clicks)
    }
}
