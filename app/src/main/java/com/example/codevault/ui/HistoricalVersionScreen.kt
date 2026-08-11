package com.example.codevault.ui.versionhistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codevault.FileVersion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoricalVersionScreen(
    fileName: String,
    version: FileVersion,
    reconstructedText: String,
    onBackToHistory: () -> Unit,

    /*
     * Restore this historical snapshot into the active editor.
     *
     * This does NOT delete or rewrite version history.
     */
    onRestoreVersion: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // NAVIGATION


        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Button(
                onClick = onBackToHistory
            ) {

                Text("Back to History")
            }

            Button(
                onClick = onRestoreVersion
            ) {

                Text("Restore Version")
            }
        }

        // VERSION INFORMATION


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    bottom = 16.dp
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = "Historical Version",
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
                            top = 6.dp
                        )
                )

                Text(
                    text =
                        "Version ${version.versionNumber}",
                    style =
                        MaterialTheme.typography
                            .titleLarge,
                    fontWeight =
                        FontWeight.SemiBold,
                    modifier =
                        Modifier.padding(
                            top = 12.dp
                        )
                )

                Text(
                    text =
                        "Created: ${
                            formatHistoricalVersionDate(
                                version.createdAt
                            )
                        }",
                    style =
                        MaterialTheme.typography
                            .bodyMedium,
                    modifier =
                        Modifier.padding(
                            top = 4.dp
                        )
                )

                Text(
                    text =
                        if (
                            version.versionNumber == 1
                        ) {

                            "Base snapshot"

                        } else {

                            "Incremental delta snapshot"
                        },
                    style =
                        MaterialTheme.typography
                            .bodySmall,
                    modifier =
                        Modifier.padding(
                            top = 4.dp
                        )
                )

                /*
                 * This label makes the historical state
                 * explicit to the user.
                 */

                Surface(
                    tonalElevation = 2.dp,
                    shape =
                        MaterialTheme.shapes.small,
                    modifier =
                        Modifier.padding(
                            top = 12.dp
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
        }

        HorizontalDivider()

        //HISTORICAL CONTENT


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp
                )
                .verticalScroll(
                    rememberScrollState()
                )
        ) {

            Text(
                text = "Snapshot Content",
                style =
                    MaterialTheme.typography
                        .titleMedium,
                fontWeight =
                    FontWeight.SemiBold
            )

            Surface(
                tonalElevation = 1.dp,
                shape =
                    MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 10.dp,
                        bottom = 16.dp
                    )
            ) {

                Text(
                    text =
                        if (
                            reconstructedText.isEmpty()
                        ) {

                            "(Empty file)"

                        } else {

                            reconstructedText
                        },

                    /*
                     * Monospace makes source code and plain-text
                     * snapshots easier to inspect.
                     */

                    fontFamily =
                        FontFamily.Monospace,

                    style =
                        MaterialTheme.typography
                            .bodyLarge,

                    modifier =
                        Modifier.padding(16.dp)
                )
            }
        }
    }
}


//FORMAT HISTORICAL VERSION DATE


private fun formatHistoricalVersionDate(
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

