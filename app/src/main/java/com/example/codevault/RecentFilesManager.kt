package com.example.codevault

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

data class RecentFile(
    val name: String,
    val uri: String
)

object RecentFilesManager {

    private const val PREFS_NAME =
        "codevault_recent_files"

    private const val KEY_RECENT_FILES =
        "recent_files"

    private const val MAX_RECENT_FILES = 10

    //Add a file to the recent-files list
    fun addRecentFile(
        context: Context,
        fileName: String,
        fileUri: Uri
    ) {

        val recentFiles =
            getRecentFiles(context)
                .toMutableList()

        recentFiles.removeAll {
            it.uri == fileUri.toString()
        }

        recentFiles.add(
            index = 0,
            element = RecentFile(
                name = fileName,
                uri = fileUri.toString()
            )
        )

        //Keep only the newest 10 files
        val limitedFiles =
            recentFiles.take(
                MAX_RECENT_FILES
            )

        saveRecentFiles(
            context = context,
            files = limitedFiles
        )
    }

    //Return all remembered recent files
    fun getRecentFiles(
        context: Context
    ): List<RecentFile> {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        val json =
            preferences.getString(
                KEY_RECENT_FILES,
                null
            ) ?: return emptyList()

        return try {

            val jsonArray =
                JSONArray(json)

            val files =
                mutableListOf<RecentFile>()

            for (
            index in 0 until jsonArray.length()
            ) {

                val item =
                    jsonArray.getJSONObject(
                        index
                    )

                files.add(
                    RecentFile(
                        name =
                            item.getString(
                                "name"
                            ),

                        uri =
                            item.getString(
                                "uri"
                            )
                    )
                )
            }

            files

        } catch (
            exception: Exception
        ) {

            emptyList()
        }
    }

    //Remove one file from Recent Files
    fun removeRecentFile(
        context: Context,
        fileUri: String
    ) {

        val updatedFiles =
            getRecentFiles(context)
                .filterNot {
                    it.uri == fileUri
                }

        saveRecentFiles(
            context = context,
            files = updatedFiles
        )
    }

    //Clear the entire Recent Files list
    fun clearRecentFiles(
        context: Context
    ) {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        preferences
            .edit()
            .remove(
                KEY_RECENT_FILES
            )
            .apply()
    }

    //Save the recent-files list as JSON
    private fun saveRecentFiles(
        context: Context,
        files: List<RecentFile>
    ) {

        val jsonArray =
            JSONArray()

        files.forEach { file ->

            val jsonObject =
                JSONObject()

            jsonObject.put(
                "name",
                file.name
            )

            jsonObject.put(
                "uri",
                file.uri
            )

            jsonArray.put(
                jsonObject
            )
        }

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        preferences
            .edit()
            .putString(
                KEY_RECENT_FILES,
                jsonArray.toString()
            )
            .apply()
    }
}