package com.example.gymapp.data

import android.util.Base64
import com.example.gymapp.data.GeminiClient.Companion.textPart
import com.example.gymapp.data.GeminiClient.Companion.type
import com.example.gymapp.domain.AnalysisConfidence
import com.example.gymapp.domain.FoodItem
import com.example.gymapp.domain.MealAnalysis
import com.example.gymapp.domain.VietFoodDb
import org.json.JSONArray
import org.json.JSONObject

class GeminiMealAnalyzer(private val client: GeminiClient) {
    val isConfigured: Boolean get() = client.isConfigured

    suspend fun analyzePhoto(jpeg: ByteArray): MealAnalysis {
        val image = JSONObject().put(
            "inline_data",
            JSONObject()
                .put("mime_type", "image/jpeg")
                .put("data", Base64.encodeToString(jpeg, Base64.NO_WRAP)),
        )
        return generate(JSONArray().put(textPart(PHOTO_PROMPT + rules())).put(image))
    }

    suspend fun analyzeText(description: String): MealAnalysis {
        return generate(JSONArray().put(textPart(TEXT_PROMPT + rules() + "\nMô tả: " + description.trim())))
    }

    private suspend fun generate(parts: JSONArray): MealAnalysis =
        parseMealAnalysis(client.generateJson(parts, responseSchema()))

    private fun parseMealAnalysis(text: String): MealAnalysis {
        val root = try {
            JSONObject(text)
        } catch (e: Exception) {
            throw MealAnalysisException("AI trả về dữ liệu không đọc được", e)
        }
        val items = root.optJSONArray("items") ?: JSONArray()
        val foods = (0 until items.length()).mapNotNull { index ->
            val item = items.optJSONObject(index) ?: return@mapNotNull null
            val name = item.optString("name").trim()
            val grams = item.optDouble("grams", 0.0)
            if (name.isBlank() || grams.isNaN() || grams <= 0.0) return@mapNotNull null
            VietFoodDb.enrich(
                FoodItem(
                    name = name,
                    grams = grams.coerceAtMost(5000.0),
                    kcalPer100g = item.per100g("kcalPer100g", max = 900.0),
                    proteinPer100g = item.per100g("proteinPer100g", max = 100.0),
                    carbsPer100g = item.per100g("carbsPer100g", max = 100.0),
                    fatPer100g = item.per100g("fatPer100g", max = 100.0),
                ),
            )
        }
        return MealAnalysis(
            isFood = root.optBoolean("isFood", foods.isNotEmpty()),
            dish = root.optString("dish").trim(),
            confidence = AnalysisConfidence.fromKey(root.optString("confidence")),
            items = foods,
        )
    }

    private fun JSONObject.per100g(key: String, max: Double): Double {
        val value = optDouble(key, 0.0)
        return if (value.isNaN()) 0.0 else value.coerceIn(0.0, max)
    }

    private fun responseSchema(): JSONObject {
        val item = JSONObject()
            .put("type", "OBJECT")
            .put(
                "properties",
                JSONObject()
                    .put("name", type("STRING"))
                    .put("grams", type("NUMBER"))
                    .put("kcalPer100g", type("NUMBER"))
                    .put("proteinPer100g", type("NUMBER"))
                    .put("carbsPer100g", type("NUMBER"))
                    .put("fatPer100g", type("NUMBER")),
            )
            .put(
                "required",
                JSONArray(listOf("name", "grams", "kcalPer100g", "proteinPer100g", "carbsPer100g", "fatPer100g")),
            )
        return JSONObject()
            .put("type", "OBJECT")
            .put(
                "properties",
                JSONObject()
                    .put("isFood", type("BOOLEAN"))
                    .put("dish", type("STRING"))
                    .put("confidence", type("STRING").put("enum", JSONArray(listOf("low", "medium", "high"))))
                    .put("items", JSONObject().put("type", "ARRAY").put("items", item)),
            )
            .put("required", JSONArray(listOf("isFood", "dish", "confidence", "items")))
    }

    private fun rules(): String = RULES + "\n- Danh sách món chuẩn (gram tính cho cả phần): " +
        VietFoodDb.foods.joinToString("; ") { it.name }

    private companion object {
        const val RULES = """
- Nếu cả một món hoặc một thành phần khớp với tên trong danh sách món chuẩn bên dưới, dùng ĐÚNG tên đó và trả về nguyên món như một mục (với phở, bún, cơm tấm... grams là cả tô/đĩa kể cả nước dùng). Không khớp thì tách thành từng thành phần riêng (cơm, thịt, trứng, rau, nước dùng, nước sốt, dầu mỡ dùng khi chiên xào). Tên tiếng Việt ngắn gọn.
- Giá trị dinh dưỡng là trên 100 g của thành phần ở dạng đã chế biến (cơm chín, thịt kho, rau luộc...).
- Không bỏ sót dầu ăn, mỡ, nước sốt và nước chấm: đây là nguồn calories hay bị bỏ qua nhất.
- Ước lượng đủ phần thịt, cá, trứng; đạm thường bị đoán thiếu.
- dish: tên món ngắn gọn bằng tiếng Việt.
- confidence: "low" nếu khó xác định món hoặc khẩu phần, "high" chỉ khi rất rõ ràng.
- Nếu không phải đồ ăn: isFood=false, items rỗng."""

        const val PHOTO_PROMPT = """Bạn là chuyên gia dinh dưỡng, quen với món ăn Việt Nam. Phân tích ảnh bữa ăn.
- Ước lượng số gram thực tế của từng thành phần nhìn thấy, dựa vào kích thước bát, đĩa, đũa, thìa trong ảnh. Không đọc chữ hay số in trong ảnh."""

        const val TEXT_PROMPT = """Bạn là chuyên gia dinh dưỡng, quen với món ăn Việt Nam. Người dùng mô tả bữa ăn bằng chữ.
- Quy đổi số lượng ("1 tô", "2 quả", "nửa đĩa") sang gram theo khẩu phần phổ biến ở Việt Nam; nếu không ghi số lượng thì dùng 1 suất thông thường."""
    }
}
