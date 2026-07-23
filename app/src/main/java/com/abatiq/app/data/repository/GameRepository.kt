package com.abatiq.app.data.repository

import com.abatiq.app.data.domain.Attempt
import com.abatiq.app.data.domain.GameSession
import com.abatiq.app.data.local.AbatiQDatabase
import com.abatiq.app.data.local.AttemptEntity
import com.abatiq.app.data.local.SessionEntity
import kotlin.random.Random

/**
 * Owns the lifecycle of a single 60-second play session. The ViewModel never
 * touches Room directly — it asks the repository to begin, persist taps, and
 * complete the session. Pure-Kotlin session rules stay in
 * [com.abatiq.app.data.domain.GameSession] so the engine is easy to test.
 */
class GameRepository(
    private val database: AbatiQDatabase,
    private val random: Random = Random.Default,
    private val clock: () -> Long = { System.currentTimeMillis() },
) {

    private val dao = database.sessionDao()

    /**
     * Allocate a fresh session row, generate a [GameSession] against it, and
     * return both so the ViewModel can call `.tap(...)` on the engine.
     */
    suspend fun startSession(moduleId: String = MODULE_ID): SessionHandle {
        val session = GameSession(random = random, clock = clock)
        dao.upsertSession(
            SessionEntity(
                id = session.id,
                moduleId = moduleId,
                durationSeconds = session.durationSeconds,
                finalScore = 0,
                correctTaps = 0,
                totalTaps = 0,
                startedAtEpochMs = session.startedAtEpochMs,
                completedAtEpochMs = null,
            )
        )
        return SessionHandle(session = session)
    }

    /**
     * Persist a single tap. The audit trail is preserved for parent reports.
     */
    suspend fun recordAttempt(attempt: Attempt) {
        dao.insertAttempts(
            listOf(
                AttemptEntity(
                    sessionId = attempt.sessionId,
                    targetLetter = attempt.targetLetter,
                    tappedLetter = attempt.tappedLetter,
                    isCorrect = attempt.isCorrect,
                    pointsAwarded = attempt.pointsAwarded,
                    timestampEpochMs = attempt.timestampMillis,
                )
            )
        )
    }

    /**
     * Finalize the session. The ViewModel is responsible for calling
     * [GameSession.end] first so the row carries the correct totals.
     */
    suspend fun completeSession(summary: GameSession.Summary) {
        dao.completeSession(
            sessionId = summary.sessionId,
            finalScore = summary.finalScore,
            correctTaps = summary.correctTaps,
            totalTaps = summary.totalTaps,
            completedAt = summary.endedAtEpochMs,
        )
    }

    suspend fun resetProgress() {
        dao.deleteAllSessions()
    }

    data class SessionHandle(
        val session: GameSession,
    )

    companion object {
        const val MODULE_ID = "tebak_huruf"
    }
}
