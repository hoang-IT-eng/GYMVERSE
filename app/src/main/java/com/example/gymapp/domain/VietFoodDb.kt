package com.example.gymapp.domain

import java.text.Normalizer

data class FoodRef(
    val name: String,
    val kcal: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val portionLabel: String,
    val portionGrams: Double,
    val aliases: List<String> = emptyList(),
) {
    fun toItem(grams: Double = portionGrams) = FoodItem(
        name = name,
        grams = grams,
        kcalPer100g = kcal,
        proteinPer100g = protein,
        carbsPer100g = carbs,
        fatPer100g = fat,
        fromDatabase = true,
    )
}

/**
 * Per 100 g as eaten. Single foods follow the Vietnamese food composition
 * table and USDA; mixed dishes are whole servings including broth and sauce,
 * so their grams should cover the full bowl or plate.
 */
object VietFoodDb {
    val foods: List<FoodRef> = listOf(
        // Tinh bột
        FoodRef("Cơm trắng", 130.0, 2.7, 28.2, 0.3, "1 chén", 150.0, listOf("cơm", "cơm tẻ")),
        FoodRef("Cơm gạo lứt", 112.0, 2.6, 23.5, 0.9, "1 chén", 150.0),
        FoodRef("Xôi trắng", 185.0, 3.8, 39.0, 0.6, "1 gói", 200.0, listOf("xôi nếp", "xôi")),
        FoodRef("Bánh mì không", 260.0, 8.5, 52.0, 2.5, "1 ổ", 90.0, listOf("bánh mì trắng", "ổ bánh mì")),
        FoodRef("Bánh mì sandwich", 265.0, 9.0, 49.0, 3.2, "1 lát", 30.0, listOf("bánh mì lát")),
        FoodRef("Bánh phở", 141.0, 3.2, 32.0, 0.1, "1 phần", 200.0, listOf("phở tươi")),
        FoodRef("Bún tươi", 110.0, 1.7, 25.7, 0.0, "1 phần", 200.0, listOf("bún")),
        FoodRef("Mì ăn liền", 450.0, 9.5, 62.0, 18.0, "1 gói", 75.0, listOf("mì gói", "mì tôm")),
        FoodRef("Khoai lang luộc", 90.0, 2.0, 20.7, 0.1, "1 củ", 150.0, listOf("khoai lang")),
        FoodRef("Ngô luộc", 96.0, 3.4, 21.0, 1.5, "1 trái", 150.0, listOf("bắp luộc", "bắp")),
        FoodRef("Yến mạch", 389.0, 16.9, 66.3, 6.9, "1 phần khô", 40.0, listOf("oats", "yến mạch khô")),

        // Món hỗn hợp
        FoodRef("Phở bò", 85.0, 5.5, 10.0, 2.4, "1 tô", 550.0, listOf("phở bò tái", "phở tái", "phở chín", "phở bò chín")),
        FoodRef("Phở gà", 75.0, 5.0, 10.0, 1.6, "1 tô", 550.0),
        FoodRef("Bún bò Huế", 80.0, 5.0, 9.0, 2.8, "1 tô", 600.0, listOf("bún bò")),
        FoodRef("Bún chả", 135.0, 7.0, 15.0, 5.2, "1 phần", 450.0),
        FoodRef("Bún riêu", 76.0, 4.5, 9.0, 2.4, "1 tô", 550.0, listOf("bún riêu cua")),
        FoodRef("Bún thịt nướng", 140.0, 7.0, 17.0, 5.0, "1 tô", 400.0),
        FoodRef("Hủ tiếu", 82.0, 5.0, 10.5, 2.2, "1 tô", 550.0, listOf("hủ tiếu nam vang", "hủ tíu")),
        FoodRef("Mì Quảng", 120.0, 6.5, 15.0, 3.8, "1 tô", 450.0),
        FoodRef("Cơm tấm sườn", 163.0, 7.5, 22.0, 5.0, "1 đĩa", 400.0, listOf("cơm sườn")),
        FoodRef("Cơm tấm sườn bì chả", 177.0, 8.5, 21.0, 6.5, "1 đĩa", 450.0, listOf("cơm tấm đặc biệt")),
        FoodRef("Cơm gà xối mỡ", 188.0, 8.0, 22.0, 7.5, "1 đĩa", 400.0, listOf("cơm gà")),
        FoodRef("Cơm chiên", 170.0, 5.0, 24.0, 6.0, "1 đĩa", 350.0, listOf("cơm chiên dương châu", "cơm rang")),
        FoodRef("Bánh mì thịt", 250.0, 10.0, 30.0, 10.0, "1 ổ", 200.0, listOf("bánh mì pate", "bánh mì kẹp thịt")),
        FoodRef("Bánh mì ốp la", 232.0, 9.5, 26.0, 10.0, "1 phần", 190.0, listOf("bánh mì trứng")),
        FoodRef("Xôi mặn", 220.0, 7.0, 32.0, 7.0, "1 gói", 250.0, listOf("xôi gà", "xôi thập cẩm")),
        FoodRef("Bánh xèo", 200.0, 6.0, 20.0, 10.5, "1 cái", 200.0),
        FoodRef("Gỏi cuốn", 105.0, 6.0, 15.0, 2.2, "1 cuốn", 90.0, listOf("cuốn tôm thịt")),
        FoodRef("Chả giò", 280.0, 9.0, 20.0, 18.0, "1 cái", 40.0, listOf("nem rán", "chả giò chiên")),
        FoodRef("Bánh cuốn", 140.0, 5.0, 22.0, 3.5, "1 đĩa", 300.0),
        FoodRef("Bánh bao", 230.0, 8.0, 32.0, 7.5, "1 cái", 150.0, listOf("bánh bao nhân thịt")),
        FoodRef("Cháo", 55.0, 3.5, 8.0, 1.0, "1 tô", 400.0, listOf("cháo gà", "cháo thịt", "cháo lòng")),
        FoodRef("Bò kho", 120.0, 11.0, 4.0, 6.5, "1 chén", 250.0),
        FoodRef("Thịt kho trứng", 230.0, 13.0, 5.0, 17.5, "1 phần", 150.0, listOf("thịt kho tàu", "thịt kho")),
        FoodRef("Cá kho", 180.0, 18.0, 6.0, 9.5, "1 khúc", 120.0, listOf("cá kho tộ")),
        FoodRef("Canh chua cá", 35.0, 3.5, 3.0, 1.0, "1 tô", 300.0, listOf("canh chua")),
        FoodRef("Canh rau", 15.0, 1.0, 2.0, 0.3, "1 chén", 250.0, listOf("canh rau muống", "canh cải")),

        // Đạm
        FoodRef("Ức gà", 165.0, 31.0, 0.0, 3.6, "1 miếng", 150.0, listOf("ức gà luộc", "ức gà áp chảo", "lườn gà")),
        FoodRef("Đùi gà", 230.0, 23.0, 0.0, 15.0, "1 đùi", 120.0, listOf("đùi gà nướng", "đùi gà chiên")),
        FoodRef("Thịt gà luộc", 198.0, 24.0, 0.0, 11.0, "1 phần", 150.0, listOf("gà luộc")),
        FoodRef("Thịt heo nạc", 198.0, 28.0, 0.0, 9.5, "1 phần", 100.0, listOf("thịt nạc", "thịt lợn nạc")),
        FoodRef("Thịt ba chỉ", 420.0, 14.0, 0.0, 40.0, "1 phần", 100.0, listOf("ba chỉ", "thịt ba rọi")),
        FoodRef("Sườn nướng", 260.0, 22.0, 6.0, 16.0, "1 miếng", 120.0, listOf("sườn cốt lết nướng")),
        FoodRef("Thịt bò nạc", 212.0, 30.0, 0.0, 10.0, "1 phần", 100.0, listOf("thịt bò", "bò bít tết", "thăn bò")),
        FoodRef("Cá hồi", 206.0, 22.0, 0.0, 13.0, "1 miếng", 120.0),
        FoodRef("Cá basa", 140.0, 18.0, 0.0, 7.5, "1 miếng", 150.0, listOf("cá tra", "phi lê cá")),
        FoodRef("Cá ngừ hộp", 116.0, 26.0, 0.0, 1.0, "1 hộp", 120.0, listOf("cá ngừ")),
        FoodRef("Tôm luộc", 99.0, 24.0, 0.2, 0.3, "1 phần", 100.0, listOf("tôm")),
        FoodRef("Mực", 92.0, 17.0, 3.0, 1.4, "1 phần", 100.0, listOf("mực hấp", "mực xào")),
        FoodRef("Trứng gà luộc", 155.0, 12.6, 1.1, 10.6, "1 quả", 50.0, listOf("trứng luộc", "trứng gà")),
        FoodRef("Trứng chiên", 196.0, 13.6, 0.8, 15.0, "1 quả", 55.0, listOf("trứng ốp la", "trứng rán")),
        FoodRef("Lòng trắng trứng", 52.0, 10.9, 0.7, 0.2, "1 quả", 33.0),
        FoodRef("Đậu hũ", 80.0, 8.0, 1.9, 4.8, "1 miếng", 100.0, listOf("đậu phụ", "đậu hũ non")),
        FoodRef("Đậu hũ chiên", 271.0, 17.0, 10.0, 20.0, "1 miếng", 100.0, listOf("đậu phụ rán")),
        FoodRef("Chả lụa", 136.0, 21.5, 0.0, 5.5, "1 lát", 30.0, listOf("giò lụa", "giò")),
        FoodRef("Xúc xích", 290.0, 12.0, 4.0, 25.0, "1 cây", 50.0),

        // Sữa và đồ bổ sung
        FoodRef("Whey protein", 400.0, 78.0, 8.0, 6.0, "1 muỗng", 30.0, listOf("whey", "bột protein")),
        FoodRef("Sữa tươi không đường", 62.0, 3.2, 4.8, 3.3, "1 hộp", 180.0, listOf("sữa không đường")),
        FoodRef("Sữa tươi có đường", 75.0, 3.0, 8.0, 3.2, "1 hộp", 180.0, listOf("sữa tươi", "sữa có đường")),
        FoodRef("Sữa chua", 100.0, 3.1, 15.0, 3.0, "1 hũ", 100.0, listOf("sữa chua có đường")),
        FoodRef("Sữa chua Hy Lạp", 73.0, 10.0, 3.9, 2.0, "1 hũ", 100.0, listOf("greek yogurt")),
        FoodRef("Sữa đậu nành", 57.0, 2.8, 7.0, 2.0, "1 ly", 250.0),
        FoodRef("Phô mai lát", 303.0, 18.0, 6.0, 23.0, "1 lát", 18.0, listOf("phô mai")),

        // Rau, trái cây
        FoodRef("Rau muống xào", 72.0, 2.8, 4.0, 5.0, "1 đĩa", 200.0, listOf("rau xào", "rau muống xào tỏi")),
        FoodRef("Rau luộc", 30.0, 2.5, 4.5, 0.3, "1 đĩa", 150.0, listOf("bông cải luộc", "cải luộc")),
        FoodRef("Salad", 20.0, 1.3, 3.5, 0.2, "1 đĩa", 150.0, listOf("xà lách")),
        FoodRef("Dưa leo", 15.0, 0.7, 3.6, 0.1, "1 quả", 150.0, listOf("dưa chuột")),
        FoodRef("Cà chua", 18.0, 0.9, 3.9, 0.2, "1 quả", 100.0),
        FoodRef("Chuối", 89.0, 1.1, 22.8, 0.3, "1 quả", 100.0),
        FoodRef("Táo", 52.0, 0.3, 13.8, 0.2, "1 quả", 180.0),
        FoodRef("Cam", 47.0, 0.9, 11.8, 0.1, "1 quả", 150.0),
        FoodRef("Xoài", 60.0, 0.8, 15.0, 0.4, "1 quả", 200.0),
        FoodRef("Dưa hấu", 30.0, 0.6, 7.6, 0.2, "1 miếng", 300.0),
        FoodRef("Đu đủ", 43.0, 0.5, 10.8, 0.3, "1 miếng", 200.0),
        FoodRef("Bơ", 160.0, 2.0, 8.5, 14.7, "nửa quả", 100.0, listOf("quả bơ", "trái bơ")),
        FoodRef("Thanh long", 60.0, 1.2, 13.0, 0.4, "1 quả", 300.0),

        // Đồ uống, ăn vặt, gia vị
        FoodRef("Trà sữa trân châu", 85.0, 0.8, 15.0, 2.6, "1 ly", 500.0, listOf("trà sữa")),
        FoodRef("Cà phê sữa đá", 65.0, 1.5, 11.0, 1.7, "1 ly", 200.0, listOf("cà phê sữa", "bạc xỉu")),
        FoodRef("Cà phê đen đá", 20.0, 0.2, 5.0, 0.0, "1 ly", 200.0, listOf("cà phê đen")),
        FoodRef("Nước ngọt có ga", 42.0, 0.0, 10.6, 0.0, "1 lon", 330.0, listOf("coca", "pepsi", "nước ngọt")),
        FoodRef("Bia", 43.0, 0.5, 3.6, 0.0, "1 lon", 330.0),
        FoodRef("Nước mía", 68.0, 0.1, 17.0, 0.0, "1 ly", 300.0),
        FoodRef("Hạt điều", 574.0, 15.0, 32.7, 46.4, "1 nắm", 30.0),
        FoodRef("Đậu phộng rang", 585.0, 24.4, 21.3, 49.7, "1 nắm", 30.0, listOf("lạc rang", "đậu phộng")),
        FoodRef("Bánh flan", 143.0, 4.5, 20.0, 5.0, "1 cái", 100.0, listOf("caramen")),
        FoodRef("Chè", 110.0, 2.5, 23.0, 1.0, "1 ly", 300.0, listOf("chè đậu xanh", "chè thập cẩm")),
        FoodRef("Dầu ăn", 884.0, 0.0, 0.0, 100.0, "1 muỗng canh", 13.0, listOf("dầu", "mỡ nước")),
        FoodRef("Mỡ hành", 505.0, 0.5, 2.0, 55.0, "1 muỗng", 15.0),
        FoodRef("Nước mắm", 35.0, 5.0, 3.6, 0.0, "1 muỗng", 15.0),
        FoodRef("Nước chấm", 60.0, 2.0, 13.0, 0.0, "1 chén", 50.0, listOf("nước mắm pha", "nước mắm chua ngọt")),
    )

