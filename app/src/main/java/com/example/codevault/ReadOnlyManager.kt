package com.example.codevault

import android.content.Context


object ReadOnlyManager {

    private const val PREFS_NAME =
        "codevault_read_only_files"



    fun isReadOnly(
        context: Context,
        fileId: String
    ): Boolean {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        return preferences.getBoolean(
            fileId,
            false
        )
    }



    fun setReadOnly(
        context: Context,
        fileId: String,
        locked: Boolean
    ) {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        preferences
            .edit()
            .putBoolean(
                fileId,
                locked
            )
            .apply()
    }

    fun removeReadOnlyState(
        context: Context,
        fileId: String
    ) {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        preferences
            .edit()
            .remove(
                fileId
            )
            .apply()
    }
}