package com.example.gymapp.domain

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt

data class ExerciseMeta(
    val vietnameseName: String,
    val tier: String,
    val equipment: String,
    val isCompound: Boolean,
    val difficulty: String,
    val setupSteps: List<Pair<String, String>>,
    val proTip: String,
    val redFlagTitle: String,
    val redFlagBody: String,
    val warningTitle: String,
    val warningBody: String,
)

data class RoutineDaySpec(
    val dayNumber: Int,
    val title: String,
    val subtitle: String,
    val targetRpe: String,
    val exerciseNames: List<String>,
    val exerciseIds: List<Long> = emptyList(),
    val isRest: Boolean = false,
)

data class RoutinePlan(
    val id: String,
    val name: String,
    val subtitle: String,
    val daysPerWeek: String,
    val durationText: String,
    val frequencyText: String,
    val badges: List<String>,
    val filterTag: String,
    val days: List<RoutineDaySpec>,
)

data class MuscleVolumeLandmark(
    val group: MuscleGroup,
    val title: String,
    val setsCompleted: Int,
    val mevSets: Int,
    val mrvSets: Int,
) {
    val zoneLabel: String
        get() = when {
            setsCompleted < mevSets -> "Dưới MEV"
            setsCompleted <= mrvSets -> "MAV ${(setsCompleted * 100 / mrvSets).coerceIn(50, 100)}%"
            else -> "Vượt MRV"
        }

    val isUnderMev: Boolean get() = setsCompleted < mevSets
    val isOverMrv: Boolean get() = setsCompleted > mrvSets
    val progressFraction: Float get() = (setsCompleted.toFloat() / mrvSets.toFloat()).coerceIn(0f, 1f)
}

data class MuscleRecoveryItem(
    val label: String,
    val percent: Int,
    val statusText: String,
    val isOptimal: Boolean,
)

data class DayCheckItem(
    val label: String,
    val date: LocalDate,
    val completed: Boolean,
    val isToday: Boolean,
)

object IronLogCatalog {

