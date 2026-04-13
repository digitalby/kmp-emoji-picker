package me.digitalby.emojipicker.internal

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
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SkinToneHelpersTest {
    private val all: List<Emoji> by lazy {
        Emoji.allGroups().flatMap { Emoji.allOf(it) }
    }
    private val plain: Emoji by lazy {
        all.first { it !is SkinTone1Emoji && it !is SkinTone2Emoji }
    }
    private val tone1: Emoji by lazy {
        all.first { it is SkinTone1Emoji && it !is SkinTone2Emoji }
    }
    private val tone2: Emoji by lazy {
        all.first { it is SkinTone2Emoji }
    }

    @Test
    fun supportsSkinToneTrueForSkinTone1Emoji() {
        assertTrue(tone1.supportsSkinTone())
    }

    @Test
    fun supportsSkinToneTrueForSkinTone2Emoji() {
        assertTrue(tone2.supportsSkinTone())
    }

    @Test
    fun supportsSkinToneFalseForPlainEmoji() {
        assertFalse(plain.supportsSkinTone())
    }

    @Test
    fun applyToneReturnsToned1ForSkinTone1Emoji() {
        val toned = tone1.applyTone(SkinTone.Medium)
        assertTrue(toned is Toned1Emoji)
        assertEquals(SkinTone.Medium, toned.tone1)
    }

    @Test
    fun applyToneReturnsToned2ForSkinTone2Emoji() {
        val toned = tone2.applyTone(SkinTone.Dark)
        assertTrue(toned is Toned2Emoji)
        assertEquals(SkinTone.Dark, toned.tone1)
        assertEquals(SkinTone.Dark, toned.tone2)
    }

    @Test
    fun applyToneReturnsSameInstanceForPlainEmoji() {
        assertSame(plain, plain.applyTone(SkinTone.Light))
    }

    @Test
    fun skinToneLabelCoversAllVariants() {
        val labels = SkinTone.entries.map { it.label() }
        assertEquals(SkinTone.entries.size, labels.toSet().size, "labels must be distinct")
        assertTrue(labels.all { it.isNotBlank() }, "labels must be non-blank")
        assertEquals("light", SkinTone.Light.label())
        assertEquals("medium light", SkinTone.MediumLight.label())
        assertEquals("medium", SkinTone.Medium.label())
        assertEquals("medium dark", SkinTone.MediumDark.label())
        assertEquals("dark", SkinTone.Dark.label())
    }
}
