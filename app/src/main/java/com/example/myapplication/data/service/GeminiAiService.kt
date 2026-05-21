package com.example.myapplication.data.service

import android.util.Log
import com.example.myapplication.BuildConfig
import com.example.myapplication.domain.model.Report
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

data class AiReportAnalysis(
    val recommendation: String,
    val confidence: String,
    val keyPoints: List<String>,
    val reasoning: String,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class OpenRouterMessage(val role: String, val content: String)

class OpenRouterChat(private val assistantKey: String) {
    private val baseUrl = "https://openrouter.ai/api/v1/chat/completions"
    private val models = listOf(
        "meta-llama/llama-3.3-70b-instruct:free",
        "google/gemma-4-26b-a4b-it:free",
        "meta-llama/llama-3.2-3b-instruct:free"
    )
    private val history = mutableListOf(
        OpenRouterMessage(
            "system",
            "Eres el asistente virtual de Vialert, una app ciudadana para reportar problemas " +
            "de infraestructura y seguridad vial en Colombia. " +
            "Ayudas a los usuarios con: uso de la app, reportes de vías, alumbrado, movilidad y seguridad vial. " +
            "Responde siempre en español, de forma amigable y concisa (máximo 3 oraciones)."
        )
    )

    suspend fun send(message: String): String {
        history.add(OpenRouterMessage("user", message))
        val response = callOpenRouter()
        history.add(OpenRouterMessage("assistant", response))
        return response
    }

    private suspend fun callOpenRouter(): String = withContext(Dispatchers.IO) {
        val messagesArray = JSONArray().apply {
            history.forEach { msg ->
                put(JSONObject().put("role", msg.role).put("content", msg.content))
            }
        }
        for (model in models) {
            try {
                val body = JSONObject().put("model", model).put("messages", messagesArray).toString()
                val conn = (URL(baseUrl).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Authorization", "Bearer $assistantKey")
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("HTTP-Referer", "https://vialert.app")
                    setRequestProperty("X-Title", "Vialert")
                    connectTimeout = 30_000
                    readTimeout = 60_000
                    doOutput = true
                }
                OutputStreamWriter(conn.outputStream).use { it.write(body) }
                val code = conn.responseCode
                if (code == 429 || code == 404) {
                    val err = conn.errorStream?.bufferedReader()?.readText() ?: ""
                    conn.disconnect()
                    if (code == 429) {
                        val wait = try {
                            val secs = JSONObject(err).optJSONObject("error")
                                ?.optJSONObject("metadata")?.optDouble("retry_after_seconds", 3.0) ?: 3.0
                            (secs * 1000).toLong().coerceIn(2_000, 10_000)
                        } catch (_: Exception) { 3_000L }
                        delay(wait)
                    }
                    continue
                }
                val text = if (code in 200..299) conn.inputStream.bufferedReader().readText()
                           else { conn.disconnect(); continue }
                conn.disconnect()
                return@withContext JSONObject(text).getJSONArray("choices")
                    .getJSONObject(0).getJSONObject("message").getString("content")
            } catch (e: Exception) {
                Log.e("AssistantChat", "Error con $model: ${e.message}")
            }
        }
        "Los servidores de IA están ocupados. Intenta de nuevo en unos segundos."
    }
}

@Singleton
class GeminiAiService @Inject constructor() {

    private val apiKey get() = BuildConfig.OPENROUTER_API_KEY
    private val baseUrl = "https://openrouter.ai/api/v1/chat/completions"

    private val models = listOf(
        "meta-llama/llama-3.3-70b-instruct:free",
        "google/gemma-4-26b-a4b-it:free",
        "meta-llama/llama-3.2-3b-instruct:free"
    )

    fun createAssistantChat(): OpenRouterChat = OpenRouterChat(BuildConfig.OPENROUTER_ASSISTANT_KEY)

    suspend fun sendChatMessage(chat: OpenRouterChat, message: String): String = chat.send(message)

    internal suspend fun chatCompletion(messages: List<OpenRouterMessage>): String {
        if (apiKey.isBlank()) return "Configura OPENROUTER_API_KEY en local.properties"
        return withContext(Dispatchers.IO) {
            val messagesArray = JSONArray().apply {
                messages.forEach { msg ->
                    put(JSONObject().put("role", msg.role).put("content", msg.content))
                }
            }
            for (model in models) {
                Log.d("OpenRouter", "Intentando modelo: $model")
                try {
                    val body = JSONObject()
                        .put("model", model)
                        .put("messages", messagesArray)
                        .toString()

                    val conn = (URL(baseUrl).openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        setRequestProperty("Authorization", "Bearer $apiKey")
                        setRequestProperty("Content-Type", "application/json")
                        setRequestProperty("HTTP-Referer", "https://vialert.app")
                        setRequestProperty("X-Title", "Vialert")
                        connectTimeout = 30_000
                        readTimeout = 60_000
                        doOutput = true
                    }
                    OutputStreamWriter(conn.outputStream).use { it.write(body) }
                    val code = conn.responseCode
                    if (code == 429 || code == 404) {
                        val err = conn.errorStream?.bufferedReader()?.readText() ?: ""
                        Log.w("OpenRouter", "Modelo $model falló con $code, probando siguiente.")
                        conn.disconnect()
                        if (code == 429) {
                            val retryAfterMs = try {
                                val meta = JSONObject(err)
                                    .optJSONObject("error")
                                    ?.optJSONObject("metadata")
                                val secs = meta?.optDouble("retry_after_seconds", 3.0) ?: 3.0
                                (secs * 1000).toLong().coerceIn(2_000, 10_000)
                            } catch (_: Exception) { 3_000L }
                            Log.d("OpenRouter", "Esperando ${retryAfterMs}ms antes del siguiente modelo")
                            delay(retryAfterMs)
                        }
                        continue
                    }
                    val responseText = if (code in 200..299) {
                        conn.inputStream.bufferedReader().readText()
                    } else {
                        val err = conn.errorStream?.bufferedReader()?.readText() ?: "sin detalle"
                        Log.e("OpenRouter", "HTTP $code: $err")
                        conn.disconnect()
                        continue
                    }
                    conn.disconnect()
                    return@withContext JSONObject(responseText)
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                } catch (e: Exception) {
                    Log.e("OpenRouter", "Error con modelo $model: ${e.message}", e)
                }
            }
            "Los servidores de IA están ocupados. Intenta de nuevo en unos segundos."
        }
    }

    suspend fun analyzeReport(report: Report): AiReportAnalysis {
        Log.d("OpenRouter", "analyzeReport llamado. Key length=${apiKey.length}, blank=${apiKey.isBlank()}")
        if (apiKey.isBlank()) {
            Log.e("OpenRouter", "API key vacía — agrega OPENROUTER_API_KEY en local.properties y haz Clean+Rebuild")
            return buildFallbackAnalysis(report).copy(error = "API key no configurada")
        }
        return try {
            val messages = listOf(
                OpenRouterMessage(
                    "system",
                    "Eres un moderador estricto de reportes ciudadanos de la app Vialert (Colombia). " +
                    "Tu única función es evaluar si un reporte es válido o inválido. " +
                    "Responde ÚNICAMENTE con JSON válido. Sin markdown, sin bloques de código, sin texto antes o después del JSON."
                ),
                OpenRouterMessage("user", buildPrompt(report))
            )
            val text = chatCompletion(messages)
            Log.d("OpenRouter", "Respuesta raw: $text")
            parseResponse(text, report)
        } catch (e: Exception) {
            Log.e("OpenRouter", "Error análisis: ${e.message}", e)
            buildFallbackAnalysis(report).copy(error = e.message)
        }
    }

    private fun buildPrompt(report: Report): String = """
        Evalúa este reporte de la app Vialert y decide si VERIFICAR o RECHAZAR.

        REPORTE:
        Título: "${report.title}"
        Categoría: ${report.type}
        Descripción: "${report.description}"
        Votos ciudadanos: ${report.important}

        CAUSAS DE RECHAZO (basta con cumplir UNA):
        - Descripción con texto sin sentido, aleatorio o incomprensible (ej: "asdfjkl", "efpojkpoafjpsd", teclas al azar)
        - Descripción vacía, demasiado corta o que no explica ningún problema
        - El problema NO es sobre vías, infraestructura vial, alumbrado público, señalización, movilidad ni seguridad vial
        - Contenido ofensivo, spam o broma

        CONDICIONES PARA VERIFICAR (debe cumplir TODAS):
        - Descripción coherente y comprensible
        - Describe un problema real de infraestructura o seguridad vial de interés público
        - Hay suficiente información para que un inspector pueda investigar

        Responde ÚNICAMENTE con este JSON (sin markdown, sin texto adicional):
        {"recommendation":"VERIFICAR","confidence":"Alta","keyPoints":["observación 1","observación 2","observación 3"],"reasoning":"explicación de 1-2 oraciones"}

        El campo "recommendation" debe ser exactamente "VERIFICAR" o "RECHAZAR".
        El campo "confidence" debe ser exactamente "Alta", "Media" o "Baja".
    """.trimIndent()

    private fun extractJsonObject(text: String): String? {
        val start = text.indexOf('{')
        if (start == -1) return null
        var depth = 0
        var inString = false
        var escape = false
        for (i in start until text.length) {
            val c = text[i]
            when {
                escape -> escape = false
                c == '\\' && inString -> escape = true
                c == '"' -> inString = !inString
                !inString && c == '{' -> depth++
                !inString && c == '}' -> {
                    depth--
                    if (depth == 0) return text.substring(start, i + 1)
                }
            }
        }
        return null
    }

    private fun parseResponse(text: String, report: Report): AiReportAnalysis {
        return try {
            val jsonStr = extractJsonObject(text)
            if (jsonStr == null) {
                Log.w("OpenRouter", "No se encontró JSON en: $text")
                return buildFallbackAnalysis(report)
            }
            val json = JSONObject(jsonStr)
            val keyPoints = mutableListOf<String>()
            json.optJSONArray("keyPoints")?.let { arr ->
                for (i in 0 until arr.length()) keyPoints.add(arr.getString(i))
            }
            val rec = json.optString("recommendation", "").uppercase()
            AiReportAnalysis(
                recommendation = if (rec == "RECHAZAR") "RECHAZAR" else "VERIFICAR",
                confidence     = json.optString("confidence", "Media"),
                keyPoints      = keyPoints.ifEmpty { listOf("Análisis completado") },
                reasoning      = json.optString("reasoning", "")
            )
        } catch (e: Exception) {
            Log.e("OpenRouter", "Error parseando JSON: ${e.message}")
            buildFallbackAnalysis(report)
        }
    }

    private fun looksLikeGarbage(text: String): Boolean {
        if (text.isBlank()) return true
        val words = text.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (words.size < 3) return true
        val vowels = "aeiouáéíóúüàèìòù"
        val consonantRuns = text.lowercase().split(Regex("[${vowels}\\s]+")).filter { it.length > 4 }
        if (consonantRuns.isNotEmpty()) return true
        val uniqueChars = text.lowercase().toSet().size
        if (text.length > 10 && uniqueChars < 5) return true
        return false
    }

    private fun buildFallbackAnalysis(report: Report): AiReportAnalysis {
        val descGarbage = looksLikeGarbage(report.description)
        val titleBlank  = report.title.isBlank()
        val isCredible  = !descGarbage && !titleBlank
        val impact = when {
            report.type.contains("Seguridad", ignoreCase = true)       -> "Zona con incidencia de seguridad."
            report.type.contains("Infraestructura", ignoreCase = true) -> "Daño en infraestructura vial."
            report.type.contains("Vial", ignoreCase = true)            -> "Riesgo vial activo."
            report.type.contains("Alumbrado", ignoreCase = true)       -> "Zona oscura con riesgo de incidentes."
            else -> "Requiere revisión técnica."
        }
        val rejectReason = when {
            titleBlank   -> "Título vacío."
            descGarbage  -> "Descripción incoherente o insuficiente."
            else         -> null
        }
        return AiReportAnalysis(
            recommendation = if (isCredible) "VERIFICAR" else "RECHAZAR",
            confidence     = "Media",
            keyPoints      = listOfNotNull(
                rejectReason,
                "Ubicación: Lat ${String.format("%.4f", report.location.latitude)}, Lng ${String.format("%.4f", report.location.longitude)}.",
                impact,
                if (report.important > 0) "${report.important} ciudadanos marcaron esto como importante." else "Sin votos aún."
            ),
            reasoning = if (isCredible) "Análisis heurístico local (IA no disponible)." else "Descripción no válida: texto incoherente o insuficiente para evaluar el reporte."
        )
    }
}
