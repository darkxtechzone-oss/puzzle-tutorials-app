package com.tutorials.puzzle

import android.content.Context
import android.graphics.drawable.GradientDrawable

object Ui {
    const val BG = "#0F0F1A"
    const val CARD = "#1C1C2E"
    const val CARD2 = "#2A2A42"
    const val PURPLE = "#7C4DFF"
    const val ORANGE = "#FF8A00"
    const val MUTED = "#9A9AB5"

    fun dp(c: Context, v: Int): Int = (v * c.resources.displayMetrics.density).toInt()

    fun rounded(c: Context, color: Int, radiusDp: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(c, radiusDp).toFloat()
        }
}
