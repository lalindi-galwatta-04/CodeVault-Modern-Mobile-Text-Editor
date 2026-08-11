package com.example.codevault

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VersionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: VersionEntity)

    @Query("""
        SELECT *
        FROM file_versions
        WHERE fileUri = :fileUri
        ORDER BY versionNumber ASC
    """)
    suspend fun getVersionsForFile(fileUri: String): List<VersionEntity>

    @Query("""
        DELETE FROM file_versions
        WHERE fileUri = :fileUri
    """)
    suspend fun deleteVersionsForFile(fileUri: String)
}