    val routines: List<RoutinePlan> = listOf(
        RoutinePlan(
            id = "ppl_6d",
            name = "PPL Hypertrophy 6 Days",
            subtitle = "Push • Pull • Legs (Tần suất 2x)",
            daysPerWeek = "6 ngày/tuần",
            durationText = "50-65m",
            frequencyText = "2x / tuần",
            badges = listOf("Chu kỳ 12 tuần", "MAV (Tối ưu)"),
            filterTag = "6day",
            days = listOf(
                RoutineDaySpec(
                    dayNumber = 1,
                    title = "Push Day A - Ngực & Vai Trước",
                    subtitle = "5 bài tập • 18 hiệp • RPE 7-9",
                    targetRpe = "RIR 1 - 2",
                    exerciseNames = listOf(
                        "Bench Press",
                        "Incline Dumbbell Press",
                        "Overhead Press",
                        "Dip",
                        "Triceps Pushdown",
                    ),
                ),
                RoutineDaySpec(
                    dayNumber = 2,
                    title = "Pull Day A - Lưng Xô & Bắp Tay Trước",
                    subtitle = "5 bài tập • 18 hiệp • RPE 8-9",
                    targetRpe = "RIR 1 - 2",
                    exerciseNames = listOf(
                        "Barbell Row",
                        "Lat Pulldown",
                        "Seated Cable Row",
                        "Face Pull",
                        "Barbell Curl",
                    ),
                ),
                RoutineDaySpec(
                    dayNumber = 3,
                    title = "Leg Day A - Đùi Trước, Mông & Bắp Chân",
                    subtitle = "5 bài tập • 18 hiệp • RPE 7-9",
                    targetRpe = "RIR 1 - 2",
                    exerciseNames = listOf(
                        "Squat",
                        "Romanian Deadlift",
                        "Leg Press",
                        "Leg Curl",
                        "Calf Raise",
                    ),
                ),
                RoutineDaySpec(
                    dayNumber = 4,
                    title = "Nghỉ hồi phục cơ bắp (Rest & Recovery)",
                    subtitle = "Bổ sung Protein, giãn cơ & nạp Glycogen",
                    targetRpe = "REST",
                    exerciseNames = emptyList(),
                    isRest = true,
                ),
                RoutineDaySpec(
                    dayNumber = 5,
                    title = "Push Day B - Ngực Trên & Vai Ngang",
                    subtitle = "5 bài tập • 17 hiệp • RPE 8-9",
                    targetRpe = "RIR 1 - 2",
                    exerciseNames = listOf(
                        "Incline Dumbbell Press",
                        "Bench Press",
                        "Dumbbell Fly",
                        "Lateral Raise",
                        "Overhead Triceps Extension",
                    ),
                ),
                RoutineDaySpec(
                    dayNumber = 6,
                    title = "Pull Day B - Độ Dày Lưng & Biceps",
                    subtitle = "5 bài tập • 18 hiệp • RPE 8-9",
                    targetRpe = "RIR 1 - 2",
                    exerciseNames = listOf(
                        "Pull-up",
                        "Barbell Row",
                        "Seated Cable Row",
                        "Hammer Curl",
                        "Barbell Curl",
                    ),
                ),
                RoutineDaySpec(
                    dayNumber = 7,
                    title = "Leg Day B - Đùi Sau & Mông Glutes",
                    subtitle = "5 bài tập • 17 hiệp • RPE 8-9",
                    targetRpe = "RIR 1 - 2",
                    exerciseNames = listOf(
                        "Romanian Deadlift",
                        "Hip Thrust",
                        "Walking Lunge",
                        "Leg Curl",
                        "Cable Crunch",
                    ),
                ),
            ),
        ),
        RoutinePlan(
            id = "ul_4d",
            name = "Upper / Lower 4 Days",
            subtitle = "Tối ưu phục hồi & siêu bù trừ (Supercompensation)",
            daysPerWeek = "4 ngày/tuần",
            durationText = "60-70m",
            frequencyText = "2x / tuần",
            badges = listOf("4 buổi / tuần", "Phục hồi tối đa", "Người bận rộn"),
            filterTag = "4day upperlower",
            days = listOf(
                RoutineDaySpec(
                    dayNumber = 1,
                    title = "Upper A - Ngực Nặng & Lưng Xô",
                    subtitle = "5 bài tập • 16 hiệp • RPE 8",
                    targetRpe = "RIR 2",
                    exerciseNames = listOf(
                        "Bench Press",
                        "Barbell Row",
                        "Overhead Press",
                        "Lat Pulldown",
                        "Barbell Curl",
                    ),
                ),
                RoutineDaySpec(
                    dayNumber = 2,
                    title = "Lower A - Squat & Bắp Chân",
                    subtitle = "4 bài tập • 15 hiệp • RPE 8",
                    targetRpe = "RIR 2",
                    exerciseNames = listOf(
                        "Squat",
                        "Romanian Deadlift",
                        "Leg Press",
                        "Calf Raise",
                    ),
                ),
                RoutineDaySpec(
                    dayNumber = 3,
                    title = "Upper B - Vai & Ngực Trên",
                    subtitle = "5 bài tập • 16 hiệp • RPE 8-9",
                    targetRpe = "RIR 1 - 2",
                    exerciseNames = listOf(
                        "Incline Dumbbell Press",
                        "Pull-up",
                        "Lateral Raise",
                        "Triceps Pushdown",
                        "Hammer Curl",
                    ),
                ),
                RoutineDaySpec(
                    dayNumber = 4,
                    title = "Lower B - Deadlift & Mông Đùi Sau",
                    subtitle = "4 bài tập • 15 hiệp • RPE 8-9",
                    targetRpe = "RIR 1 - 2",
                    exerciseNames = listOf(
                        "Deadlift",
                        "Hip Thrust",
                        "Leg Curl",
                        "Hanging Leg Raise",
                    ),
                ),
            ),
        ),
        RoutinePlan(
            id = "arnold_6d",
            name = "Arnold Split Cổ Điển",
            subtitle = "Ngực & Lưng / Vai & Tay / Chân (Antagonist Superset)",
            daysPerWeek = "6 ngày/tuần",
            durationText = "65-75m",
            frequencyText = "2x / tuần",
            badges = listOf("Volume Cao", "Cảm giác Pump cực đại"),
            filterTag = "6day",
            days = listOf(
                RoutineDaySpec(
                    dayNumber = 1,
                    title = "Ngày 1 & 4: Ngực & Lưng (Chest & Back)",
                    subtitle = "Superset đối kháng • 20 hiệp",
                    targetRpe = "RIR 1 - 2",
                    exerciseNames = listOf(
                        "Bench Press",
                        "Barbell Row",
                        "Incline Dumbbell Press",
                        "Pull-up",
                        "Dumbbell Fly",
                    ),
                ),
                RoutineDaySpec(
                    dayNumber = 2,
                    title = "Ngày 2 & 5: Vai & Cánh Tay (Shoulders & Arms)",
                    subtitle = "Superset tay trước + tay sau • 18 hiệp",
                    targetRpe = "RIR 1 - 2",
                    exerciseNames = listOf(
                        "Overhead Press",
                        "Lateral Raise",
                        "Barbell Curl",
                        "Overhead Triceps Extension",
                        "Hammer Curl",
                    ),
                ),
                RoutineDaySpec(
                    dayNumber = 3,
                    title = "Ngày 3 & 6: Toàn diện Chân & Bụng",
                    subtitle = "Squat, RDL, Leg Curl, Calves • 18 hiệp",
                    targetRpe = "RIR 1 - 2",
                    exerciseNames = listOf(
                        "Squat",
                        "Romanian Deadlift",
                        "Walking Lunge",
                        "Leg Curl",
                        "Calf Raise",
                    ),
                ),
            ),
        ),
        RoutinePlan(
            id = "fullbody_3d",
            name = "Full Body Hypertrophy 3 Days",
            subtitle = "Kích thích tổng hợp protein liên tục cho người mới & trung cấp",
            daysPerWeek = "3 ngày/tuần",
            durationText = "65-75m",
            frequencyText = "3x / tuần",
            badges = listOf("3 buổi / tuần", "Tần suất 3x / cơ bắp"),
            filterTag = "3day",
            days = listOf(
                RoutineDaySpec(
                    dayNumber = 1,
                    title = "Full Body A (Squat + Bench)",
                    subtitle = "5 bài tập • 16 hiệp",
                    targetRpe = "RIR 2",
                    exerciseNames = listOf(
                        "Squat",
                        "Bench Press",
                        "Barbell Row",
                        "Romanian Deadlift",
                        "Calf Raise",
                    ),
                ),
                RoutineDaySpec(
                    dayNumber = 2,
                    title = "Full Body B (Deadlift + OHP)",
                    subtitle = "5 bài tập • 16 hiệp",
                    targetRpe = "RIR 2",
                    exerciseNames = listOf(
                        "Deadlift",
                        "Overhead Press",
                        "Leg Press",
                        "Lat Pulldown",
                        "Cable Crunch",
                    ),
                ),
                RoutineDaySpec(
                    dayNumber = 3,
                    title = "Full Body C (Hypertrophy Pump)",
                    subtitle = "5 bài tập • 16 hiệp",
                    targetRpe = "RIR 1 - 2",
                    exerciseNames = listOf(
                        "Incline Dumbbell Press",
                        "Hip Thrust",
                        "Seated Cable Row",
                        "Lateral Raise",
                        "Barbell Curl",
                    ),
                ),
            ),
        ),
    )

