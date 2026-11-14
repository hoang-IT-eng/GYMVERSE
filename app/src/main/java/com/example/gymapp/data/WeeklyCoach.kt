package com.example.gymapp.data

import android.content.Context
import com.example.gymapp.data.GeminiClient.Companion.stringArray
import com.example.gymapp.data.GeminiClient.Companion.textPart
import com.example.gymapp.data.GeminiClient.Companion.type
import com.example.gymapp.domain.CoachReport
import com.example.gymapp.domain.WeeklyDigest
import org.json.JSONArray
import org.json.JSONObject

class WeeklyCoach(private val client: GeminiClient) {
    val isConfigured: Boolean get() = client.isConfigured

    suspend fun review(digest: WeeklyDigest, nowMs: Long = System.currentTimeMillis()): CoachReport {
        val text = client.generateJson(
            parts = JSONArray().put(textPart(PROMPT + "\n\n" + digest.toPromptText())),
            schema = schema(),
            temperature = 0.4,
        )
        val root = try {
            JSONObject(text)
        } catch (e: Exception) {
            throw MealAnalysisException("AI trả về dữ liệu không đọc được", e)
        }
        return CoachReport(
            headline = root.optString("headline").trim().ifBlank { "Tổng kết tuần" },
            wins = root.optJSONArray("wins").strings(),
            issues = root.optJSONArray("issues").strings(),
            actions = root.optJSONArray("actions").strings(),
            deloadSuggested = root.optBoolean("deloadSuggested", false),
            generatedAt = nowMs,
        )
    }

    private fun schema(): JSONObject = JSONObject()
        .put("type", "OBJECT")
        .put(
            "properties",
            JSONObject()
                .put("headline", type("STRING"))
                .put("wins", stringArray())
                .put("issues", stringArray())
                .put("actions", stringArray())
                .put("deloadSuggested", type("BOOLEAN")),
        )
        .put("required", JSONArray(listOf("headline", "wins", "issues", "actions", "deloadSuggested")))

    private companion object {
        const val PROMPT = """Bạn là huấn luyện viên thể hình theo trường phái hypertrophy dựa trên bằng chứng (double progression, RIR 1–3, khối lượng MEV–MRV). Dưới đây là số liệu 7 ngày qua của một người tập. Viết tổng kết tuần bằng tiếng Việt, xưng "bạn", ngắn gọn và cụ thể theo đúng số liệu, không bịa thêm dữ liệu.
- headline: 1 câu tóm tắt tuần.
- wins: 1–3 điểm làm tốt (có số liệu).
- issues: 0–3 vấn đề đáng chú ý nhất (nhóm cơ dưới MEV hoặc trên MRV, bài chững lại, ăn thiếu đạm hoặc lệch calo so với mục tiêu, ít buổi).
- actions: 2–4 việc cụ thể cho tuần tới (ví dụ thêm 4 hiệp cho nhóm X, đổi biến thể bài Y, ăn thêm Z g đạm).
- deloadSuggested: true chỉ khi nhiều bài cùng chững lại hoặc khối lượng vượt MRV ở nhiều nhóm cơ.
Nếu tuần không có buổi tập nào, nói thẳng và gợi ý cách quay lại. Nếu không ghi bữa ăn, đừng kết luận về dinh dưỡng."""
    }
}

private fun JSONArray?.strings(): List<String> {
    if (this == null) return emptyList()
    return (0 until length()).mapNotNull { optString(it).trim().ifBlank { null } }
}

/** Keeps the last report so reopening the screen does not spend another AI call. */
object CoachStore {
    private const val PREFS = "weekly_coach"
    private const val REPORT = "report"

    fun save(context: Context, report: CoachReport) {
        val json = JSONObject()
            .put("headline", report.headline)
            .put("wins", JSONArray(report.wins))
            .put("issues", JSONArray(report.issues))
            .put("actions", JSONArray(report.actions))
            .put("deloadSuggested", report.deloadSuggested)
            .put("generatedAt", report.generatedAt)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(REPORT, json.toString()).apply()
    }

    fun load(context: Context): CoachReport? {
        val text = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(REPORT, null) ?: return null
        return runCatching {
            val o = JSONObject(text)
            CoachReport(
                headline = o.getString("headline"),
                wins = o.optJSONArray("wins").strings(),
                issues = o.optJSONArray("issues").strings(),
                actions = o.optJSONArray("actions").strings(),
                deloadSuggested = o.optBoolean("deloadSuggested"),
                generatedAt = o.getLong("generatedAt"),
            )
        }.getOrNull()
    }
}
