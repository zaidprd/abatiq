package com.abatiq.app.domain

import com.abatiq.app.data.domain.ParentalChallenge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ParentalChallengeTest {

    @Test
    fun `every generated challenge has the correct sum`() {
        repeat(200) { seed ->
            val challenge = ParentalChallenge.generate(Random(seed.toLong()))
            assertEquals(
                "Seed=$seed should produce $challenge with sum $challenge.expected",
                challenge.a + challenge.b,
                challenge.expected,
            )
        }
    }

    @Test
    fun `challenge result stays within adult-friendly band`() {
        repeat(200) { seed ->
            val challenge = ParentalChallenge.generate(Random(seed.toLong()))
            assertTrue(
                "Sum ${challenge.expected} should be in band",
                challenge.expected in ParentalChallenge.MIN_VALUE..ParentalChallenge.MAX_VALUE,
            )
        }
    }

    @Test
    fun `isCorrect accepts only the exact answer`() {
        val challenge = ParentalChallenge(a = 7, b = 5)
        assertTrue(challenge.isCorrect(12))
        assertFalse(challenge.isCorrect(11))
        assertFalse(challenge.isCorrect(13))
        assertFalse(challenge.isCorrect(-1))
    }

    @Test
    fun `two challenges with same seed are equal`() {
        val a = ParentalChallenge.generate(Random(123L))
        val b = ParentalChallenge.generate(Random(123L))
        assertEquals(a, b)
    }
}