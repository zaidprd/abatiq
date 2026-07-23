package com.abatiq.app.domain

import com.abatiq.app.data.domain.GameSession
import com.abatiq.app.data.domain.LetterBatchFactory
import com.abatiq.app.data.domain.Question
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TimedBalloonGameTest {

    @Test
    fun `session lasts two minutes by default`() {
        var now = 1_000L
        val session = GameSession(random = Random(1), clock = { now })

        assertEquals(120, session.durationSeconds)
        assertEquals(120, session.remainingSeconds(now))

        now += 119_001L
        assertEquals(1, session.remainingSeconds(now))

        now += 999L
        assertEquals(0, session.remainingSeconds(now))
    }

    @Test
    fun `correct tap gives ten points and advances target`() {
        val session = GameSession(random = Random(2), clock = { 10L })
        val target = session.targetLetter

        val result = session.tap(target, listOf("B", "C", target))

        assertTrue(result.isCorrect)
        assertEquals(10, result.pointsAwarded)
        assertEquals(10, session.score)
        assertEquals(1, session.correctTaps)
        assertEquals(1, session.totalTaps)
        assertNotEquals(target, result.nextTarget)
    }

    @Test
    fun `wrong tap gives no penalty and still advances target`() {
        val session = GameSession(random = Random(3), clock = { 20L })
        val target = session.targetLetter
        val wrong = Question.Letters.first { it != target }

        val result = session.tap(wrong, listOf("E", "F", "G"))

        assertFalse(result.isCorrect)
        assertEquals(0, result.pointsAwarded)
        assertEquals(0, session.score)
        assertEquals(0, session.correctTaps)
        assertEquals(1, session.totalTaps)
        assertNotEquals(target, result.nextTarget)
    }

    @Test
    fun `letter batch always contains the requested target`() {
        val factory = LetterBatchFactory(random = Random(4))

        repeat(20) {
            val target = Question.Letters.random()
            val batch = factory.nextBatch(target)
            assertEquals(LetterBatchFactory.ROUND_SIZE, batch.size)
            assertTrue(batch.any { it.letter == target })
            assertEquals(batch.size, batch.map { it.letter }.distinct().size)
            assertEquals((0 until batch.size).toList(), batch.map { it.revealIndex })
            batch.forEach { card ->
                assertNotNull(card.id)
                assertFalse(card.isRevealed)
            }
        }
    }
}
