package com.example

import com.example.data.ai.AiResult
import com.example.data.ai.PlaceholderAiRepository
import com.example.data.model.BadgeTier
import com.example.data.repository.JodivaSeedCatalog
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun badgeTier_progression_matches_thresholds() {
        assertEquals(BadgeTier.BRONZE, BadgeTier.forPoints(0))
        assertEquals(BadgeTier.SILVER, BadgeTier.forPoints(150))
        assertEquals(BadgeTier.GOLD, BadgeTier.forPoints(350))
        assertEquals(BadgeTier.DIAMOND, BadgeTier.forPoints(900))
        assertEquals(BadgeTier.LEGEND, BadgeTier.forPoints(1600))
    }

    @Test
    fun placeholderAi_returns_labeled_sample_and_notice() = runTest {
        val repo = PlaceholderAiRepository()
        val translated = repo.translateText("Hello world", "Hindi")
        assertTrue(translated.isPlaceholder)
        assertEquals(AiResult.FIREBASE_REQUIRED_NOTICE, translated.notice)
        assertTrue(translated.text.contains("Hello world"))

        val help = repo.askHelpAssistant("How do I recover my password?", "aarav")
        assertTrue(help.isPlaceholder)
        assertEquals(AiResult.FIREBASE_REQUIRED_NOTICE, help.notice)
    }

    @Test
    fun seedCatalog_quizBank_has_ten_valid_questions() {
        assertEquals(10, JodivaSeedCatalog.quizBank.size)
        JodivaSeedCatalog.quizBank.forEach { q ->
            assertTrue(q.options.size == 4)
            assertTrue(q.correctIndex in 0..3)
        }
    }
}
