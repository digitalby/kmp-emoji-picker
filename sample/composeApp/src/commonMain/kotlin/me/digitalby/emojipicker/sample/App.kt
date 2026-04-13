package me.digitalby.emojipicker.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.digitalby.emojipicker.EmojiPicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme {
        var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }
        var showSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = { Text("kmp-emoji-picker sample") })
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Say something") },
                    trailingIcon = {
                        IconButton(onClick = { showSheet = true }) {
                            Icon(Icons.Default.TagFaces, contentDescription = "Insert emoji")
                        }
                    },
                )
                Text(
                    text = "Length: ${text.text.length}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            if (showSheet) {
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = { showSheet = false },
                ) {
                    EmojiPicker(
                        onEmojiSelected = { emoji ->
                            text = insertAtCaret(text, emoji.details.string)
                            scope.launch {
                                sheetState.hide()
                                showSheet = false
                            }
                        },
                        modifier = Modifier.navigationBarsPadding(),
                    )
                }
            }
        }
    }
}

private fun insertAtCaret(tfv: TextFieldValue, insert: String): TextFieldValue {
    val start = tfv.selection.start.coerceIn(0, tfv.text.length)
    val end = tfv.selection.end.coerceIn(0, tfv.text.length)
    val newText = tfv.text.substring(0, start) + insert + tfv.text.substring(end)
    val cursor = start + insert.length
    return TextFieldValue(text = newText, selection = TextRange(cursor))
}
