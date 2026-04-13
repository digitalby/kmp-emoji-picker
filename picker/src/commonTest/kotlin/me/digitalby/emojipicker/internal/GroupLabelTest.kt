package me.digitalby.emojipicker.internal

import kotlin.test.Test
import kotlin.test.assertEquals

class GroupLabelTest {
    @Test
    fun humanizesKnownUnicodeGroupIds() {
        assertEquals("Smileys & Emotion", humanizeGroupId("smileys_emotion"))
        assertEquals("People & Body", humanizeGroupId("people_body"))
        assertEquals("Animals & Nature", humanizeGroupId("animals_nature"))
        assertEquals("Food & Drink", humanizeGroupId("food_drink"))
        assertEquals("Travel & Places", humanizeGroupId("travel_places"))
        assertEquals("Activities", humanizeGroupId("activities"))
        assertEquals("Objects", humanizeGroupId("objects"))
        assertEquals("Symbols", humanizeGroupId("symbols"))
        assertEquals("Flags", humanizeGroupId("flags"))
    }

    @Test
    fun resolveUsesLocalizedMapWhenPresent() {
        val localized = mapOf("smileys_emotion" to "Смайлики", "flags" to "Флаги")
        assertEquals("Смайлики", resolveGroupLabel("smileys_emotion", localized))
        assertEquals("Флаги", resolveGroupLabel("flags", localized))
    }

    @Test
    fun resolveFallsBackToHumanizerForMissingKey() {
        val localized = mapOf("flags" to "Флаги")
        assertEquals("Animals & Nature", resolveGroupLabel("animals_nature", localized))
    }

    @Test
    fun resolveFallsBackToHumanizerForNullMap() {
        assertEquals("Objects", resolveGroupLabel("objects", null))
    }
}
