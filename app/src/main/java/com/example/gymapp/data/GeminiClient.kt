package com.example.gymapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MealAnalysisException(message: String, cause: Throwable? = null) : Exception(message, cause)

/** Minimal Gemini generateContent call that returns the model's JSON text. */
class GeminiClient(
    private val apiKey: String,
    private val model: String,
) {
    val isConfigured: Boolean get() = apiKey.isNotBlank()

    suspend fun generateJson(
        parts: JSONArray,
        schema: JSONObject,
        temperature: Double = 0.2,
    ): String = withContext(Dispatchers.IO) {
        if (!isConfigured) {
            throw MealAnalysisException("Chưa cấu hình khóa Gemini (gemini.apiKey trong local.properties)")
        }
        val body = JSONObject()
            .put("contents", JSONArray().put(JSONObject().put("parts", parts)))
            .put(
                "generationConfig",
                JSONObject()
                    .put("responseMimeType", "application/json")
                    .put("responseSchema", schema)
                    .put("temperature", temperature),
            )
        val connection = URL("$BASE_URL/$model:generateContent").openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 15_000
            connection.readTimeout = 60_000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            connection.setRequestProperty("x-goog-api-key", apiKey)
            connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            val code = connection.responseCode
            if (code !in 200..299) {
                connection.errorStream?.close()
                throw MealAnalysisException(messageForStatus(code))
            }
            val text = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            extractText(text)
        } catch (e: MealAnalysisException) {
            throw e
        } catch (e: SocketTimeoutException) {
            throw MealAnalysisException("AI phản hồi quá lâu, thử lại sau", e)
        } catch (e: IOException) {
            throw MealAnalysisException("Không kết nối được tới AI, kiểm tra mạng", e)
        } finally {
            connection.disconnect()
        }
    }

    private fun extractText(response: String): String {
        val root = JSONObject(response)
        val candidate = root.optJSONArray("candidates")?.optJSONObject(0)
        if (candidate == null) {
            val reason = root.optJSONObject("promptFeedback")?.optString("blockReason").orEmpty()
            throw MealAnalysisException(if (reason.isNotBlank()) "AI từ chối yêu cầu này ($reason)" else "AI không trả về kết quả")
        }
        val parts = candidate.optJSONObject("content")?.optJSONArray("parts")
            ?: throw MealAnalysisException("AI không trả về kết quả (${candidate.optString("finishReason")})")
        return (0 until parts.length())
            .map { parts.getJSONObject(it) }
            .filterNot { it.optBoolean("thought") }
            .joinToString("") { it.optString("text") }
    }

    private fun messageForStatus(code: Int): String = when (code) {
        400 -> "Yêu cầu gửi AI không hợp lệ (400)"
        401, 403 -> "Khóa Gemini không hợp lệ hoặc chưa được cấp quyền ($code)"
        404 -> "Không tìm thấy model $model (404)"
        429 -> "Đã hết lượt gọi AI, thử lại sau ít phút (429)"
        in 500..599 -> "Máy chủ AI đang quá tải, thử lại sau ($code)"
        else -> "AI trả lỗi $code"
    }

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

        fun textPart(text: String): JSONObject = JSONObject().put("text", text)

        fun type(name: String): JSONObject = JSONObject().put("type", name)

        fun stringArray(): JSONObject = JSONObject().put("type", "ARRAY").put("items", type("STRING"))
    }
}
