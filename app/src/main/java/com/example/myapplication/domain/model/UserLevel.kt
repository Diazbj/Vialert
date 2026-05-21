package com.example.myapplication.domain.model

enum class UserLevel(
    val displayName: String,
    val minPoints: Int,
    val maxPoints: Int
) {
    NOVATO("Novato", 0, 99),
    COLABORADOR("Colaborador", 100, 299),
    GUARDIAN("Guardián", 300, 599),
    HEROE_COMUNITARIO("Héroe Comunitario", 600, Int.MAX_VALUE);

    companion object {
        fun fromScore(score: Int): UserLevel {
            return entries.find { score in it.minPoints..it.maxPoints } ?: HEROE_COMUNITARIO
        }
    }
}
