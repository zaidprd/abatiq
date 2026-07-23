package com.abatiq.app.audio

/**
 * The exact Indonesian text spoken for feedback prompts is kept centralised so
 * a syar'i reviewer can audit every line. TargetLetter is now backed by the
 * bundled human recording files in `res/raw/alphabet_a.wav` through
 * `alphabet_z.wav`; this catalog remains the fallback text table.
 *
 * Tone rules (per PRD Bab 6.4):
 *   - Always positive and constructive.
 *   - Never "kamu salah" — speak about the action, not the child.
 *   - Islamic phrases (MasyaAllah, Barakallah, Alhamdulillah) used sparingly.
 *
 * Bundled target recordings:
 *   - alphabet_a.wav ... alphabet_z.wav for [GamePrompt.TargetLetter]
 *   - feedback and session-end prompts remain TTS until reviewed recordings are
 *     supplied.
 */
object PromptCatalog {

    fun lineFor(prompt: GamePrompt): String = when (prompt) {
        is GamePrompt.TargetLetter -> "Mana huruf ${prompt.letter}?"
        is GamePrompt.CorrectThenTarget -> "MasyaAllah, benar. Sekarang, mana huruf ${prompt.letter}?"
        is GamePrompt.GentleThenTarget -> "Ayo lanjut. Mana huruf ${prompt.letter}?"
        GamePrompt.CorrectFeedback -> "MasyaAllah, benar! Barakallahu fiik."
        GamePrompt.GentleFeedback -> "Belum tepat, ayo terus mencoba ya."
        GamePrompt.SessionEnd -> "Alhamdulillah, sesi belajar selesai!"
        GamePrompt.AudioTest -> "Tes suara. AbatiQ siap menemani belajar."
    }
}
