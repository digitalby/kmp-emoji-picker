package me.digitalby.emojipicker.internal

import me.digitalby.emojipicker.RECENT_CATEGORY_ID
import org.kodein.emoji.Emoji
import org.kodein.emoji.allGroups
import org.kodein.emoji.allOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EmojiCatalogHelpersTest {
    private val realGroups: List<String> by lazy {
        Emoji.allGroups().filter { it != "Component" }.take(2)
    }
    private val firstGroup: String get() = realGroups[0]
    private val secondGroup: String get() = realGroups[1]
    private val catalog: EmojiCatalog by lazy {
        EmojiCatalog(
            groups = realGroups,
            emojisByGroup = mapOf(
                firstGroup to Emoji.allOf(firstGroup).take(8),
                secondGroup to Emoji.allOf(secondGroup).take(4),
            ),
        )
    }
    private val oneEmoji: Emoji by lazy { Emoji.allOf(firstGroup).first() }

    @Test
    fun buildCategoriesPrependsRecentWhenShown() {
        val result = buildCategories(
            groups = catalog.groups,
            showRecent = true,
            hasRecent = true,
            recentLabel = "Recent",
        )
        assertEquals(3, result.size)
        assertEquals(RECENT_CATEGORY_ID, result[0].id)
        assertEquals("Recent", result[0].label)
        assertEquals(firstGroup, result[1].id)
        assertEquals(secondGroup, result[2].id)
    }

    @Test
    fun buildCategoriesOmitsRecentWhenShowRecentFalse() {
        val result = buildCategories(
            groups = catalog.groups,
            showRecent = false,
            hasRecent = true,
            recentLabel = "Recent",
        )
        assertEquals(2, result.size)
        assertTrue(result.none { it.id == RECENT_CATEGORY_ID })
    }

    @Test
    fun buildCategoriesOmitsRecentWhenNoRecentItems() {
        val result = buildCategories(
            groups = catalog.groups,
            showRecent = true,
            hasRecent = false,
            recentLabel = "Recent",
        )
        assertEquals(2, result.size)
        assertTrue(result.none { it.id == RECENT_CATEGORY_ID })
    }

    @Test
    fun buildCategoriesPreservesGroupOrder() {
        val result = buildCategories(
            groups = listOf("A", "B", "C"),
            showRecent = false,
            hasRecent = false,
            recentLabel = "Recent",
        )
        assertEquals(listOf("A", "B", "C"), result.map { it.id })
    }

    @Test
    fun buildCategoriesEmptyGroupsReturnsEmpty() {
        val result = buildCategories(
            groups = emptyList(),
            showRecent = true,
            hasRecent = false,
            recentLabel = "Recent",
        )
        assertTrue(result.isEmpty())
    }

    @Test
    fun selectSourceEmojisReturnsRecentForRecentCategory() {
        val recent = listOf(oneEmoji)
        val result = selectSourceEmojis(catalog, recent, RECENT_CATEGORY_ID)
        assertEquals(recent, result)
    }

    @Test
    fun selectSourceEmojisReturnsGroupEmojis() {
        val result = selectSourceEmojis(catalog, emptyList(), firstGroup)
        assertEquals(catalog.emojisByGroup.getValue(firstGroup), result)
    }

    @Test
    fun selectSourceEmojisReturnsEmptyForUnknownCategory() {
        val result = selectSourceEmojis(catalog, emptyList(), "Not A Category")
        assertTrue(result.isEmpty())
    }
}
