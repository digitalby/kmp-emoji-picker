package me.digitalby.emojipicker.sample

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlin.test.Test
import kotlin.test.assertEquals

class TextEditingTest {
    @Test
    fun insertIntoEmptyPlacesCaretAfterInsertion() {
        val result = insertAtCaret(TextFieldValue(""), "😀")
        assertEquals("😀", result.text)
        assertEquals(TextRange("😀".length), result.selection)
    }

    @Test
    fun insertAtMiddleCursor() {
        val source = TextFieldValue(text = "hello world", selection = TextRange(5))
        val result = insertAtCaret(source, " 👋")
        assertEquals("hello 👋 world", result.text)
        assertEquals(TextRange(5 + " 👋".length), result.selection)
    }

    @Test
    fun insertReplacesSelection() {
        val source = TextFieldValue(text = "hello world", selection = TextRange(0, 5))
        val result = insertAtCaret(source, "hi")
        assertEquals("hi world", result.text)
        assertEquals(TextRange(2), result.selection)
    }

    @Test
    fun insertAtEnd() {
        val source = TextFieldValue(text = "hi", selection = TextRange(2))
        val result = insertAtCaret(source, "!")
        assertEquals("hi!", result.text)
        assertEquals(TextRange(3), result.selection)
    }

    @Test
    fun insertWithOutOfBoundsSelectionCoerces() {
        val source = TextFieldValue(text = "ab", selection = TextRange(99, 99))
        val result = insertAtCaret(source, "c")
        assertEquals("abc", result.text)
        assertEquals(TextRange(3), result.selection)
    }
}
