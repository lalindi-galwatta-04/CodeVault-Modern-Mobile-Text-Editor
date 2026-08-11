package com.example.codevault

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.example.codevault.ui.editor.EditorScreen
import com.example.codevault.ui.home.HomeScreen
import com.example.codevault.ui.versionhistory.VersionHistoryScreen
import com.example.codevault.ui.versionhistory.HistoricalVersionScreen
import com.example.codevault.ui.theme.CodeVaultTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            CodeVaultTheme {

                //BASIC EDITOR STATE

                var currentScreen by remember {
                    mutableStateOf("home")
                }


                var editorValue by remember {
                    mutableStateOf(
                        TextFieldValue("")
                    )
                }

                //Last successfully saved text.

                var savedText by remember {
                    mutableStateOf("")
                }

                var fileName by remember {
                    mutableStateOf("Untitled")
                }

                var fileUri by remember {
                    mutableStateOf<Uri?>(null)
                }

                //Recent Files shown on the Home screen.

                var recentFiles by remember {
                    mutableStateOf(
                        RecentFilesManager.getRecentFiles(
                            this@MainActivity
                        )
                    )
                }

                fun refreshRecentFiles() {
                    recentFiles =
                        RecentFilesManager.getRecentFiles(
                            this@MainActivity
                        )
                }

                var wordWrapEnabled by remember {
                    mutableStateOf(
                        EditorPreferencesManager.isWordWrapEnabled(
                            this@MainActivity
                        )
                    )
                }

                var versionMessage by remember {
                    mutableStateOf<String?>(null)
                }

                //FILE READ-ONLY STATE
                var isReadOnly by remember {
                    mutableStateOf(false)
                }

                /*
                 * Selected historical version.
                 *
                 * The next milestone will use this to open a
                 * reconstructed read-only snapshot.
                 */
                var selectedHistoryVersion by remember {
                    mutableStateOf<FileVersion?>(null)
                }

                //RESTORE HISTORICAL VERSION STATE
                var showRestoreVersionDialog by remember {
                    mutableStateOf(false)
                }

                var showUnsavedDialog by remember {
                    mutableStateOf(false)
                }

                val recoveryAvailableAtStartup = remember {
                    RecoveryManager.hasRecoveryDraft(this@MainActivity)
                }

                var showRecoveryDialog by remember {
                    mutableStateOf(recoveryAvailableAtStartup)
                }

                var recoveryDecisionPending by remember {
                    mutableStateOf(recoveryAvailableAtStartup)
                }

                // Undo / Redo state
                val undoRedoManager = remember { UndoRedoManager() }

                var canUndo by remember { mutableStateOf(false) }
                var canRedo by remember { mutableStateOf(false) }

                fun updateUndoRedoState() {
                    canUndo = undoRedoManager.canUndo()
                    canRedo = undoRedoManager.canRedo()
                }

                fun clearUndoRedoHistory() {
                    undoRedoManager.clear()
                    updateUndoRedoState()
                }

                //SEARCH & REPLACE STATE
                var showSearchPanel by remember {
                    mutableStateOf(false)
                }

                var searchText by remember {
                    mutableStateOf("")
                }

                var replaceText by remember {
                    mutableStateOf("")
                }


                var currentMatchIndex by remember {
                    mutableIntStateOf(0)
                }


                val matches =
                    findMatches(
                        text = editorValue.text,
                        query = searchText
                    )


                val safeMatchIndex =
                    if (matches.isEmpty()) {

                        0

                    } else {

                        currentMatchIndex
                            .coerceIn(
                                0,
                                matches.lastIndex
                            )
                    }

                val currentMatchNumber =
                    if (matches.isEmpty()) {

                        0

                    } else {

                        safeMatchIndex + 1
                    }


                val hasUnsavedChanges =
                    editorValue.text != savedText

                //AUTOMATIC CRASH RECOVERY
                LaunchedEffect(
                    editorValue.text,
                    savedText,
                    fileName,
                    fileUri,
                    recoveryDecisionPending
                ) {
                    if (!recoveryDecisionPending) {
                        if (hasUnsavedChanges) {
                            RecoveryManager.saveRecoveryDraft(
                                context = this@MainActivity,
                                text = editorValue.text,
                                fileName = fileName,
                                fileUri = fileUri?.toString()
                            )
                        } else {
                            RecoveryManager.clearRecoveryDraft(
                                context = this@MainActivity
                            )
                        }
                    }
                }

                //SAVE AS LAUNCHER

                val createFileLauncher =
                    rememberLauncherForActivityResult(
                        contract =
                            ActivityResultContracts
                                .CreateDocument(
                                    "application/octet-stream"
                                )
                    ) { uri ->

                        if (uri != null) {

                            val saveSuccessful =
                                saveFile(
                                    uri = uri,
                                    text =
                                        editorValue.text
                                )

                            if (saveSuccessful) {

                                fileUri = uri

                                fileName =
                                    getFileNameFromUri(uri)
                                        ?: "Saved File"

                                savedText =
                                    editorValue.text


                                isReadOnly = false

                                ReadOnlyManager.setReadOnly(
                                    context = this@MainActivity,
                                    fileId = uri.toString(),
                                    locked = false
                                )

                                // Add the successfully saved file to Recent Files.
                                RecentFilesManager.addRecentFile(
                                    context = this@MainActivity,
                                    fileName = fileName,
                                    fileUri = uri
                                )

                                refreshRecentFiles()
                            }
                        }
                    }

                //OPEN FILE LAUNCHER

                val openFileLauncher =
                    rememberLauncherForActivityResult(
                        contract =
                            ActivityResultContracts
                                .OpenDocument()
                    ) { uri ->

                        if (uri != null) {

                            try {

                                contentResolver
                                    .takePersistableUriPermission(
                                        uri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    )

                            } catch (
                                exception: SecurityException
                            ) {

                                exception.printStackTrace()
                            }

                            val openedText =
                                readFile(uri)

                            fileUri = uri

                            fileName =
                                getFileNameFromUri(uri)
                                    ?: "Opened File"

                            // Restore this document's persistent Read-Only state.

                            isReadOnly =
                                ReadOnlyManager.isReadOnly(
                                    context = this@MainActivity,
                                    fileId = uri.toString()
                                )

                            editorValue =
                                TextFieldValue(
                                    text = openedText,
                                    selection =
                                        TextRange(
                                            openedText.length
                                        )
                                )

                            savedText = openedText

                            // Add the successfully opened fileto Recent Files.
                            RecentFilesManager.addRecentFile(
                                context = this@MainActivity,
                                fileName = fileName,
                                fileUri = uri
                            )

                            refreshRecentFiles()

                            clearUndoRedoHistory()


                            showSearchPanel = false

                            searchText = ""

                            replaceText = ""

                            currentMatchIndex = 0

                            currentScreen = "editor"
                        }
                    }

                //NDROID SYSTEM BACK

                BackHandler(
                    enabled =
                        currentScreen == "editor" &&
                                !showUnsavedDialog
                ) {


                    if (showSearchPanel) {

                        showSearchPanel = false

                    } else if (
                        hasUnsavedChanges
                    ) {

                        showUnsavedDialog = true

                    } else {

                        currentScreen = "home"
                    }
                }

                //SCREEN CONTENT

                when (currentScreen) {

                    /*
                     * HOME
                     */
                    "home" -> {

                        HomeScreen(

                            recentFiles = recentFiles,

                            onNewFile = {

                                editorValue =
                                    TextFieldValue("")

                                savedText = ""

                                fileName = "Untitled"

                                fileUri = null

                                //A brand-new unsaved document is always editable.

                                isReadOnly = false

                                clearUndoRedoHistory()


                                showSearchPanel = false

                                searchText = ""

                                replaceText = ""

                                currentMatchIndex = 0

                                currentScreen = "editor"
                            },

                            onOpenFile = {

                                openFileLauncher.launch(
                                    arrayOf(
                                        "text/plain",
                                        "text/markdown",
                                        "application/octet-stream"
                                    )
                                )
                            },

                            onRecentFileClick = {
                                    recentFile ->

                                val recentUri =
                                    Uri.parse(
                                        recentFile.uri
                                    )


                                try {

                                    val openedText =
                                        contentResolver
                                            .openInputStream(
                                                recentUri
                                            )
                                            ?.bufferedReader(
                                                Charsets.UTF_8
                                            )
                                            ?.use {
                                                it.readText()
                                            }
                                            ?: throw IllegalStateException(
                                                "Unable to read recent file."
                                            )

                                    fileUri =
                                        recentUri

                                    fileName =
                                        getFileNameFromUri(
                                            recentUri
                                        )
                                            ?: recentFile.name


                                    isReadOnly =
                                        ReadOnlyManager.isReadOnly(
                                            context = this@MainActivity,
                                            fileId = recentUri.toString()
                                        )

                                    editorValue =
                                        TextFieldValue(
                                            text =
                                                openedText,
                                            selection =
                                                TextRange(
                                                    openedText.length
                                                )
                                        )

                                    savedText =
                                        openedText

                                    RecentFilesManager
                                        .addRecentFile(
                                            context =
                                                this@MainActivity,
                                            fileName =
                                                fileName,
                                            fileUri =
                                                recentUri
                                        )

                                    refreshRecentFiles()

                                    clearUndoRedoHistory()

                                    showSearchPanel =
                                        false

                                    searchText = ""

                                    replaceText = ""

                                    currentMatchIndex =
                                        0

                                    currentScreen =
                                        "editor"

                                } catch (
                                    exception: Exception
                                ) {

                                    exception
                                        .printStackTrace()

                                    RecentFilesManager
                                        .removeRecentFile(
                                            context =
                                                this@MainActivity,
                                            fileUri =
                                                recentFile.uri
                                        )

                                    refreshRecentFiles()
                                }
                            },

                            onRemoveRecentFile = {
                                    recentFile ->

                                RecentFilesManager
                                    .removeRecentFile(
                                        context =
                                            this@MainActivity,
                                        fileUri =
                                            recentFile.uri
                                    )

                                refreshRecentFiles()
                            },

                            onClearRecentFiles = {

                                RecentFilesManager
                                    .clearRecentFiles(
                                        context =
                                            this@MainActivity
                                    )

                                refreshRecentFiles()
                            }
                        )
                    }

                    //EDITOR

                    "editor" -> {

                        EditorScreen(

                            fileName = fileName,

                            editorValue = editorValue,

                            hasUnsavedChanges =
                                hasUnsavedChanges,

                            isReadOnly =
                                isReadOnly,

                            showSearchPanel =
                                showSearchPanel,

                            searchText = searchText,

                            replaceText = replaceText,

                            currentMatch =
                                currentMatchNumber,

                            totalMatches =
                                matches.size,

                            canUndo = canUndo,

                            canRedo = canRedo,

                            wordWrapEnabled = wordWrapEnabled,

                            onWordWrapChange = { enabled ->
                                wordWrapEnabled = enabled
                                EditorPreferencesManager.setWordWrapEnabled(
                                    this@MainActivity,
                                    enabled
                                )
                            },


                            onEditorValueChange = {
                                    newValue ->


                                if (
                                    !isReadOnly ||
                                    newValue.text == editorValue.text
                                ) {

                                    if (
                                        newValue.text !=
                                        editorValue.text
                                    ) {

                                        undoRedoManager.recordChange(
                                            editorValue.text
                                        )

                                        updateUndoRedoState()
                                    }

                                    editorValue =
                                        newValue
                                }
                            },

                            //BACK BUTTON

                            onBack = {

                                if (showSearchPanel) {

                                    showSearchPanel =
                                        false

                                } else if (
                                    hasUnsavedChanges
                                ) {

                                    showUnsavedDialog =
                                        true

                                } else {

                                    currentScreen =
                                        "home"
                                }
                            },

                            //SAVE

                            onSave = {

                                val currentUri =
                                    fileUri

                                if (currentUri != null) {

                                    val successful =
                                        saveFile(
                                            uri =
                                                currentUri,
                                            text =
                                                editorValue.text
                                        )

                                    if (successful) {

                                        savedText =
                                            editorValue.text

                                        // Keep this successfully saved
                                        // file in Recent Files.
                                        RecentFilesManager.addRecentFile(
                                            context = this@MainActivity,
                                            fileName = fileName,
                                            fileUri = currentUri
                                        )

                                        refreshRecentFiles()
                                    }

                                } else {

                                    createFileLauncher
                                        .launch(
                                            suggestFileName(
                                                editorValue.text
                                            )
                                        )
                                }
                            },

                            /*
                             * SAVE AS
                             */
                            onSaveAs = {

                                createFileLauncher
                                    .launch(
                                        suggestFileName(
                                            editorValue.text
                                        )
                                    )
                            },

                            //OPEN SEARCH PANEL

                            onShowSearch = {

                                showSearchPanel = true

                                currentMatchIndex = 0


                                val currentMatches =
                                    findMatches(
                                        editorValue.text,
                                        searchText
                                    )

                                if (
                                    currentMatches
                                        .isNotEmpty()
                                ) {

                                    editorValue =
                                        selectMatch(
                                            editorValue,
                                            currentMatches[0]
                                        )
                                }
                            },

                            //UNDO

                            onUndo = {

                                if (!isReadOnly) {

                                    val restoredText =
                                        undoRedoManager.undo(
                                            editorValue.text
                                        )

                                    if (restoredText != null) {
                                        editorValue =
                                            TextFieldValue(
                                                text = restoredText,
                                                selection =
                                                    TextRange(
                                                        restoredText.length
                                                    )
                                            )

                                        currentMatchIndex = 0
                                        updateUndoRedoState()
                                    }
                                }
                            },

                            //REDO

                            onRedo = {

                                if (!isReadOnly) {

                                    val restoredText =
                                        undoRedoManager.redo(
                                            editorValue.text
                                        )

                                    if (restoredText != null) {
                                        editorValue =
                                            TextFieldValue(
                                                text = restoredText,
                                                selection =
                                                    TextRange(
                                                        restoredText.length
                                                    )
                                            )

                                        currentMatchIndex = 0
                                        updateUndoRedoState()
                                    }
                                }
                            },

                            //LOCK / UNLOCK CURRENT FILE                       onToggleReadOnly = {

                                val currentUri =
                                    fileUri

                                if (currentUri == null) {

                                    versionMessage =
                                        "Save the file before changing Read-Only mode."

                                } else if (
                                    hasUnsavedChanges &&
                                    !isReadOnly
                                ) {


                                    versionMessage =
                                        "Save your latest changes before locking the file."

                                } else {

                                    val newReadOnlyState =
                                        !isReadOnly

                                    ReadOnlyManager.setReadOnly(
                                        context = this@MainActivity,
                                        fileId =
                                            currentUri.toString(),
                                        locked =
                                            newReadOnlyState
                                    )

                                    isReadOnly =
                                        newReadOnlyState


                                    if (newReadOnlyState) {

                                        showSearchPanel =
                                            false

                                        replaceText =
                                            ""
                                    }

                                    versionMessage =
                                        if (newReadOnlyState) {

                                            "$fileName is now Read Only."

                                        } else {

                                            "$fileName is now editable."
                                        }
                                }
                            },

                            //CREATE VERSION
                            onCreateVersion = {

                                val currentUri = fileUri

                                if (currentUri == null) {

                                    versionMessage =
                                        "Save the file before creating a version."

                                } else if (hasUnsavedChanges) {

                                    versionMessage =
                                        "Save your latest changes before creating a version."

                                } else {

                                    val createdVersion =
                                        VersionHistoryManager.createVersion(
                                            context = this@MainActivity,
                                            fileId = currentUri.toString(),
                                            currentText = editorValue.text
                                        )

                                    versionMessage =
                                        "Version ${createdVersion.versionNumber} created."
                                }
                            },

                            //VERSION HISTORY
                            onShowVersionHistory = {

                                val currentUri = fileUri

                                if (currentUri == null) {

                                    versionMessage =
                                        "Save the file before viewing version history."

                                } else {

                                    currentScreen =
                                        "history"
                                }
                            },

                            //CLOSE SEARCH

                            onCloseSearch = {

                                showSearchPanel = false


                                editorValue =
                                    editorValue.copy(
                                        selection =
                                            TextRange(
                                                editorValue
                                                    .selection
                                                    .end
                                            )
                                    )
                            },

                            //SEARCH QUERY CHANGED

                            onSearchTextChange = {
                                    newQuery ->

                                searchText =
                                    newQuery

                                currentMatchIndex = 0

                                val newMatches =
                                    findMatches(
                                        text =
                                            editorValue.text,
                                        query =
                                            newQuery
                                    )

                                //Immediately select first matching result.

                                if (
                                    newMatches.isNotEmpty()
                                ) {

                                    editorValue =
                                        selectMatch(
                                            editorValue,
                                            newMatches[0]
                                        )
                                }
                            },

                            //REPLACEMENT TEXT CHANGED

                            onReplaceTextChange = {
                                    newText ->

                                replaceText = newText
                            },

                            //PREVIOUS MATCH

                            onPreviousMatch = {

                                val currentMatches =
                                    findMatches(
                                        editorValue.text,
                                        searchText
                                    )

                                if (
                                    currentMatches
                                        .isNotEmpty()
                                ) {

                                    currentMatchIndex =
                                        if (
                                            safeMatchIndex == 0
                                        ) {

                                            currentMatches
                                                .lastIndex

                                        } else {

                                            safeMatchIndex - 1
                                        }

                                    editorValue =
                                        selectMatch(
                                            editorValue,
                                            currentMatches[
                                                currentMatchIndex
                                            ]
                                        )
                                }
                            },

                            //NEXT MATCH

                            onNextMatch = {

                                val currentMatches =
                                    findMatches(
                                        editorValue.text,
                                        searchText
                                    )

                                if (
                                    currentMatches
                                        .isNotEmpty()
                                ) {

                                    currentMatchIndex =
                                        if (
                                            safeMatchIndex ==
                                            currentMatches
                                                .lastIndex
                                        ) {

                                            0

                                        } else {

                                            safeMatchIndex + 1
                                        }

                                    editorValue =
                                        selectMatch(
                                            editorValue,
                                            currentMatches[
                                                currentMatchIndex
                                            ]
                                        )
                                }
                            },

                            //REPLACE CURRENT MATCH

                            onReplace = {

                                val currentMatches =
                                    findMatches(
                                        editorValue.text,
                                        searchText
                                    )

                                if (
                                    currentMatches
                                        .isNotEmpty()
                                ) {

                                    val index =
                                        currentMatchIndex
                                            .coerceIn(
                                                0,
                                                currentMatches
                                                    .lastIndex
                                            )

                                    val match =
                                        currentMatches[index]

                                    val previousText =
                                        editorValue.text

                                    val newText =
                                        previousText
                                            .replaceRange(
                                                match,
                                                replaceText
                                            )

                                    if (newText != previousText) {
                                        undoRedoManager.recordChange(
                                            previousText
                                        )
                                        updateUndoRedoState()
                                    }

                                    /*
                                     * Put cursor immediately
                                     * after replacement.
                                     */
                                    val newCursorPosition =
                                        match.first +
                                                replaceText.length

                                    editorValue =
                                        TextFieldValue(
                                            text = newText,
                                            selection =
                                                TextRange(
                                                    newCursorPosition
                                                )
                                        )


                                    val updatedMatches =
                                        findMatches(
                                            newText,
                                            searchText
                                        )

                                    if (
                                        updatedMatches.isEmpty()
                                    ) {

                                        currentMatchIndex = 0

                                    } else {

                                        /*
                                         * Keep index valid.
                                         */
                                        currentMatchIndex =
                                            index.coerceAtMost(
                                                updatedMatches
                                                    .lastIndex
                                            )

                                        editorValue =
                                            selectMatch(
                                                editorValue,
                                                updatedMatches[
                                                    currentMatchIndex
                                                ]
                                            )
                                    }
                                }
                            },

                            //REPLACE ALL

                            onReplaceAll = {

                                if (
                                    searchText.isNotEmpty()
                                ) {

                                    val previousText =
                                        editorValue.text

                                    val replaced =
                                        replaceAllIgnoreCase(
                                            text =
                                                previousText,
                                            search =
                                                searchText,
                                            replacement =
                                                replaceText
                                        )

                                    if (replaced != previousText) {
                                        undoRedoManager.recordChange(
                                            previousText
                                        )
                                        updateUndoRedoState()
                                    }

                                    editorValue =
                                        TextFieldValue(
                                            text = replaced,
                                            selection =
                                                TextRange(
                                                    replaced.length
                                                )
                                        )

                                    currentMatchIndex = 0
                                }
                            }
                        )
                    }

                    //VERSION HISTORY

                    "history" -> {

                        val currentUri =
                            fileUri

                        val versions =
                            if (currentUri != null) {

                                VersionHistoryManager
                                    .getVersions(
                                        context =
                                            this@MainActivity,
                                        fileId =
                                            currentUri.toString()
                                    )

                            } else {

                                emptyList()
                            }

                        VersionHistoryScreen(

                            fileName =
                                fileName,

                            versions =
                                versions,

                            onBack = {

                                currentScreen =
                                    "editor"
                            },

                            onVersionClick = {
                                    version ->


                                currentScreen =
                                    "historicalVersion"
                            }
                        )
                    }

                    //HISTORICAL VERSION - READ ONLY

                    "historicalVersion" -> {

                        val selectedVersion =
                            selectedHistoryVersion

                        val currentUri =
                            fileUri


                        if (
                            selectedVersion != null &&
                            currentUri != null
                        ) {

                            /*
                             * Load all stored versions for this file.
                             */
                            val versions =
                                VersionHistoryManager
                                    .getVersions(
                                        context =
                                            this@MainActivity,
                                        fileId =
                                            currentUri.toString()
                                    )

                            //Reconstruct the exact selected version.

                            val reconstructedText =
                                VersionHistoryManager
                                    .reconstructVersion(
                                        versions =
                                            versions,
                                        versionNumber =
                                            selectedVersion
                                                .versionNumber
                                    )

                            HistoricalVersionScreen(

                                fileName =
                                    fileName,

                                version =
                                    selectedVersion,

                                reconstructedText =
                                    reconstructedText,

                                onBackToHistory = {

                                    currentScreen =
                                        "history"
                                },


                                onRestoreVersion = {


                                    if (isReadOnly) {

                                        versionMessage =
                                            "This file is Read Only. Unlock the file before restoring a historical version."

                                    } else {

                                        showRestoreVersionDialog =
                                            true
                                    }
                                }
                            )

                        } else {


                            LaunchedEffect(
                                selectedVersion,
                                currentUri
                            ) {

                                currentScreen =
                                    "history"
                            }
                        }
                    }
                }

                //RESTORE HISTORICAL VERSION CONFIRMATION
                if (showRestoreVersionDialog) {

                    val versionToRestore =
                        selectedHistoryVersion

                    AlertDialog(

                        onDismissRequest = {

                            showRestoreVersionDialog =
                                false
                        },

                        title = {

                            Text("Restore Version")
                        },

                        text = {

                            Text(
                                if (versionToRestore != null) {

                                    "Restore Version " +
                                            "${versionToRestore.versionNumber} " +
                                            "into the editor?\n\n" +
                                            "Your existing version history will be kept. " +
                                            "The restored content will become the current " +
                                            "editor content and can then be saved as a new version."

                                } else {

                                    "Unable to restore this version."
                                }
                            )
                        },

                        confirmButton = {

                            TextButton(

                                onClick = {

                                    val selectedVersion =
                                        versionToRestore

                                    val currentUri =
                                        fileUri

                                    if (
                                        selectedVersion != null &&
                                        currentUri != null &&
                                        !isReadOnly
                                    ) {

                                        /*
                                         * Load the complete history for the
                                         * current document.
                                         */
                                        val versions =
                                            VersionHistoryManager
                                                .getVersions(
                                                    context =
                                                        this@MainActivity,

                                                    fileId =
                                                        currentUri
                                                            .toString()
                                                )

                                        //Reconstruct the exact historical snapshot selected by the user
                                        val restoredText =
                                            VersionHistoryManager
                                                .reconstructVersion(
                                                    versions =
                                                        versions,

                                                    versionNumber =
                                                        selectedVersion
                                                            .versionNumber
                                                )


                                        editorValue =
                                            TextFieldValue(
                                                text =
                                                    restoredText,

                                                selection =
                                                    TextRange(
                                                        restoredText.length
                                                    )
                                            )

                                        //Start a fresh Undo/Redo session for the restored editor state.

                                        clearUndoRedoHistory()

                                        /Reset Search state because the editorcontent has changed substantially.

                                        showSearchPanel =
                                            false

                                        searchText = ""

                                        replaceText = ""

                                        currentMatchIndex =
                                            0


                                        showRestoreVersionDialog =
                                            false

                                        selectedHistoryVersion =
                                            null

                                        currentScreen =
                                            "editor"
                                    }
                                }
                            ) {

                                Text("Restore")
                            }
                        },

                        dismissButton = {

                            TextButton(

                                onClick = {

                                    showRestoreVersionDialog =
                                        false
                                }
                            ) {

                                Text("Cancel")
                            }
                        }
                    )
                }

                //VERSION FEEDBACK DIALOG
                versionMessage?.let { message ->

                    AlertDialog(
                        onDismissRequest = {
                            versionMessage = null
                        },
                        title = {
                            Text("Version Control")
                        },
                        text = {
                            Text(message)
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    versionMessage = null
                                }
                            ) {
                                Text("OK")
                            }
                        }
                    )
                }

                //RECOVERY AVAILABLE DIALOG

                if (showRecoveryDialog) {
                    AlertDialog(
                        onDismissRequest = { },
                        title = {
                            Text("Recovery Available")
                        },
                        text = {
                            Text(
                                "CodeVault found unsaved work from your " +
                                        "previous editing session. Would you like to restore it?"
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val recoveredText =
                                        RecoveryManager.getRecoveryText(this@MainActivity)
                                    val recoveredFileName =
                                        RecoveryManager.getRecoveryFileName(this@MainActivity)
                                    val recoveredUriString =
                                        RecoveryManager.getRecoveryFileUri(this@MainActivity)

                                    RecoveryManager.clearRecoveryDraft(this@MainActivity)

                                    editorValue = TextFieldValue(
                                        text = recoveredText,
                                        selection = TextRange(recoveredText.length)
                                    )

                                    savedText = ""
                                    fileName = recoveredFileName
                                    fileUri = recoveredUriString
                                        ?.takeIf { it.isNotBlank() }
                                        ?.let { Uri.parse(it) }

                                    clearUndoRedoHistory()
                                    showSearchPanel = false
                                    searchText = ""
                                    replaceText = ""
                                    currentMatchIndex = 0

                                    showRecoveryDialog = false
                                    recoveryDecisionPending = false
                                    currentScreen = "editor"
                                }
                            ) {
                                Text("Restore")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    RecoveryManager.clearRecoveryDraft(this@MainActivity)
                                    showRecoveryDialog = false
                                    recoveryDecisionPending = false
                                }
                            ) {
                                Text("Discard")
                            }
                        }
                    )
                }

                //UNSAVED CHANGES DIALOG

                if (showUnsavedDialog) {

                    AlertDialog(

                        onDismissRequest = {

                            showUnsavedDialog = false
                        },

                        title = {

                            Text(
                                "Unsaved Changes"
                            )
                        },

                        text = {

                            Text(
                                "You have unsaved changes in " +
                                        "$fileName. What would " +
                                        "you like to do?"
                            )
                        },

                        confirmButton = {

                            TextButton(

                                onClick = {

                                    val currentUri =
                                        fileUri

                                    if (
                                        currentUri != null
                                    ) {

                                        val successful =
                                            saveFile(
                                                uri =
                                                    currentUri,
                                                text =
                                                    editorValue
                                                        .text
                                            )

                                        if (successful) {

                                            savedText =
                                                editorValue.text

                                            showUnsavedDialog =
                                                false

                                            currentScreen =
                                                "home"
                                        }

                                    } else {

                                        showUnsavedDialog =
                                            false

                                        createFileLauncher
                                            .launch(
                                                suggestFileName(
                                                    editorValue
                                                        .text
                                                )
                                            )
                                    }
                                }

                            ) {

                                Text("Save")
                            }
                        },

                        dismissButton = {

                            DialogSecondaryButtons(

                                onDiscard = {

                                    /*
                                     * Restore saved content.
                                     */
                                    editorValue =
                                        TextFieldValue(
                                            text = savedText,
                                            selection =
                                                TextRange(
                                                    savedText
                                                        .length
                                                )
                                        )

                                    showUnsavedDialog =
                                        false

                                    currentScreen =
                                        "home"
                                },

                                onCancel = {

                                    showUnsavedDialog =
                                        false
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    //IND MATCHES
    private fun findMatches(
        text: String,
        query: String
    ): List<IntRange> {

        if (
            query.isBlank() ||
            text.isEmpty()
        ) {
            return emptyList()
        }

        val matches =
            mutableListOf<IntRange>()

        var startIndex = 0

        while (
            startIndex <=
            text.length - query.length
        ) {

            val foundIndex =
                text.indexOf(
                    string = query,
                    startIndex = startIndex,
                    ignoreCase = true
                )

            if (foundIndex == -1) {

                break
            }

            matches.add(
                foundIndex until
                        foundIndex + query.length
            )


            startIndex =
                foundIndex + query.length
        }

        return matches
    }

    //SELECT MATCH
    private fun selectMatch(
        editorValue: TextFieldValue,
        range: IntRange
    ): TextFieldValue {

        return editorValue.copy(

            selection =
                TextRange(
                    start = range.first,
                    end = range.last + 1
                )
        )
    }

    //REPLACE ALL
    private fun replaceAllIgnoreCase(
        text: String,
        search: String,
        replacement: String
    ): String {

        if (search.isEmpty()) {

            return text
        }

        val result =
            StringBuilder()

        var currentIndex = 0

        while (currentIndex < text.length) {

            val foundIndex =
                text.indexOf(
                    string = search,
                    startIndex =
                        currentIndex,
                    ignoreCase = true
                )

            if (foundIndex == -1) {

                result.append(
                    text.substring(
                        currentIndex
                    )
                )

                break
            }


            result.append(
                text.substring(
                    currentIndex,
                    foundIndex
                )
            )


            result.append(
                replacement
            )


            currentIndex =
                foundIndex +
                        search.length
        }

        return result.toString()
    }

    //READ FILE
    private fun readFile(
        uri: Uri
    ): String {

        return try {

            contentResolver
                .openInputStream(uri)
                ?.bufferedReader(
                    Charsets.UTF_8
                )
                ?.use {

                    it.readText()
                }
                ?: ""

        } catch (exception: Exception) {

            exception.printStackTrace()

            ""
        }
    }

    //SAVE FILE
    private fun saveFile(
        uri: Uri,
        text: String
    ): Boolean {

        return try {

            val outputStream =
                contentResolver
                    .openOutputStream(
                        uri,
                        "wt"
                    )

            if (outputStream == null) {

                false

            } else {

                outputStream
                    .bufferedWriter(
                        Charsets.UTF_8
                    )
                    .use {

                        it.write(text)
                    }

                true
            }

        } catch (exception: Exception) {

            exception.printStackTrace()

            false
        }
    }

    //SUGGEST FILE NAME
    private fun suggestFileName(
        text: String
    ): String {

        return if (
            text.contains("fun ") ||
            text.contains("val ") ||
            text.contains("var ") ||
            text.contains("class ")
        ) {

            "Untitled.kt"

        } else {

            "Untitled.md"
        }
    }

    //GET FILE NAME
    private fun getFileNameFromUri(
        uri: Uri
    ): String? {

        var result: String? = null

        contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )?.use { cursor ->

            val nameIndex =
                cursor.getColumnIndex(
                    OpenableColumns.DISPLAY_NAME
                )

            if (
                nameIndex >= 0 &&
                cursor.moveToFirst()
            ) {

                result =
                    cursor.getString(
                        nameIndex
                    )
            }
        }

        return result
    }
}

//DIALOG SECONDARY BUTTONS
@Composable
private fun DialogSecondaryButtons(
    onDiscard: () -> Unit,
    onCancel: () -> Unit
) {

    Row {

        TextButton(
            onClick = onDiscard
        ) {

            Text("Discard")
        }

        TextButton(
            onClick = onCancel
        ) {

            Text("Cancel")
        }
    }
}