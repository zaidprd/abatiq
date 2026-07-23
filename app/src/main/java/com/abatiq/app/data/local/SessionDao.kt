package com.abatiq.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: SessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempts(attempts: List<AttemptEntity>)

    @Query(
        """
        UPDATE sessions
        SET finalScore = :finalScore,
            correctTaps = :correctTaps,
            totalTaps = :totalTaps,
            completedAtEpochMs = :completedAt
        WHERE id = :sessionId
        """
    )
    suspend fun completeSession(
        sessionId: String,
        finalScore: Int,
        correctTaps: Int,
        totalTaps: Int,
        completedAt: Long,
    )

    @Query("SELECT COUNT(*) FROM sessions WHERE completedAtEpochMs IS NOT NULL")
    fun observeCompletedSessionCount(): Flow<Int>

    @Query(
        """
        SELECT MAX(finalScore) FROM sessions
        WHERE completedAtEpochMs IS NOT NULL
        """
    )
    fun observeBestScore(): Flow<Int?>

    @Query(
        """
        SELECT finalScore FROM sessions
        WHERE completedAtEpochMs IS NOT NULL
        ORDER BY completedAtEpochMs DESC LIMIT 1
        """
    )
    fun observeLastScore(): Flow<Int?>

    @Query(
        """
        SELECT IFNULL(SUM(correctTaps), 0) FROM sessions
        WHERE completedAtEpochMs IS NOT NULL
        """
    )
    fun observeTotalCorrectTaps(): Flow<Int>

    @Query(
        """
        SELECT IFNULL(SUM(totalTaps), 0) FROM sessions
        WHERE completedAtEpochMs IS NOT NULL
        """
    )
    fun observeTotalTaps(): Flow<Int>

    @Query("DELETE FROM sessions")
    suspend fun deleteAllSessions()
}
