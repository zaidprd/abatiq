package com.abatiq.app.data.domain

/**
 * Persisted record of a single tap inside a Phase 1 session.
 */
data class Attempt(
    val sessionId: String,
    val targetLetter: String,
    val tappedLetter: String,
    val isCorrect: Boolean,
    val pointsAwarded: Int,
    val timestampMillis: Long,
)
