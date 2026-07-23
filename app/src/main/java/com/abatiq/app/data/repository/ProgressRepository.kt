package com.abatiq.app.data.repository

import com.abatiq.app.data.domain.ProgressSummary
import com.abatiq.app.data.local.AbatiQDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Derives a [ProgressSummary] from Room so the home screen only needs to
 * observe one Flow.
 */
class ProgressRepository(database: AbatiQDatabase) {

    private val dao = database.sessionDao()

    val summary: Flow<ProgressSummary> = combine(
        dao.observeCompletedSessionCount(),
        dao.observeBestScore(),
        dao.observeLastScore(),
        dao.observeTotalCorrectTaps(),
        dao.observeTotalTaps(),
    ) { sessions, best, last, correct, total ->
        ProgressSummary(
            completedSessions = sessions,
            bestScore = best ?: 0,
            lastScore = last ?: 0,
            totalCorrectTaps = correct,
            totalTaps = total,
        )
    }
}
