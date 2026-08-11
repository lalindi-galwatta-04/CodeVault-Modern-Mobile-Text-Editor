package com.example.codevault.ui.versionhistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codevault.FileVersion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VersionHistoryScreen(
    fileName: String,
    versions: List<FileVersion>,
    onBack: () -> Unit,
    onVersionClick: (FileVersion) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // HEADER


        Button(
            onClick = onBack
        ) {
            Text("Back to Editor")
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Version History",
            style =
                MaterialTheme.typography
                    .headlineMedium
        )

        Text(
            text = fileName,
            style =
                MaterialTheme.typography
                    .titleMedium,
            modifier =
                Modifier.padding(
                    top = 4.dp
                )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text =
                "${versions.size} saved version(s)",
            style =
                MaterialTheme.typography
                    .bodyMedium
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // EMPTY HISTORY


        if (versions.isEmpty()) {

            Text(
                text =
                    "No versions have been created yet.",
                style =
                    MaterialTheme.typography
                        .bodyLarge
            )

            Text(
                text =
                    "Return to the editor and use " +
                            "\"Create Version\" to create " +
                            "the first snapshot.",
                style =
                    MaterialTheme.typography
                        .bodyMedium,
                modifier =
                    Modifier.padding(
                        top = 8.dp
                    )
            )

        } else {

            // VERSION LIST

            val newestVersionNumber =
                versions.maxOfOrNull {
                    it.versionNumber
                }

            LazyColumn(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {

                items(
                    items =
                        versions.sortedByDescending {
                            it.versionNumber
                        },

                    key = {
                        it.versionNumber
                    }
                ) { version ->

                    VersionHistoryItem(
                        version = version,

                        isLatest =
                            version.versionNumber ==
                                    newestVersionNumber,

                        onClick = {
                            onVersionClick(
                                version
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun VersionHistoryItem(
    version: FileVersion,
    isLatest: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
        ) {

            /*
             * VERSION NUMBER + TYPE
             */

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        "Version ${version.versionNumber}",

                    style =
                        MaterialTheme.typography
                            .titleLarge,

                    fontWeight =
                        FontWeight.SemiBold
                )

                Text(
                    text =
                        when {

                            version.versionNumber == 1 ->
                                "Base snapshot"

                            isLatest ->
                                "Latest snapshot"

                            else ->
                                "Delta snapshot"
                        },

                    style =
                        MaterialTheme.typography
                            .labelMedium
                )
            }

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            /*
             * CREATION DATE
             */

            Text(
                text =
                    "Created: ${
                        formatVersionDate(
                            version.createdAt
                        )
                    }",

                style =
                    MaterialTheme.typography
                        .bodyMedium
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            /*
             * STORAGE INFORMATION
             */

            Text(
                text =
                    if (
                        version.versionNumber == 1
                    ) {

                        "Stored as the initial base version."

                    } else {

                        "Stored incrementally as changes " +
                                "from the previous version."
                    },

                style =
                    MaterialTheme.typography
                        .bodySmall
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            //VIEW VERSION


            Button(
                onClick = onClick
            ) {

                Text("View Version")
            }
        }
    }
}


//FORMAT VERSION TIMESTAMP


private fun formatVersionDate(
    timestamp: Long
): String {

    val formatter =
        SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        )

    return formatter.format(
        Date(timestamp)
    )
}

