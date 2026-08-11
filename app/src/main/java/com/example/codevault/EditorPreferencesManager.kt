package com.example.codevault

import android.content.Context


object EditorPreferencesManager {

    private const val PREFS_NAME =
        "codevault_editor_preferences"

    private const val KEY_WORD_WRAP =
        "word_wrap_enabled"



    fun isWordWrapEnabled(
        context: Context
    ): Boolean {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        return preferences.getBoolean(
            KEY_WORD_WRAP,
            true
        )
    }



    fun setWordWrapEnabled(
        context: Context,
        enabled: Boolean
    ) {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        preferences
            .edit()
            .putBoolean(
                KEY_WORD_WRAP,
                enabled
            )
            .apply()
    }
}