    fun metaFor(exercise: Exercise): ExerciseMeta {
        return when (exercise.name) {
            "Bench Press" -> ExerciseMeta(
                vietnameseName = "Đẩy ngực ngang đòn tạ",
                tier = "Tier S",
                equipment = "Tạ đòn (Barbell)",
                isCompound = true,
                difficulty = "Trung cấp",
                setupSteps = listOf(
                    "5 Điểm tiếp xúc chuẩn (Base)" to "Hai bàn chân ghìm chặt sàn nhà, mông, lưng trên và đầu áp sát đệm ghế. Mắt gióng thẳng trục dưới đòn tạ.",
                    "Thu bả vai & Grip (Scapular Retraction)" to "Khóa chặt và ép hai bả vai xuống dưới, giữ vòm lưng tự nhiên. Độ rộng tay cầm khoảng 1.5 lần vai.",
                    "Đường tạ cong chữ J (Bar Path)" to "Hạ thanh đòn xuống điểm thấp nhất của xương ức. Cẳng tay vuông góc mặt sàn, góc nách khép 45°–60°.",
                    "Lực đẩy chân & Khóa nách (Leg Drive)" to "Đạp gót truyền lực lên hông đẩy tạ dứt khoát. Thở ra mạnh khi vượt qua điểm dính.",
                ),
                proTip = "Áp dụng nhịp tempo 3-0-1-0: Dành 3 giây hạ thanh tạ (Eccentric) để kéo giãn tối đa sợi cơ ngực dưới tải trọng, dừng nhẹ 0.5s ở ngực để triệt tiêu quán tính nảy.",
                redFlagTitle = "Khuỷu tay xòe ngang 90° (Flaring Elbows)",
                redFlagBody = "Tạo áp lực xoắn cực lớn lên chóp xoay vai (Rotator cuff), dễ dẫn đến viêm gân khớp vai.",
                warningTitle = "Nhấc mông khỏi mặt ghế khi đẩy nặng",
                warningBody = "Làm biến dạng đường cong thắt lưng, gây dồn phản lực đè nén vào đốt sống L4-L5.",
            )
            "Incline Dumbbell Press" -> ExerciseMeta(
                vietnameseName = "Đẩy ngực trên tạ đơn (Góc 30°)",
                tier = "Tier S",
                equipment = "Tạ đơn (Dumbbell)",
                isCompound = true,
                difficulty = "Trung cấp",
                setupSteps = listOf(
                    "Chỉnh ghế nghiêng 30°" to "Góc 30° tối ưu hóa bó cơ ngực trên (Clavicular head) mà không bị vai trước chiếm hết lực.",
                    "Đá đùi lên tạ an toàn" to "Đặt hai quả tạ lên đùi, hít sâu và đá từng chân đưa tạ về vị trí ngang vai.",
                    "Hạ sâu mở ngực" to "Hạ tạ có kiểm soát cho đến khi tạ ngang hõm ngực trên, khuỷu tay hơi thu vào 45°.",
                    "Đẩy hội tụ" to "Đẩy thẳng lên trên, không chạm mạnh hai đầu tạ vào nhau để giữ áp lực liên tục.",
                ),
                proTip = "Giữ cổ tay thẳng trục với cẳng tay và dừng 1 giây ở điểm căng nhất dưới đáy để kích thích cơ ngực trên.",
                redFlagTitle = "Dựng ghế quá cao (> 45°)",
                redFlagBody = "Biến bài đẩy ngực trên thành đẩy vai, làm giảm kích thích cơ ngực trên.",
                warningTitle = "Thả rơi tạ tự do ở pha âm",
                warningBody = "Dễ gây chấn thương gân cơ nhị đầu và khớp vai trước.",
            )
            "Squat" -> ExerciseMeta(
                vietnameseName = "Gánh đùi tạ đòn (Barbell Back Squat)",
                tier = "Tier S",
                equipment = "Tạ đòn (Barbell)",
                isCompound = true,
                difficulty = "Nâng cao",
                setupSteps = listOf(
                    "Đặt đòn lên cầu vai" to "Nắm chặt đòn, ép hai bả vai tạo đệm thịt vững chắc cho thanh đòn.",
                    "Hít bụng 360° (Valsalva)" to "Hít sâu vào bụng, gồng cứng core như chuẩn bị chịu đấm trước khi hạ người.",
                    "Mở gối theo mũi chân" to "Hạ hông và đẩy gối theo hướng mũi chân cho tới khi đùi song song hoặc sâu hơn mặt sàn.",
                    "Đạp sàn đứng dậy" to "Đạp mạnh cả bàn chân xuống sàn, giữ ngực và hông đi lên cùng tốc độ.",
                ),
                proTip = "Kê nhẹ gót chân hoặc mang giày đế phẳng/cứng để tối ưu biên độ gập cổ chân cho cơ đùi trước.",
                redFlagTitle = "Chụm gối vào trong (Knee Valgus)",
                redFlagBody = "Gây quá tải dây chằng chéo trước (ACL) và sụn chêm khớp gối.",
                warningTitle = "Cong lưng dưới (Butt Wink quá sớm)",
                warningBody = "Tăng áp lực đĩa đệm cột sống thắt lưng dưới tải nặng.",
            )
            "Deadlift" -> ExerciseMeta(
                vietnameseName = "Kéo tạ đòn từ sàn (Conventional Deadlift)",
                tier = "Tier S",
                equipment = "Tạ đòn (Barbell)",
                isCompound = true,
                difficulty = "Nâng cao",
                setupSteps = listOf(
                    "Đòn tạ sát ống đồng" to "Đứng rộng bằng hông, thanh đòn cắt ngang giữa bàn chân (cách ống đồng 2-3cm).",
                    "Kéo căng đòn (Pull the slack)" to "Hạ hông vừa chạm ống đồng vào đòn, ép xô như kẹp quả cam dưới nách.",
                    "Đạp sàn kéo dọc chân" to "Đạp mạnh chân xuống sàn, giữ thanh đòn trượt sát ống đồng và đùi.",
                    "Khóa hông chuẩn" to "Siết cơ mông đứng thẳng, không ngửa lưng quá mức ở đỉnh.",
                ),
                proTip = "Hãy nghĩ 'đạp sàn ra xa' trong nửa đầu hành trình thay vì giật tạ bằng lưng dưới.",
                redFlagTitle = "Gù lưng dưới khi rời sàn",
                redFlagBody = "Nguy cơ thoát vị đĩa đệm L4-L5 và L5-S1 rất cao.",
                warningTitle = "Giật đòn tạ bằng tay cong",
                warningBody = "Dễ gây đứt gân cơ nhị đầu tay (Biceps tear) khi dùng grip ngửa.",
            )
            else -> defaultMeta(exercise)
        }
    }

