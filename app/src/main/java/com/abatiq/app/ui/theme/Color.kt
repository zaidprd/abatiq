package com.abatiq.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Sunlit learning garden palette. Warm cream paper, deep forest green for
 * structure, terracotta for soft negative feedback, honey for reward, mint,
 * sky blue, and lavender for playful letter balloons.
 */
object AbatiQColors {
    val Cream = Color(0xFFFFF6E4)
    val CreamDim = Color(0xFFF4E6CC)
    val Paper = Color(0xFFFFFBF1)
    val Forest = Color(0xFF1F4A36)
    val ForestDark = Color(0xFF163626)
    val Terracotta = Color(0xFFC4644A)
    val TerracottaSoft = Color(0xFFE9A98E)
    val Honey = Color(0xFFE6B73A)
    val HoneyDeep = Color(0xFFC89422)
    val Mint = Color(0xFFC9E2C2)
    val MintSurface = Color(0xFFE4EFD9)
    val SkyBlue = Color(0xFF9DD7E8)
    val Lavender = Color(0xFFC9B7E8)

    /**
     * Balloon pastel palette — matches the slot layout used by
     * [com.abatiq.app.ui.components.BalloonView]:
     *
     *  - Slot 0 (left):  body PastelBlue   with letter in [PastelPink]
     *  - Slot 1 (mid):   body PastelPink   with letter in [HoneyDeep]
     *  - Slot 2 (right): body PastelYellow with letter in [PastelBlue]
     *
     * These are deliberately soft (felt-like, doff) and coordinate with the
     * existing warm cream/forest palette. They never appear with full
     * saturation so the background TK-classroom scene stays calm.
     */
    val PastelBlue = Color(0xFFB6D8F2)
    val PastelPink = Color(0xFFF6C6CF)
    val PastelYellow = Color(0xFFFFE9A8)
    val TetherGrey = Color(0xFFB8B0A4)

    val Ink = Color(0xFF1F1B16)
    val InkSoft = Color(0xFF3A332A)
    val OnDark = Color(0xFFFFF6E4)
    val OnLight = Color(0xFF1F1B16)
}