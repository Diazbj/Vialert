package com.example.myapplication.data.service

import android.util.Log
import com.example.myapplication.BuildConfig
import com.example.myapplication.domain.model.Report
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import javax.inject.Inject
import javax.inject.Singleton

data class AiReportAnalysis(
    val recommendation: String,   // "VERIFICAR" o "RECHAZAR"
    val confidence: String,       // "Alta", "Media", "Baja"
    val keyPoints: List<String>,  // Puntos clave del análisis
    val reasoning: String,        // Justificación de la IA
    val isLoading: Boolean = false,
    val error: String? = null
)

@Singleton
class GeminiAiService @Inject constructor() {

    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    fun createAssistantChat(): Chat = model.startChat(
        history = listOf(
            content(role = "user") {
                text(
                    "Eres el asistente virtual de Vialert, una app ciudadana para reportar problemas " +
                    "de infraestructura y seguridad vial en Colombia. " +
                    "Ayudas a los usuarios con: uso de la app, reportes de vías, alumbrado, movilidad y seguridad vial. " +
                    "Responde siempre en español, de forma amigable y concisa (máximo 3 oraciones)."
                )
            },
            content(role = "model") {
                text("Entendido. Soy el asistente de Vialert, listo para ayudarte con cualquier duda sobre la app o la seguridad vial.")
            }
        )
    )

    suspend fun sendChatMessage(chat: Chat, message: String): String {
        return try {
            chat.sendMessage(message).text ?: "No pude procesar tu pregunta. Intenta de nuevo."
        } catch (e: Exception) {
            Log.e("GeminiAI", "Error chat: ${e.message}", e)
            "Error al conectar con el asistente. Verifica tu conexión."
        }
    }

    suspend fun analyzeReport(report: Report): AiReportAnalysis {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            Log.e("GeminiAI", "API key vacía — revisa local.properties y haz Clean + Rebuild")
            return buildFallbackAnalysis(report).copy(error = "API key no configurada")
        }
        return try {
            val prompt = buildPrompt(report)
            val response = model.generateContent(prompt)
            val text = response.text ?: return buildFallbackAnalysis(report)
            parseResponse(text, report)
        } catch (e: Exception) {
            Log.e("GeminiAI", "Error Gemini: ${e::class.simpleName} — ${e.message}", e)
            buildFallbackAnalysis(report).copy(error = "${e::class.simpleName}: ${e.message}")
        }
    }

    private fun buildPrompt(report: Report): String = """
        Eres un asistente de moderación de reportes ciudadanos para la aplicación Vialert.
        Analiza el siguiente reporte y determina si debe ser VERIFICADO o RECHAZADO.

        REPORTE:
        - Título: ${report.title}
        - Categoría: ${report.type}
        - Descripción: ${report.description}
        - Votos de importancia: ${report.important}
        - Ubicación: Lat ${report.location.latitude}, Lng ${report.location.longitude}

        Responde en el siguiente formato JSON (solo el JSON, sin markdown):
        {
          "recommendation": "VERIFICAR" o "RECHAZAR",
          "confidence": "Alta" o "Media" o "Baja",
          "keyPoints": ["punto1", "punto2", "punto3"],
          "reasoning": "Justificación breve"
        }

        Criterios para VERIFICAR: descripción clara, categoría coherente, problema real de infraestructura o seguridad vial.
        Criterios para RECHAZAR: descripción vaga, contenido inapropiado, no relacionado con vías o infraestructura.
    """.trimIndent()

    private fun parseResponse(text: String, report: Report): AiReportAnalysis {
        return try {
            // Extraer el JSON de la respuesta
            val jsonStart = text.indexOf('{')
            val jsonEnd = text.lastIndexOf('}') + 1
            if (jsonStart == -1 || jsonEnd == 0) return buildFallbackAnalysis(report)

            val json = text.substring(jsonStart, jsonEnd)

            val recommendation = Regex("\"recommendation\":\\s*\"([^\"]+)\"").find(json)?.groupValues?.get(1) ?: "VERIFICAR"
            val confidence = Regex("\"confidence\":\\s*\"([^\"]+)\"").find(json)?.groupValues?.get(1) ?: "Media"
            val reasoning = Regex("\"reasoning\":\\s*\"([^\"]+)\"").find(json)?.groupValues?.get(1) ?: ""

            // Extraer array de keyPoints
            val keyPointsMatch = Regex("\"keyPoints\":\\s*\\[([^\\]]+)\\]").find(json)
            val keyPoints = keyPointsMatch?.groupValues?.get(1)
                ?.split(",")
                ?.map { it.trim().removeSurrounding("\"") }
                ?: listOf("Análisis completado por IA")

            AiReportAnalysis(
                recommendation = recommendation,
                confidence = confidence,
                keyPoints = keyPoints,
                reasoning = reasoning
            )
        } catch (e: Exception) {
            buildFallbackAnalysis(report)
        }
    }

    private fun buildFallbackAnalysis(report: Report): AiReportAnalysis {
        // Análisis heurístico cuando la IA no está disponible
        val isCredible = report.description.length > 20 && report.title.isNotBlank()
        val recommendation = if (isCredible) "VERIFICAR" else "RECHAZAR"
        val impact = when {
            report.type.contains("Seguridad", ignoreCase = true) -> "Zona con incidencia de seguridad."
            report.type.contains("Infraestructura", ignoreCase = true) -> "Daño en infraestructura vial."
            report.type.contains("Vial", ignoreCase = true) -> "Riesgo vial activo."
            report.type.contains("Alumbrado", ignoreCase = true) -> "Zona oscura con riesgo de incidentes."
            else -> "Requiere revisión técnica."
        }
        return AiReportAnalysis(
            recommendation = recommendation,
            confidence = "Media",
            keyPoints = listOf(
                "Ubicación: Lat ${String.format("%.4f", report.location.latitude)}, Lng ${String.format("%.4f", report.location.longitude)}.",
                impact,
                if (report.important > 0) "${report.important} ciudadanos marcaron esto como importante." else "Sin votos de importancia aún."
            ),
            reasoning = "Análisis basado en heurística local (IA no disponible)."
        )
    }
}