    private val byKey: Map<String, FoodRef> = buildMap {
        for (food in foods) {
            putIfAbsent(normalize(food.name), food)
            food.aliases.forEach { putIfAbsent(normalize(it), food) }
        }
    }

    /** Exact match on name or alias, ignoring accents and case. */
    fun match(name: String): FoodRef? = byKey[normalize(name)]

    fun search(query: String, limit: Int = 20): List<FoodRef> {
        val q = normalize(query)
        if (q.isEmpty()) return foods.take(limit)
        fun rank(food: FoodRef): Int? {
            val keys = listOf(normalize(food.name)) + food.aliases.map(::normalize)
            return when {
                keys.any { it == q } -> 0
                keys.any { it.startsWith(q) } -> 1
                keys.any { key -> key.split(' ').any { it.startsWith(q) } } -> 2
                keys.any { it.contains(q) } -> 3
                else -> null
            }
        }
        return foods.mapNotNull { food -> rank(food)?.let { food to it } }
            .sortedBy { it.second }
            .take(limit)
            .map { it.first }
    }

    /** Replace AI per-100 g guesses with table values when the name matches exactly; grams stay. */
    fun enrich(item: FoodItem): FoodItem {
        val ref = match(item.name) ?: return item
        return ref.toItem(item.grams).copy(name = item.name)
    }

    fun normalize(text: String): String =
        Normalizer.normalize(text.trim().lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .replace('đ', 'd')
            .replace(Regex("\\s+"), " ")
}
