package com.example.codevault

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

//VERSION HISTORY MANAGER

data class TextDelta(
    val start: Int,
    val deletedText: String,
    val insertedText: String
)

data class FileVersion(
    val versionNumber: Int,
    val createdAt: Long,
    val baseText: String?,
    val delta: TextDelta?
)

object VersionHistoryManager {

    private const val PREFS_NAME =
        "codevault_version_history"

    // CREATE A DELTA


    fun createDelta(
        oldText: String,
        newText: String
    ): TextDelta {

        var prefixLength = 0

        val minimumLength =
            minOf(
                oldText.length,
                newText.length
            )

        while (
            prefixLength < minimumLength &&
            oldText[prefixLength] ==
            newText[prefixLength]
        ) {

            prefixLength++
        }

        var oldSuffixIndex =
            oldText.length - 1

        var newSuffixIndex =
            newText.length - 1

        while (
            oldSuffixIndex >= prefixLength &&
            newSuffixIndex >= prefixLength &&
            oldText[oldSuffixIndex] ==
            newText[newSuffixIndex]
        ) {

            oldSuffixIndex--

            newSuffixIndex--
        }

        val deletedText =
            if (
                oldSuffixIndex >=
                prefixLength
            ) {

                oldText.substring(
                    prefixLength,
                    oldSuffixIndex + 1
                )

            } else {

                ""
            }

        val insertedText =
            if (
                newSuffixIndex >=
                prefixLength
            ) {

                newText.substring(
                    prefixLength,
                    newSuffixIndex + 1
                )

            } else {

                ""
            }

        return TextDelta(
            start =
                prefixLength,

            deletedText =
                deletedText,

            insertedText =
                insertedText
        )
    }

    /*

      APPLY A DELTA

      Reconstructs the next version from the previous version.

     */

    fun applyDelta(
        originalText: String,
        delta: TextDelta
    ): String {

        val start =
            delta.start.coerceIn(
                0,
                originalText.length
            )

        val deleteEnd =
            (
                    start +
                            delta.deletedText.length
                    ).coerceAtMost(
                    originalText.length
                )

        return buildString {

            append(
                originalText.substring(
                    0,
                    start
                )
            )

            append(
                delta.insertedText
            )

            append(
                originalText.substring(
                    deleteEnd
                )
            )
        }
    }

    // CREATE VERSION

    fun createVersion(
        context: Context,
        fileId: String,
        currentText: String
    ): FileVersion {

        val existingVersions =
            getVersions(
                context = context,
                fileId = fileId
            )

        val newVersion =
            if (
                existingVersions.isEmpty()
            ) {

                FileVersion(
                    versionNumber = 1,

                    createdAt =
                        System.currentTimeMillis(),

                    baseText =
                        currentText,

                    delta = null
                )

            } else {

                val previousText =
                    reconstructVersion(
                        versions =
                            existingVersions,

                        versionNumber =
                            existingVersions
                                .last()
                                .versionNumber
                    )

                val delta =
                    createDelta(
                        oldText =
                            previousText,

                        newText =
                            currentText
                    )

                FileVersion(
                    versionNumber =
                        existingVersions
                            .last()
                            .versionNumber + 1,

                    createdAt =
                        System.currentTimeMillis(),

                    baseText = null,

                    delta = delta
                )
            }

        val updatedVersions =
            existingVersions +
                    newVersion

        saveVersions(
            context = context,
            fileId = fileId,
            versions =
                updatedVersions
        )

        return newVersion
    }

    //RECONSTRUCT A VERSION


    fun reconstructVersion(
        versions: List<FileVersion>,
        versionNumber: Int
    ): String {

        if (versions.isEmpty()) {

            return ""
        }

        val sortedVersions =
            versions.sortedBy {
                it.versionNumber
            }

        var reconstructedText =
            sortedVersions
                .firstOrNull()
                ?.baseText
                ?: ""

        for (
        version in sortedVersions
        ) {

            if (
                version.versionNumber == 1
            ) {

                if (
                    version.versionNumber ==
                    versionNumber
                ) {

                    return reconstructedText
                }

                continue
            }

            if (
                version.versionNumber >
                versionNumber
            ) {

                break
            }

            val delta =
                version.delta

            if (delta != null) {

                reconstructedText =
                    applyDelta(
                        originalText =
                            reconstructedText,

                        delta =
                            delta
                    )
            }

            if (
                version.versionNumber ==
                versionNumber
            ) {

                return reconstructedText
            }
        }

        return reconstructedText
    }

    //GET ALL VERSIONS FOR A FILE

    fun getVersions(
        context: Context,
        fileId: String
    ): List<FileVersion> {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        val storedJson =
            preferences.getString(
                fileId,
                null
            )
                ?: return emptyList()

        return try {

            val jsonArray =
                JSONArray(
                    storedJson
                )

            val versions =
                mutableListOf<FileVersion>()

            for (
            index in
            0 until jsonArray.length()
            ) {

                val versionObject =
                    jsonArray.getJSONObject(
                        index
                    )

                val deltaObject =
                    versionObject
                        .optJSONObject(
                            "delta"
                        )

                val delta =
                    if (
                        deltaObject != null
                    ) {

                        TextDelta(
                            start =
                                deltaObject
                                    .getInt(
                                        "start"
                                    ),

                            deletedText =
                                deltaObject
                                    .getString(
                                        "deletedText"
                                    ),

                            insertedText =
                                deltaObject
                                    .getString(
                                        "insertedText"
                                    )
                        )

                    } else {

                        null
                    }

                val baseText =
                    if (
                        versionObject
                            .isNull(
                                "baseText"
                            )
                    ) {

                        null

                    } else {

                        versionObject
                            .getString(
                                "baseText"
                            )
                    }

                versions.add(

                    FileVersion(
                        versionNumber =
                            versionObject
                                .getInt(
                                    "versionNumber"
                                ),

                        createdAt =
                            versionObject
                                .getLong(
                                    "createdAt"
                                ),

                        baseText =
                            baseText,

                        delta =
                            delta
                    )
                )
            }

            versions.sortedBy {
                it.versionNumber
            }

        } catch (
            exception: Exception
        ) {

            exception
                .printStackTrace()

            emptyList()
        }
    }

    // SAVE VERSION METADATA


    private fun saveVersions(
        context: Context,
        fileId: String,
        versions: List<FileVersion>
    ) {

        val jsonArray =
            JSONArray()

        versions.forEach {
                version ->

            val versionObject =
                JSONObject()

            versionObject.put(
                "versionNumber",
                version.versionNumber
            )

            versionObject.put(
                "createdAt",
                version.createdAt
            )

            if (
                version.baseText != null
            ) {

                versionObject.put(
                    "baseText",
                    version.baseText
                )

            } else {

                versionObject.put(
                    "baseText",
                    JSONObject.NULL
                )
            }

            if (
                version.delta != null
            ) {

                val deltaObject =
                    JSONObject()

                deltaObject.put(
                    "start",
                    version.delta.start
                )

                deltaObject.put(
                    "deletedText",
                    version
                        .delta
                        .deletedText
                )

                deltaObject.put(
                    "insertedText",
                    version
                        .delta
                        .insertedText
                )

                versionObject.put(
                    "delta",
                    deltaObject
                )

            } else {

                versionObject.put(
                    "delta",
                    JSONObject.NULL
                )
            }

            jsonArray.put(
                versionObject
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
                fileId,
                jsonArray.toString()
            )
            .apply()
    }
}