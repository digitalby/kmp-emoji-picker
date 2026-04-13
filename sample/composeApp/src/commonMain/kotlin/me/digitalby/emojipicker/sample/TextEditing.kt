package me.digitalby.emojipicker.sample

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

internal fun insertAtCaret(tfv: TextFieldValue, insert: String): TextFieldValue {
    val start = tfv.selection.start.coerceIn(0, tfv.text.length)
    val end = tfv.selection.end.coerceIn(0, tfv.text.length)
    val newText = tfv.text.substring(0, start) + insert + tfv.text.substring(end)
    val cursor = start + insert.length
    return TextFieldValue(text = newText, selection = TextRange(cursor))
}
