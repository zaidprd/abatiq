package com.abatiq.app.ui.navigation

/**
 * Type-safe destination set for Phase 1. New screens in later phases extend
 * this sealed hierarchy and add their own entry to [AbatiQNavGraph].
 */
sealed class Destination(val route: String) {
    object Home : Destination("home")
    object Game : Destination("game")
    object Result : Destination("result?score={score}&correct={correct}&total={total}") {
        const val ARG_SCORE = "score"
        const val ARG_CORRECT = "correct"
        const val ARG_TOTAL = "total"
        fun build(score: Int, correctTaps: Int, totalTaps: Int): String =
            "result?score=$score&correct=$correctTaps&total=$totalTaps"
    }
    object ParentalGate : Destination("parental_gate")
    object Settings : Destination("settings")
}
