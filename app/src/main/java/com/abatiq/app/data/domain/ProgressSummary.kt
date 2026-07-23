package com.abatiq.app.data.domain

/**
 * Lightweight summary the home screen shows. Drawn from Room; the domain model
 * is deliberately a single immutable value so the ViewModel just forwards it.
 */
data class ProgressSummary(
    val completedSessions: Int,
    val bestScore: Int,
    val lastScore: Int,
    val totalCorrectTaps: Int,
    val totalTaps: Int,
) {
    companion object {
        val Empty = ProgressSummary(
            completedSessions = 0,
            bestScore = 0,
            lastScore = 0,
            totalCorrectTaps = 0,
            totalTaps = 0,
        )
    }
}
