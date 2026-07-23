package com.abatiq.app.data.domain

import kotlin.random.Random

/**
 * A single letter card in the current round.
 *
 * The board holds [ROUND_SIZE] cards. Cards are revealed one-by-one with a
 * stagger so children can see each letter arrive. After the player taps a
 * card the ViewModel briefly plays the success or gentle feedback animation
 * on it, then dismisses the whole batch and reveals a new one.
 */
data class LetterCard(
    val letter: String,
    /** Stable identifier for the card across the session, used by the renderer. */
    val id: Long,
    /** Zero-based reveal order (0..ROUND_SIZE-1) used to stagger entry animations. */
    val revealIndex: Int,
    /** Whether the card has been revealed on screen yet. */
    val isRevealed: Boolean,
)

enum class LetterCardFeedback {
    None,
    Correct,
    Gentle,
}

/**
 * Generates the three-letter batches for Phase 1.
 *
 * Behaviour:
 *   - Each batch contains exactly [ROUND_SIZE] unique letters drawn from
 *     [Question.Letters] (A..J in Phase 1).
 *   - The spoken target letter for the round is included in every batch so the
 *     child can always tap a correct option.
 *   - The same batch is reused by the UI until the child taps a card, after
 *     which a fresh batch is generated.
 */
class LetterBatchFactory(
    private val random: Random = Random.Default,
    private val letters: List<String> = Question.Letters,
) {

    /**
     * Build a new batch that includes [targetLetter]. The cards are returned in
     * reveal order so the renderer can stagger them.
     */
    fun nextBatch(targetLetter: String): List<LetterCard> {
        require(targetLetter in letters) { "Target $targetLetter di luar alfabet Phase 1" }
        val picked = mutableListOf(targetLetter)
        picked += letters.filter { it != targetLetter }.shuffled(random).take(ROUND_SIZE - 1)
        picked.shuffle(random)
        return picked.mapIndexed { index, letter ->
            LetterCard(
                letter = letter,
                id = (random.nextLong() and 0x7FFFFFFFFFFFFFFFL),
                revealIndex = index,
                isRevealed = false,
            )
        }
    }

    companion object {
        const val ROUND_SIZE: Int = GameSession.ROUND_SIZE
    }
}
