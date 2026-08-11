package com.example.codevault

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_versions")
data class VersionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val fileUri: String,

    val versionNumber: Int,

    val timestamp: Long,

    val isBaseSnapshot: Boolean,

    val content: String,

    val deltaStart: Int,

    val deltaEnd: Int,

    val insertedText: String
)