    private fun defaultMeta(exercise: Exercise): ExerciseMeta {
        val vnName = when (exercise.name) {
            "Dumbbell Fly" -> "Ép ngực tạ đơn"
            "Barbell Row" -> "Chèo tạ đòn gập người (Bent-over Row)"
            "Lat Pulldown" -> "Kéo xô máy cáp rộng tay"
            "Seated Cable Row" -> "Ngồi kéo cáp lưng giữa"
            "Pull-up" -> "Kéo xà đơn (Weighted Pull-up)"
            "Overhead Press" -> "Đẩy vai đứng tạ đòn (OHP)"
            "Lateral Raise" -> "Dang tạ đơn tập vai ngang"
            "Face Pull" -> "Kéo cáp dây thừng vai sau"
            "Barbell Curl" -> "Cuốn tạ đòn bắp tay trước"
            "Hammer Curl" -> "Cuốn tạ đơn hình búa"
            "Triceps Pushdown" -> "Duỗi tay sau với cáp"
            "Overhead Triceps Extension" -> "Duỗi tay sau qua đầu"
            "Dip" -> "Xà kép có tải (Weighted Dip)"
            "Leg Press" -> "Đạp đùi máy nghiêng 45°"
            "Walking Lunge" -> "Chùng chân bước đi với tạ"
            "Romanian Deadlift" -> "Kéo tạ đòn thẳng chân (RDL)"
            "Leg Curl" -> "Cuốn đùi sau máy"
            "Hip Thrust" -> "Đẩy hông tạ đòn tập mông"
            "Calf Raise" -> "Nhón bắp chân đứng"
            "Cable Crunch" -> "Gập bụng quỳ với dây cáp"
            "Hanging Leg Raise" -> "Treo người nâng chân tập bụng"
            else -> "Bài tập ${exercise.muscle.label}"
        }
        val isCompound = exercise.secondaryMuscles.isNotEmpty() || exercise.repMax <= 10
        val equipment = when {
            exercise.addedLoad -> "Bodyweight + Tạ treo"
            exercise.name.contains("Dumbbell") || exercise.name.contains("Hammer") || exercise.name.contains("Lateral") || exercise.name.contains("Lunge") -> "Tạ đơn (Dumbbell)"
            exercise.name.contains("Cable") || exercise.name.contains("Pulldown") || exercise.name.contains("Pushdown") || exercise.name.contains("Face Pull") -> "Dây cáp (Cable)"
            exercise.name.contains("Leg Press") || exercise.name.contains("Leg Curl") || exercise.name.contains("Calf") -> "Máy tạ (Machine)"
            else -> "Tạ đòn (Barbell)"
        }
        return ExerciseMeta(
            vietnameseName = vnName,
            tier = if (isCompound) "Tier S" else "Tier A",
            equipment = equipment,
            isCompound = isCompound,
            difficulty = if (isCompound) "Trung cấp" else "Cơ bản",
            setupSteps = listOf(
                "Ổn định tư thế & Khớp trụ" to "Cố định thân người, khóa chặt bả vai và gồng cơ lõi (core) trước khi phát lực.",
                "Kiểm soát pha hạ tạ (Eccentric)" to "Hạ tạ trong 2–3 giây theo đúng đường chuyển động tự nhiên của khớp.",
                "Điểm căng cơ tối đa" to "Dừng 0.5 giây ở vị trí cơ mục tiêu (${exercise.muscle.label}) giãn sâu nhất để triệt tiêu đà quán tính.",
                "Phát lực dứt khoát (Concentric)" to "Thở ra và phát lực mượt mà trong khoảng mục tiêu ${exercise.repMin}–${exercise.repMax} reps @ RIR 1–2.",
            ),
            proTip = "Duy trì Tempo 3-0-1-0 và dừng ở ngưỡng RIR 1–2 trong các hiệp chính để tối ưu kích thích Hypertrophy mà không gây quá tải thần kinh.",
            redFlagTitle = "Dùng đà giật tạ quá mức (Ego Lifting)",
            redFlagBody = "Làm giảm sức căng cơ học lên nhóm cơ ${exercise.muscle.label} và tăng nguy cơ chấn thương gân khớp.",
            warningTitle = "Cắt ngắn biên độ chuyển động (Half Reps)",
            warningBody = "Bỏ lỡ giai đoạn kéo giãn dưới tải trọng (Stretch-mediated hypertrophy) quan trọng nhất.",
        )
    }

