package com.abatiq.app.data.domain


import kotlin.random.Random

/**
 * One of exactly three moving letter balloons shown during Phase 1.
 * Coordinates are stored in pixels because Compose reports the measured arena
 * in pixels and drives the simulation frame by frame.
 */
data class Balloon(
    val letter: String,
    val xFraction: Float,
    val topY: Float,
    val speed: Float,
    val id: Long,
    val state: BalloonState = BalloonState.Active,
)

enum class BalloonState {
    Active,
    TappedCorrect,
    TappedWrong,
}

/**
 * Owns a fixed three-letter field.
 *
 * Important gameplay invariant: motion never creates a new letter. Existing
 * letters rise slowly and wait at the top until the child chooses one. A tap
 * completes the round: all three old letters disappear together and one new
 * three-letter batch starts from below.
 */
class BalloonField(
    private val random: Random = Random.Default,
    private val letters: List<String> = Question.Letters,
) {
    private var balloonWidthPx: Float = 200f
    private var balloonHeightPx: Float = 240f
    private val balloonsInternal = mutableListOf<Balloon>()
    private var nextId = 1L

    val balloons: List<Balloon> get() = balloonsInternal
    val visibleLetters: List<String> get() = balloonsInternal.map { it.letter }

    fun updateMetrics(widthPx: Float, heightPx: Float) {
        balloonWidthPx = widthPx.coerceAtLeast(1f)
        balloonHeightPx = heightPx.coerceAtLeast(1f)
    }

    /** Create exactly three unique letters, including the spoken target. */
    fun populate(
        targetLetter: String,
        fieldWidth: Float,
        fieldHeight: Float,
        count: Int = FIELD_COUNT,
    ) {
        require(count == FIELD_COUNT) { "Phase 1 always displays exactly three letters" }
        balloonsInternal.clear()
        nextId = 1L

        val initialLetters = buildList {
            add(targetLetter)
            addAll(letters.filter { it != targetLetter }.shuffled(random).take(FIELD_COUNT - 1))
        }.shuffled(random)

        val lanes = listOf(0.20f, 0.50f, 0.80f)
        val sharedTop = fieldHeight * 0.68f
        initialLetters.forEachIndexed { index, letter ->
            balloonsInternal += newBalloon(
                letter = letter,
                xFraction = lanes[index],
                topY = sharedTop + (random.nextFloat() - 0.5f) * fieldHeight * 0.035f,
                fieldHeight = fieldHeight,
            )
        }
    }

    /**
     * Move the same three letters upward. At the upper edge they wait; this
     * method never recycles or spawns a letter.
     */
    fun step(deltaSeconds: Float) {
        if (deltaSeconds <= 0f) return
        balloonsInternal.indices.forEach { index ->
            val balloon = balloonsInternal[index]
            balloonsInternal[index] = balloon.copy(
                topY = (balloon.topY + balloon.speed * deltaSeconds).coerceAtLeast(0f),
            )
        }
    }

    /**
     * Complete the current round after any tap. All three old letters are
     * removed together and replaced by exactly three new, unique A–J letters
     * starting almost level below the arena. No old and new batch coexist.
     */
    fun replaceBatchAfterTap(fieldHeight: Float): List<Balloon> {
        val newLetters = letters.shuffled(random).take(FIELD_COUNT)
        val lanes = listOf(0.20f, 0.50f, 0.80f)
        val sharedTop = fieldHeight + balloonHeightPx * 0.15f

        balloonsInternal.clear()
        newLetters.forEachIndexed { index, letter ->
            balloonsInternal += newBalloon(
                letter = letter,
                xFraction = lanes[index],
                topY = sharedTop + (random.nextFloat() - 0.5f) * fieldHeight * 0.02f,
                fieldHeight = fieldHeight,
            )
        }
        return balloonsInternal.toList()
    }

    private fun newBalloon(
        letter: String,
        xFraction: Float,
        topY: Float,
        fieldHeight: Float,
    ): Balloon {
        val balloon = Balloon(
            letter = letter,
            xFraction = xFraction,
            topY = topY,
            speed = -fieldHeight * (0.035f + random.nextFloat() * 0.015f),
            id = nextId++,
        )
        return balloon
    }



    companion object {
        const val FIELD_COUNT = 3
    }
}
