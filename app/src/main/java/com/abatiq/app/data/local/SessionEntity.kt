package com.abatiq.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A played 60-second session of "Tebak Huruf". The row is inserted when the
 * session starts and updated with the final stats when the timer ends.
 *
 * v2 schema (Phase 1 revised brief):
 *   - [durationSeconds] is the configured session length (always 60 in Phase 1).
 *   - [finalScore] is the running total of +10 per correct tap.
 *   - [correctTaps] / [totalTaps] are recorded for the parent summary.
 *   - [completedAtEpochMs] is non-null when the timer reached 0.
 */
@Entity(
    tableName = "sessions",
    indices = [Index(value = ["startedAtEpochMs"])],
)
data class SessionEntity(
    @PrimaryKey val id: String,
    val moduleId: String,
    val durationSeconds: Int,
    val finalScore: Int,
    val correctTaps: Int,
    val totalTaps: Int,
    val startedAtEpochMs: Long,
    val completedAtEpochMs: Long?,
)

@Entity(
    tableName = "attempts",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId")],
)
data class AttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val targetLetter: String,
    val tappedLetter: String,
    val isCorrect: Boolean,
    val pointsAwarded: Int,
    val timestampEpochMs: Long,
)
