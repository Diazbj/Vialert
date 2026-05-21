package com.example.myapplication.domain.model

import androidx.compose.ui.graphics.Color

enum class SeverityLevel(
    val label: String,
    val color: Color,
    val bgColor: Color,
    val emoji: String
) {
    LOW("Baja", Color(0xFF10B981), Color(0xFFD1FAE5), "🟢"),
    MEDIUM("Media", Color(0xFFF59E0B), Color(0xFFFEF3C7), "🟡"),
    HIGH("Alta", Color(0xFFEF4444), Color(0xFFFEE2E2), "🔴");

    companion object {
        fun fromVotes(votes: Int): SeverityLevel = when {
            votes >= 15 -> HIGH
            votes >= 5  -> MEDIUM
            else        -> LOW
        }
    }
}
