package com.abatiq.app.util

/**
 * Helper for translating letter state into a spoken description that screen
 * readers can announce. Kept tiny so it can be unit-tested in isolation.
 */
object AccessibilityUtils {

    fun describeLetter(letter: String, isCorrect: Boolean?, isRevealed: Boolean): String {
        return when {
            isRevealed -> "Huruf $letter, jawaban yang benar"
            isCorrect == true -> "Huruf $letter, jawaban benar"
            isCorrect == false -> "Huruf $letter, coba lagi"
            else -> "Huruf $letter"
        }
    }
}