    fun computeWeeklyLandmarks(
        exercises: List<Exercise>,
        sessions: List<Session>,
        sets: List<SetEntry>,
        weekOffset: Int = 0,
        nowMs: Long = System.currentTimeMillis(),
        zone: ZoneId = ZoneId.systemDefault(),
    ): Pair<String, List<MuscleVolumeLandmark>> {
        val today = Instant.ofEpochMilli(nowMs).atZone(zone).toLocalDate()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(weekOffset.toLong())
        val sunday = monday.plusDays(6)
        val startMs = monday.atStartOfDay(zone).toInstant().toEpochMilli()
        val endMs = sunday.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        val weekSessionIds = sessions
            .filter { it.startedAt in startMs until endMs }
            .map { it.id }
            .toSet()
        val weekSets = sets.filter { it.sessionId in weekSessionIds }
        val byExercise = exercises.associateBy { it.id }

        val directCounts = mutableMapOf<MuscleGroup, Double>()
        for (set in weekSets) {
            val ex = byExercise[set.exerciseId] ?: continue
            directCounts[ex.muscle] = (directCounts[ex.muscle] ?: 0.0) + 1.0
            for (sec in ex.secondaryMuscles) {
                directCounts[sec] = (directCounts[sec] ?: 0.0) + 0.5
            }
        }

        fun setsFor(vararg groups: MuscleGroup): Int {
            return groups.sumOf { directCounts[it] ?: 0.0 }.roundToInt()
        }

        val weekLabel = "Tuần (${monday.dayOfMonth}/${monday.monthValue} – ${sunday.dayOfMonth}/${sunday.monthValue})"
        val landmarks = listOf(
            MuscleVolumeLandmark(MuscleGroup.CHEST, "Ngực (Chest)", setsFor(MuscleGroup.CHEST), 10, 20),
            MuscleVolumeLandmark(MuscleGroup.BACK, "Lưng xô (Back)", setsFor(MuscleGroup.BACK), 10, 20),
            MuscleVolumeLandmark(MuscleGroup.QUADS, "Đùi trước (Quads)", setsFor(MuscleGroup.QUADS), 10, 18),
            MuscleVolumeLandmark(MuscleGroup.HAMSTRINGS, "Đùi sau & Mông (Hamstrings/Glutes)", setsFor(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES), 10, 16),
            MuscleVolumeLandmark(MuscleGroup.SHOULDERS, "Vai (Shoulders)", setsFor(MuscleGroup.SHOULDERS), 8, 16),
            MuscleVolumeLandmark(MuscleGroup.BICEPS, "Tay trước & Tay sau (Arms)", setsFor(MuscleGroup.BICEPS, MuscleGroup.TRICEPS), 8, 16),
            MuscleVolumeLandmark(MuscleGroup.CALVES, "Bắp chân & Bụng (Calves/Abs)", setsFor(MuscleGroup.CALVES, MuscleGroup.CORE), 8, 14),
        )
        return weekLabel to landmarks
    }

