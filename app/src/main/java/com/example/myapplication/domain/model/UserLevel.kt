package com.example.myapplication.domain.model

enum class UserLevel(
    val displayName: String,
    val minPoints: Int,
    val maxPoints: Int
) {
    NIVEL_1("Iniciante", 0, 100),
    NIVEL_2("Colaborador", 101, 200),
    NIVEL_3("Héroe Comunitario", 201, 300),
    NIVEL_4("Vigilante Urbano", 301, 400),
    NIVEL_5("Protector", 401, 500),
    NIVEL_6("Guardián de Ciudad", 501, 600),
    NIVEL_7("Líder de Barrio", 601, 700),
    NIVEL_8("Maestro Vial", 701, 800),
    NIVEL_9("Leyenda Local", 801, 900),
    NIVEL_10("Centinela Supremo", 901, 1000);

    companion object {
        fun fromScore(score: Int): UserLevel {
            return entries.find { score in it.minPoints..it.maxPoints } ?: entries.last()
        }
    }
}
