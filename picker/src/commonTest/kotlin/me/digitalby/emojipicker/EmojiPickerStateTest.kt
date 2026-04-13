package me.digitalby.emojipicker

import org.kodein.emoji.Emoji
import org.kodein.emoji.SkinTone
import org.kodein.emoji.SkinTone1Emoji
import org.kodein.emoji.SkinTone2Emoji
import org.kodein.emoji.Toned1Emoji
import org.kodein.emoji.Toned2Emoji
import org.kodein.emoji.allGroups
import org.kodein.emoji.allOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class EmojiPickerStateTest {
    private val all: List<Emoji> by lazy {
        Emoji.allGroups().flatMap { Emoji.allOf(it) }
    }
    private val plain: Emoji by lazy {
        all.first { it !is SkinTone1Emoji && it !is SkinTone2Emoji }
    }
    private val tone1: SkinTone1Emoji by lazy {
        all.first { it is SkinTone1Emoji && it !is SkinTone2Emoji } as SkinTone1Emoji
    }
    private val tone2: SkinTone2Emoji by lazy {
        all.first { it is SkinTone2Emoji } as SkinTone2Emoji
    }

    private fun newState(
        category: String = RECENT_CATEGORY_ID,
        query: String = "",
        tone: SkinTone? = null,
    ) = EmojiPickerState(
        recentStore = RecentEmojiStore.inMemory(),
        initialCategory = category,
        initialQuery = query,
        initialSkinTone = tone,
    )

    @Test
    fun resolveReturnsBaseWhenNoPreferredTone() {
        val state = newState()
        assertSame(tone1, state.resolveWithSkinTone(tone1))
        assertSame(plain, state.resolveWithSkinTone(plain))
    }

    @Test
    fun resolveAppliesToneToSkinTone1Emoji() {
        val state = newState(tone = SkinTone.Medium)
        val resolved = state.resolveWithSkinTone(tone1)
        assertTrue(resolved is Toned1Emoji, "expected Toned1Emoji, got ${resolved::class.simpleName}")
        assertEquals(SkinTone.Medium, resolved.tone1)
    }

    @Test
    fun resolveAppliesDoubleToneToSkinTone2Emoji() {
        val state = newState(tone = SkinTone.Dark)
        val resolved = state.resolveWithSkinTone(tone2)
        assertTrue(resolved is Toned2Emoji, "expected Toned2Emoji, got ${resolved::class.simpleName}")
        assertEquals(SkinTone.Dark, resolved.tone1)
        assertEquals(SkinTone.Dark, resolved.tone2)
    }

    @Test
    fun resolveReturnsBaseForNonToneableEmoji() {
        val state = newState(tone = SkinTone.Light)
        assertSame(plain, state.resolveWithSkinTone(plain))
    }

    @Test
    fun mutableFieldsPersist() {
        val state = newState()
        state.query = "smile"
        state.selectedCategory = "Smileys & Emotion"
        state.preferredSkinTone = SkinTone.MediumLight
        assertEquals("smile", state.query)
        assertEquals("Smileys & Emotion", state.selectedCategory)
        assertEquals(SkinTone.MediumLight, state.preferredSkinTone)
    }

    @Test
    fun initialQueryAndCategoryRespected() {
        val state = newState(category = "Activities", query = "trophy", tone = SkinTone.Dark)
        assertEquals("Activities", state.selectedCategory)
        assertEquals("trophy", state.query)
        assertEquals(SkinTone.Dark, state.preferredSkinTone)
    }

    @Test
    fun recentCategoryIdIsStable() {
        assertEquals("__recent__", RECENT_CATEGORY_ID)
        assertFalse(RECENT_CATEGORY_ID in Emoji.allGroups())
    }

    @Test
    fun preferredSkinToneCanBeCleared() {
        val state = newState(tone = SkinTone.Dark)
        state.preferredSkinTone = null
        assertNull(state.preferredSkinTone)
    }
}