    fun computeRecoveryStatus(
        exercises: List<Exercise>,
        sets: List<SetEntry>,
        nowMs: Long = System.currentTimeMillis(),
    ): List<MuscleRecoveryItem> {
        val byExercise = exercises.associateBy { it.id }
        fun recoveryFor(label: String, vararg groups: MuscleGroup): MuscleRecoveryItem {
            val targetGroups = groups.toSet()
            val lastAt = sets.filter { set ->
                val ex = byExercise[set.exerciseId]
                ex != null && ex.muscle in targetGroups
            }.maxOfOrNull { it.completedAt }
            if (lastAt == null) {
                return MuscleRecoveryItem(
                    label = label,
                    percent = 100,
                    statusText = "100% Sẵn sàng",
                    isOptimal = true,
                )
            }
            val hours = ((nowMs - lastAt) / 3_600_000L).coerceAtLeast(1L)
            val pct = ((hours * 100L) / 48L).toInt().coerceIn(35, 100)
            val status = when {
                pct >= 100 -> "100% Sẵn sàng (${hours}h)"
                hours < 24 -> "$pct% Đang hồi phục (${hours}h trước)"
                else -> "$pct% Phục hồi (${hours}h)"
            }
            return MuscleRecoveryItem(
                label = label,
                percent = pct,
                statusText = status,
                isOptimal = pct >= 95,
            )
        }
        return listOf(
            recoveryFor("Ngực & Vai (Chest & Shoulders)", MuscleGroup.CHEST, MuscleGroup.SHOULDERS),
            recoveryFor("Tay sau (Triceps)", MuscleGroup.TRICEPS),
            recoveryFor("Lưng xô & Tay trước (Back & Biceps)", MuscleGroup.BACK, MuscleGroup.BICEPS),
            recoveryFor("Đùi & Mông (Quads & Glutes)", MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES),
        )
    }

