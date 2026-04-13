package me.digitalby.emojipicker.internal.l10n

import kotlin.test.Test
import kotlin.test.assertEquals

class EmojiL10nResolveLocaleTest {
    @Test
    fun exactTagsResolve() {
        assertEquals("en", EmojiL10n.resolveLocale("en"))
        assertEquals("ru", EmojiL10n.resolveLocale("ru"))
        assertEquals("uk", EmojiL10n.resolveLocale("uk"))
        assertEquals("be", EmojiL10n.resolveLocale("be"))
    }

    @Test
    fun regionFallsBackToPrimary() {
        assertEquals("ru", EmojiL10n.resolveLocale("ru-RU"))
        assertEquals("en", EmojiL10n.resolveLocale("en-US"))
        assertEquals("es", EmojiL10n.resolveLocale("es_419"))
        assertEquals("pt", EmojiL10n.resolveLocale("pt_BR"))
    }

    @Test
    fun chineseSimplifiedVsTraditional() {
        assertEquals("zh", EmojiL10n.resolveLocale("zh"))
        assertEquals("zh", EmojiL10n.resolveLocale("zh-CN"))
        assertEquals("zh", EmojiL10n.resolveLocale("zh-Hans"))
        assertEquals("zh-hant", EmojiL10n.resolveLocale("zh-Hant"))
        assertEquals("zh-hant", EmojiL10n.resolveLocale("zh-TW"))
        assertEquals("zh-hant", EmojiL10n.resolveLocale("zh-HK"))
    }

    @Test
    fun unsupportedFallsBackToEnglish() {
        assertEquals("en", EmojiL10n.resolveLocale("sw"))
        assertEquals("en", EmojiL10n.resolveLocale("th-TH"))
        assertEquals("en", EmojiL10n.resolveLocale(""))
    }
}
