package com.example.codevault

import android.content.Context


object RecoveryManager {


    private const val PREFS_NAME =
        "codevault_recovery"


    private const val KEY_HAS_RECOVERY =
        "has_recovery"

    private const val KEY_TEXT =
        "recovery_text"

    private const val KEY_FILE_NAME =
        "recovery_file_name"

    private const val KEY_FILE_URI =
        "recovery_file_uri"

    fun saveRecoveryDraft(
        context: Context,
        text: String,
        fileName: String,
        fileUri: String?
    ) {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        preferences
            .edit()
            .putBoolean(
                KEY_HAS_RECOVERY,
                true
            )
            .putString(
                KEY_TEXT,
                text
            )
            .putString(
                KEY_FILE_NAME,
                fileName
            )
            .putString(
                KEY_FILE_URI,
                fileUri
            )
            .apply()
    }
    fun hasRecoveryDraft(
        context: Context
    ): Boolean {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        return preferences.getBoolean(
            KEY_HAS_RECOVERY,
            false
        )
    }


    fun getRecoveryText(
        context: Context
    ): String {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        return preferences.getString(
            KEY_TEXT,
            ""
        ) ?: ""
    }
    fun getRecoveryFileName(
        context: Context
    ): String {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        return preferences.getString(
            KEY_FILE_NAME,
            "Untitled"
        ) ?: "Untitled"
    }

    fun getRecoveryFileUri(
        context: Context
    ): String? {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        return preferences.getString(
            KEY_FILE_URI,
            null
        )
    }


    fun clearRecoveryDraft(
        context: Context
    ) {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        preferences
            .edit()
            .clear()
            .apply()
    }
}