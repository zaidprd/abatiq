package com.abatiq.app.data.domain

import kotlin.random.Random

/**
 * A tiny two-operand addition challenge used to gate the settings screen.
 * Pure data, no Android dependency.
 */
data class ParentalChallenge(
    val a: Int,
    val b: Int,
) {
    val expected: Int = a + b
    val prompt: String = "$a + $b"

    fun isCorrect(answer: Int): Boolean = answer == expected

    companion object {
        const val MIN_VALUE = 5
        const val MAX_VALUE = 30
        private const val MIN_OPERAND = 2
        private const val MAX_OPERAND = 12

        /**
         * Produce a fresh challenge where the result stays inside
         * [[MIN_VALUE], [MAX_VALUE]] so it is approachable for any literate
         * adult.
         */
        fun generate(random: Random = Random.Default): ParentalChallenge {
            val min = MIN_VALUE
            val max = MAX_VALUE
            // Pick `a` so that even when `b` is at its minimum, the sum
            // clears [min].
            val aRangeLow = MIN_OPERAND
            val aRangeHigh = (MAX_OPERAND - MIN_OPERAND).coerceAtLeast(1)
            val a = random.nextInt(aRangeLow, aRangeLow + aRangeHigh)
            // b must satisfy: a + b in [min, max]. The lower bound enforces
            // "at least MIN_VALUE", the upper bound enforces "at most MAX_VALUE".
            val bLow = (min - a).coerceAtLeast(MIN_OPERAND)
            val bHigh = (max - a).coerceAtLeast(bLow + 1)
            val b = random.nextInt(bLow, bHigh)
            check(a + b in min..max) {
                "Challenge $a + $b must lie in $min..$max"
            }
            return ParentalChallenge(a, b)
        }
    }
}