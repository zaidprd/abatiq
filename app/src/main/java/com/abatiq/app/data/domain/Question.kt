package com.abatiq.app.data.domain

/**
 * Phase 1 alphabet. The session engine no longer consumes a list of
 * pre-generated questions — it picks a target letter on each tap — but the
 * alphabet subset itself is the A..J constant the brief mandates.
 *
 * Kept as a data class so legacy references compile and as a documented home
 * for the alphabet constant that [GameSession] and [BalloonField] share.
 */
data class Question(
    val id: String,
    val correctLetter: String,
) {
    companion object {
        const val ALPHABET_SIZE = 10

        /** Letters used in Phase 1 — uppercase Latin A–J. */
        val Letters: List<String> = ('A'..'J').map { it.toString() }
    }
}
