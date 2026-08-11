package com.example.codevault.ui.home

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.codevault.RecentFile

@Composable
fun HomeScreen(
    recentFiles: List<RecentFile>,
    onNewFile: () -> Unit,
    onOpenFile: () -> Unit,
    onRecentFileClick: (RecentFile) -> Unit,
    onRemoveRecentFile: (RecentFile) -> Unit,
    onClearRecentFiles: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(48.dp)
        )

        Text(
            text = "CodeVault",
            style =
                MaterialTheme.typography
                    .headlineLarge
        )

        Text(
            text = "Modern Mobile Text Editor",
            style =
                MaterialTheme.typography
                    .bodyLarge,
            modifier =
                Modifier.padding(
                    top = 8.dp,
                    bottom = 24.dp
                )
        )

        //NEW / OPEN BUTTONS

        Button(
            onClick = onNewFile
        ) {

            Text("New File")
        }

        Button(
            onClick = onOpenFile,
            modifier =
                Modifier.padding(
                    top = 12.dp
                )
        ) {

            Text("Open File")
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        //RECENT FILES HEADER

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text = "Recent Files",
                style =
                    MaterialTheme.typography
                        .titleLarge,
                fontWeight =
                    FontWeight.SemiBold
            )

            if (recentFiles.isNotEmpty()) {

                TextButton(
                    onClick =
                        onClearRecentFiles
                ) {

                    Text("Clear All")
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        //EMPTY RECENT FILES MESSAGE

        if (recentFiles.isEmpty()) {

            Text(
                text =
                    "No recent files yet.\n" +
                            "Open or save a file and it will appear here.",
                style =
                    MaterialTheme.typography
                        .bodyMedium
            )

        } else {

            //RECENT FILES LIST

            LazyColumn(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                items(
                    items = recentFiles,
                    key = {
                        it.uri
                    }
                ) { recentFile ->

                    RecentFileItem(
                        recentFile =
                            recentFile,

                        onClick = {
                            onRecentFileClick(
                                recentFile
                            )
                        },

                        onRemove = {
                            onRemoveRecentFile(
                                recentFile
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun RecentFileItem(
    recentFile: RecentFile,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 4.dp
                )
                .clickable {
                    onClick()
                }
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        top = 8.dp,
                        bottom = 8.dp,
                        end = 8.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text =
                    recentFile.name,

                style =
                    MaterialTheme.typography
                        .bodyLarge,

                fontWeight =
                    FontWeight.Medium,

                maxLines = 1,

                overflow =
                    TextOverflow.Ellipsis,

                modifier =
                    Modifier
                        .weight(1f)
                        .padding(
                            end = 8.dp
                        )
            )

            TextButton(
                onClick = onRemove
            ) {

                Text("Remove")
            }
        }
    }
}