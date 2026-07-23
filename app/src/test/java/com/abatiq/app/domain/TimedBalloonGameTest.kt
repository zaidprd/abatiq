package com.abatiq.app.domain

import com.abatiq.app.data.domain.BalloonField
import com.abatiq.app.data.domain.GameSession
import com.abatiq.app.data.domain.Question
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TimedBalloonGameTest {

    @Test
    fun `session lasts sixty seconds`() {
        var now = 1_000L
        val session = GameSession(random = Random(1), clock = { now })

        assertEquals(60, session.durationSeconds)
        assertEquals(60, session.remainingSeconds(now))

        now += 59_001L
        assertEquals(1, session.remainingSeconds(now))

        now += 999L
        assertEquals(0, session.remainingSeconds(now))
    }

    @Test
    fun `correct tap gives ten points and advances target`() {
        val session = GameSession(random = Random(2), clock = { 10L })
        val target = session.targetLetter

        val result = session.tap(target, listOf("B", "C", "D"))

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
    fun `three-letter batch stays until tap then all three are replaced`() {
        val field = BalloonField(random = Random(4))
        field.updateMetrics(widthPx = 216f, heightPx = 264f)
        field.populate(targetLetter = "G", fieldWidth = 1080f, fieldHeight = 1600f)

        assertEquals(3, field.balloons.size)
        assertEquals(3, field.balloons.map { it.letter }.distinct().size)
        assertTrue(field.balloons.all { it.letter in Question.Letters })
        assertTrue(field.balloons.any { it.letter == "G" })
        assertTrue(field.balloons.maxOf { it.topY } - field.balloons.minOf { it.topY } < 100f)

        val originalIds = field.balloons.map { it.id }
        repeat(300) { field.step(0.1f) }
        assertEquals(originalIds, field.balloons.map { it.id })
        assertEquals(3, field.balloons.size)

        field.replaceBatchAfterTap(fieldHeight = 1600f)

        assertEquals(3, field.balloons.size)
        assertEquals(0, field.balloons.count { it.id in originalIds })
        assertEquals(3, field.balloons.map { it.letter }.distinct().size)
        assertTrue(field.balloons.all { it.letter in Question.Letters })
    }
}