    fun computeWeekStripAndStreak(
        sessions: List<Session>,
        sets: List<SetEntry>,
        nowMs: Long = System.currentTimeMillis(),
        zone: ZoneId = ZoneId.systemDefault(),
    ): Pair<Int, List<DayCheckItem>> {
        val nonEmptySessionIds = sets.map { it.sessionId }.toSet()
        val trainedDates = sessions
            .filter { it.id in nonEmptySessionIds }
            .map { Instant.ofEpochMilli(it.startedAt).atZone(zone).toLocalDate() }
            .toSet()
        val today = Instant.ofEpochMilli(nowMs).atZone(zone).toLocalDate()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val labels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
        val strip = labels.mapIndexed { idx, label ->
            val date = monday.plusDays(idx.toLong())
            DayCheckItem(
                label = label,
                date = date,
                completed = date in trainedDates,
                isToday = date == today,
            )
        }
        var streak = 0
        var cursor = if (today in trainedDates) today else today.minusDays(1)
        while (cursor in trainedDates || (streak > 0 && cursor.plusDays(1) in trainedDates && cursor.minusDays(1) in trainedDates)) {
            if (cursor in trainedDates) streak++
            cursor = cursor.minusDays(1)
            if (streak > 365) break
        }
        return streak to strip
    }
}
