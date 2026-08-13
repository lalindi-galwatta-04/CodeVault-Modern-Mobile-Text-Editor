package com.example.codevault.ui.editor

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun EditorScreen(
    fileName: String,
    editorValue: TextFieldValue,
    hasUnsavedChanges: Boolean,

    showSearchPanel: Boolean,
    searchText: String,
    replaceText: String,
    currentMatch: Int,
    totalMatches: Int,

    canUndo: Boolean,
    canRedo: Boolean,


    wordWrapEnabled: Boolean,
    isReadOnly: Boolean,

    onEditorValueChange: (TextFieldValue) -> Unit,

    onBack: () -> Unit,
    onSave: () -> Unit,
    onSaveAs: () -> Unit,

    onShowSearch: () -> Unit,

    onUndo: () -> Unit,
    onRedo: () -> Unit,

    onWordWrapChange: (Boolean) -> Unit,

    onToggleReadOnly: () -> Unit,

    // Version-control actions.
    onCreateVersion: () -> Unit,
    onShowVersionHistory: () -> Unit,

    onCloseSearch: () -> Unit,

    onSearchTextChange: (String) -> Unit,
    onReplaceTextChange: (String) -> Unit,

    onPreviousMatch: () -> Unit,
    onNextMatch: () -> Unit,
    onReplace: () -> Unit,
    onReplaceAll: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Button(
                onClick = onBack
            ) {
                Text("Back")
            }

            Button(
                onClick = onSave,
                enabled = !isReadOnly
            ) {
                Text("Save")
            }

            Button(
                onClick = onSaveAs
            ) {
                Text("Save As")
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Button(
                onClick = onShowSearch
            ) {
                Text("Find")
            }

            Button(
                onClick = onUndo,
                enabled =
                    canUndo &&
                            !isReadOnly
            ) {
                Text("Undo")
            }

            Button(
                onClick = onRedo,
                enabled =
                    canRedo &&
                            !isReadOnly
            ) {
                Text("Redo")
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {


            Button(
                onClick = onCreateVersion,
                enabled =
                    fileName != "Untitled" &&
                            !hasUnsavedChanges
            ) {
                Text("Create Version")
            }

            Button(
                onClick = onShowVersionHistory,
                enabled =
                    fileName != "Untitled"
            ) {
                Text("History")
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = "File Protection",
                    style =
                        MaterialTheme.typography
                            .titleSmall
                )

                Text(
                    text =
                        when {

                            fileName == "Untitled" -> {
                                "Save the file before locking it"
                            }

                            isReadOnly -> {
                                "This file is protected from modifications"
                            }

                            else -> {
                                "Lock this file to prevent accidental changes"
                            }
                        },
                    style =
                        MaterialTheme.typography
                            .bodySmall
                )
            }

            Button(
                onClick = onToggleReadOnly,
                enabled =
                    fileName != "Untitled"
            ) {

                Text(
                    text =
                        if (isReadOnly) {
                            "Unlock File"
                        } else {
                            "Lock File"
                        }
                )
            }
        }

        if (isReadOnly) {

            Surface(
                tonalElevation = 2.dp,
                shape =
                    MaterialTheme.shapes.small,
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 12.dp
                    )
            ) {

                Text(
                    text = "READ ONLY",
                    fontWeight =
                        FontWeight.Bold,
                    modifier =
                        Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                )
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 12.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Column {

                Text(
                    text = "Word Wrap",
                    style =
                        MaterialTheme.typography
                            .titleSmall
                )

                Text(
                    text =
                        if (wordWrapEnabled) {
                            "Long lines wrap to fit the screen"
                        } else {
                            "Long lines use horizontal scrolling"
                        },
                    style =
                        MaterialTheme.typography
                            .bodySmall
                )
            }

            Switch(
                checked = wordWrapEnabled,
                onCheckedChange =
                    onWordWrapChange
            )
        }



        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {

            Text(
                text =
                    buildString {

                        append(fileName)

                        if (hasUnsavedChanges) {
                            append(" *")
                        }

                        if (isReadOnly) {
                            append("  🔒")
                        }
                    },
                style =
                    MaterialTheme.typography
                        .headlineSmall
            )

            Text(
                text =
                    when {

                        fileName == "Untitled" -> {
                            "New text file"
                        }

                        isReadOnly -> {
                            "Viewing protected file"
                        }

                        else -> {
                            "Editing file"
                        }
                    },
                style =
                    MaterialTheme.typography
                        .bodySmall
            )
        }

        if (showSearchPanel) {

            HorizontalDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = "Find",
                    style =
                        MaterialTheme.typography
                            .labelLarge,
                    color = Color.Black,
                    modifier =
                        Modifier.padding(
                            bottom = 4.dp
                        )
                )



                OutlinedTextField(
                    value = searchText,
                    onValueChange =
                        onSearchTextChange,
                    modifier =
                        Modifier.fillMaxWidth(),

                    textStyle =
                        TextStyle(
                            color = Color.Black,
                            fontSize =
                                MaterialTheme
                                    .typography
                                    .bodyLarge
                                    .fontSize
                        ),

                    placeholder = {
                        Text(
                            text =
                                "Enter text to find",
                            color =
                                Color.Gray
                        )
                    },

                    colors =
                        OutlinedTextFieldDefaults
                            .colors(
                                focusedTextColor =
                                    Color.Black,

                                unfocusedTextColor =
                                    Color.Black,

                                cursorColor =
                                    Color.Black,

                                focusedBorderColor =
                                    MaterialTheme
                                        .colorScheme
                                        .primary,

                                unfocusedBorderColor =
                                    Color.Gray,

                                focusedPlaceholderColor =
                                    Color.Gray,

                                unfocusedPlaceholderColor =
                                    Color.Gray
                            ),

                    singleLine = true
                )

                Text(
                    text = "Replace with",
                    style =
                        MaterialTheme.typography
                            .labelLarge,
                    color = Color.Black,
                    modifier =
                        Modifier.padding(
                            top = 12.dp,
                            bottom = 4.dp
                        )
                )


                OutlinedTextField(
                    value = replaceText,
                    onValueChange =
                        onReplaceTextChange,
                    enabled =
                        !isReadOnly,
                    modifier =
                        Modifier.fillMaxWidth(),

                    textStyle =
                        TextStyle(
                            color = Color.Black,
                            fontSize =
                                MaterialTheme
                                    .typography
                                    .bodyLarge
                                    .fontSize
                        ),

                    placeholder = {
                        Text(
                            text =
                                if (isReadOnly) {
                                    "Unavailable in Read-Only mode"
                                } else {
                                    "Enter replacement text"
                                },
                            color =
                                Color.Gray
                        )
                    },

                    colors =
                        OutlinedTextFieldDefaults
                            .colors(
                                focusedTextColor =
                                    Color.Black,

                                unfocusedTextColor =
                                    Color.Black,

                                cursorColor =
                                    Color.Black,

                                focusedBorderColor =
                                    MaterialTheme
                                        .colorScheme
                                        .primary,

                                unfocusedBorderColor =
                                    Color.Gray,

                                focusedPlaceholderColor =
                                    Color.Gray,

                                unfocusedPlaceholderColor =
                                    Color.Gray
                            ),

                    singleLine = true
                )

                Text(
                    text =
                        when {

                            searchText.isBlank() ->
                                "Enter text to search"

                            totalMatches == 0 ->
                                "No matches"

                            else ->
                                "$currentMatch of $totalMatches"
                        },

                    color = Color.Black,

                    modifier =
                        Modifier.padding(
                            top = 12.dp,
                            bottom = 6.dp
                        ),

                    style =
                        MaterialTheme.typography
                            .bodyMedium
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    TextButton(
                        onClick =
                            onPreviousMatch,
                        enabled =
                            totalMatches > 0
                    ) {
                        Text("Previous")
                    }

                    TextButton(
                        onClick =
                            onNextMatch,
                        enabled =
                            totalMatches > 0
                    ) {
                        Text("Next")
                    }
                }

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(4.dp)
                ) {

                    TextButton(
                        onClick =
                            onReplace,
                        enabled =
                            totalMatches > 0 &&
                                    !isReadOnly
                    ) {
                        Text("Replace")
                    }

                    TextButton(
                        onClick =
                            onReplaceAll,
                        enabled =
                            totalMatches > 0 &&
                                    !isReadOnly
                    ) {
                        Text("Replace All")
                    }

                    TextButton(
                        onClick =
                            onCloseSearch
                    ) {
                        Text("Close")
                    }
                }
            }
        }

        HorizontalDivider()


        val isKotlinFile =
            fileName.endsWith(
                suffix = ".kt",
                ignoreCase = true
            )

        val editorTextStyle =
            MaterialTheme
                .typography
                .bodyLarge
                .copy(
                    color = Color.Black
                )

        if (wordWrapEnabled) {



            BasicTextField(
                value = editorValue,

                onValueChange =
                    onEditorValueChange,


                readOnly =
                    isReadOnly,

                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),

                textStyle =
                    editorTextStyle,

                visualTransformation =
                    if (isKotlinFile) {

                        KotlinSyntaxVisualTransformation()

                    } else {

                        androidx.compose.ui.text.input
                            .VisualTransformation.None
                    }
            )

        } else {


            val horizontalScrollState =
                rememberScrollState()

            BasicTextField(
                value = editorValue,

                onValueChange =
                    onEditorValueChange,


                readOnly =
                    isReadOnly,

                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(
                        horizontalScrollState
                    )
                    .padding(16.dp),

                textStyle =
                    editorTextStyle,

                visualTransformation =
                    if (isKotlinFile) {

                        KotlinSyntaxVisualTransformation()

                    } else {

                        androidx.compose.ui.text.input
                            .VisualTransformation.None
                    }
            )
        }
    }
}