/* ==========================================================================
   IronLog (GYMVERSE) — Interactive 3D Barbell, GSAP ScrollTrigger & Sandbox
   Resilient offline-first progressive enhancement with EN / VI localization
   ========================================================================== */
(function () {
  "use strict";

  /* ---------------- 1. BILINGUAL DICTIONARY (EN / VI) ---------------- */
  var I18N = {
    en: {
      "meta.title": "IronLog — Offline-First Hypertrophy & Strength System",
      "meta.desc": "IronLog (GYMVERSE) is an offline-first Android hypertrophy and strength log built with Kotlin, Jetpack Compose, Room SQLite WAL, and ForegroundService rest timers.",
      "skip": "Skip to content",
      "brand.sub": "GYMVERSE · ANDROID OFFLINE",
      "nav.signal": "01 Signal Drop",
      "nav.rest": "02 Rest Service",
      "nav.sandbox": "03 Live Log",
      "nav.arch": "04 Architecture",
      "nav.proto": "Live App ↗",
      "hero.kicker": "KOTLIN 2.0 · ROOM SQLITE WAL · API 27+",
      "hero.h1": "BUILT FOR CONCRETE BASEMENTS. <em>ZERO CLOUD LAG.</em>",
      "hero.lead": "IronLog is an offline-first hypertrophy and strength instrument for Android. Log heavy sets in reps × kg, track RIR &amp; Epley 1RM, keep a lock-screen ForegroundService rest timer running, and audit weekly MEV–MRV volume — with every byte committed locally on your device.",
      "btn.apk": "Download Android APK ↓",
      "btn.live": "Launch Live Interactive App ↗",
      "btn.tryInline": "Log a Set Below",
      "btn.fullProto": "Full Screen Prototype ↗",
      "hero.t1": "Room SQLite WAL local commit",
      "hero.t2": "Calibrated IPF collar stack",
      "hero.t3": "Vietnamese dishes macro table",
      "hero.t4": "Offline · zero account required",
      "stage.spec": "29MM KNURLED SHAFT · CALIBRATED STACK",
      "stage.explode": "EXPLODE AXIS",
      "s1.kicker": "PINNED NARRATIVE · OFFLINE-FIRST PERSISTENCE",
      "s1.h2": "THE BASEMENT SIGNAL DROP",
      "s1.lead": "Heavy squat racks live behind reinforced concrete where cellular reception dies. Scroll or step through the signal drop below to watch how IronLog commits a top set atomically to local Room SQLite (WAL) while cloud workout trackers hang and lose sets.",
      "s1.scrubHint": "Scroll to scrub reception · or inspect stage directly:",
      "s1.ironSub": "Zero network socket touched. Direct Kotlin Coroutine DAO transaction on local flash storage.",
      "s1.cloudLabel": "CLOUD-ONLY TRACKER",
      "band1.quote": "IRON DOES NOT WAIT FOR A RETRY SPINNER.",
      "band1.sub": "LOCAL-FIRST ANDROID ARCHITECTURE · ZERO ACCOUNT WALL",
      "s2.kicker": "PINNED NARRATIVE · ANDROID FOREGROUND SERVICE",
      "s2.h2": "SURVIVES SCREEN-OFF &amp; APP BACKGROUNDING",
      "s2.lead": "Ordinary web wrappers and naive timers get killed by Android Doze the moment you lock your phone and chalk your hands. IronLog promotes an ongoing ForegroundService notification that counts down accurately on your lock screen.",
      "s2.locked": "ANDROID LOCK SCREEN · SCREEN OFF",
      "s2.notifLift": "Next: Barbell Squat · 140 kg × 5",
      "s2.notifSub": "Target RIR 1–2 · Chime + Haptic at 00:00",
      "s2.pause": "Pause",
      "s2.resume": "Resume",
      "s2.skip": "Skip →",
      "s2.rightTitle": "HONEST CHRONOMETRY BETWEEN HEAVY SETS",
      "s2.rightLead": "Hypertrophy and strength progression depend on reproducible inter-set recovery. Whether you switch to your music player, answer a message, or pocket a locked phone for two and a half minutes, the countdown stays anchored to monotonic system uptime.",
      "s2.sp1t": "SystemClock.elapsedRealtime()",
      "s2.sp1d": "Immune to CPU throttling and deep sleep drift — remaining rest is computed against hardware boot timestamps rather than fragile UI intervals.",
      "s2.sp2t": "Direct Notification Actions",
      "s2.sp2d": "Add +30 seconds after a grinder set, pause visibly, or skip straight to your next set right from the Android lock-screen shade.",
      "s2.sp3t": "Explicit Guardrails",
      "s2.sp3d": "Finishing a workout with 0 logged sets or accidentally typing 600 kg instead of 60 kg triggers immediate confirmation guards before touching history.",
      "s3.kicker": "LIVE INTERACTIVE WORKBENCH · TRY ON PAGE",
      "s3.h2": "LOG A REAL SET. WATCH E1RM &amp; VOLUME REACT.",
      "s3.lead": "Adjust load, reps, and RPE below and commit a set. IronLog computes your Epley 1RM, checks progressive overload criteria, triggers the live rest timer, and updates weekly muscle-group sets against MEV–MRV landmarks immediately.",
      "s3.sessionTag": "ACTIVE SESSION · UPPER / LOWER HYPERTROPHY",
      "s3.weight": "LOAD (KG)",
      "s3.reps": "REPS",
      "s3.commitBtn": "Commit Set to Local Log +",
      "s3.openDrawer": "Open Full Android Prototype Drawer ↗",
      "s3.reset": "Reset",
      "s3.restIdle": "REST TIMER IDLE · AUTO-STARTS ON SET COMMIT",
      "s3.restRunning": "RESTING · FOREGROUND SERVICE ACTIVE",
      "s3.thLift": "Exercise",
      "s3.teleTitle": "EPLEY 1RM &amp; WEEKLY MEV–MRV TELEMETRY",
      "s3.e1rmLabel": "ESTIMATED 1RM (EPLEY)",
      "s3.overloadLabel": "NEXT SESSION DECISION",
      "s3.reasonPlus": "RIR ≥ 1.5 · Headroom confirmed",
      "s3.reasonHold": "Near failure (RIR ≤ 1) · Hold weight",
      "s3.volHeading": "WEEKLY SETS VS MEV–MRV LANDMARKS",
      "m.quads": "Quads &amp; Glutes",
      "m.chest": "Chest",
      "m.back": "Back &amp; Posterior",
      "m.shoulders": "Deltoids",
      "s3.nutritionNote": "Includes built-in Katch-McArdle / Mifflin-St Jeor TDEE calculator and an offline Vietnamese macro table (~90 dishes: Cơm tấm, Phở bò, Ức gà) plus optional Gemini meal photo parsing.",
      "band2.quote": "CHALK ON STEEL. NUMBERS IN STONE.",
      "band2.sub": "CSV SPREADSHEET EXPORT · FULL JSON BACKUP &amp; RESTORE",
      "s4.kicker": "SYSTEM ARCHITECTURE · KOTLIN &amp; JETPACK COMPOSE",
      "s4.h2": "ENGINEERED WITHOUT BLOAT",
      "s4.lead": "Designed and written by Trần Huy Hoàng from real training logs: unidirectional Compose state, deterministic hypertrophy math, local SQLite durability, and zero third-party analytics trackers.",
      "s4.n1": "Material 3 adaptive screens bound via lifecycle-aware StateFlow. Sub-frame recomposition for set tables and rest banners.",
      "s4.n2": "Pure Kotlin calculators for Epley e1RM, RIR progression gates, muscle recovery decay, and weekly MEV–MRV landmarks.",
      "s4.n3": "Write-Ahead Logging local database with Coroutine DAOs, 4 built-in splits (PPL, Upper/Lower, Arnold, Full Body), CSV &amp; JSON export.",
      "s4.n4": "Dedicated Android Service + Notification chronometer surviving lock screen, app switching, and aggressive battery savers.",
      "s4.cap1": "CALIBRATED HARDWARE INSPIRATION — Raw steel knurling, competition red 25 kg plates, and machined brass collars.",
      "s4.makerKicker": "SOLO ENGINEERED · OPEN SOURCE MIT",
      "s4.makerTitle": "FROM THE GYM FLOOR TO EVERY LINE OF KOTLIN.",
      "s4.makerBody": "Built by Trần Huy Hoàng. Every screen in IronLog earned its place during real hypertrophy blocks — answering what load you moved last week, how recovered each muscle group is today, and when you are ready to pull the next set.",
      "drawer.expand": "Full Page ↗",
      "drawer.close": "Close ✕",
      "foot.copy": "Designed &amp; engineered by Trần Huy Hoàng. Android 8.1+ (API 27+) · Kotlin · Jetpack Compose · Room SQLite. Sample telemetry shown for interactive demonstration."
    },
    vi: {
      "meta.title": "IronLog — Hệ Thống Nhật Ký Tập Hypertrophy & Sức Mạnh Offline-First",
      "meta.desc": "IronLog (GYMVERSE) là ứng dụng Android ghi nhật ký tập gym offline-first viết bằng Kotlin, Jetpack Compose, Room SQLite WAL và đồng hồ nghỉ ForegroundService.",
      "skip": "Chuyển tới nội dung chính",
      "brand.sub": "GYMVERSE · ANDROID OFFLINE",
      "nav.signal": "01 Mất Sóng Hầm",
      "nav.rest": "02 Đồng Hồ Nền",
      "nav.sandbox": "03 Ghi Hiệp Thử",
      "nav.arch": "04 Kiến Trúc",
      "nav.proto": "Mở App ↗",
      "hero.kicker": "KOTLIN 2.0 · ROOM SQLITE WAL · API 27+",
      "hero.h1": "ĐÚC CHO PHÒNG TẬP TẦNG HẦM. <em>KHÔNG ĐỘ TRỄ MÂY.</em>",
      "hero.lead": "IronLog là thiết bị ghi nhật ký hypertrophy &amp; sức mạnh offline-first trên Android. Ghi nhanh hiệp nặng theo rep × kg, tính RIR &amp; Epley 1RM, giữ đồng hồ nghỉ ForegroundService ngay trên màn hình khoá và đối chiếu volume tuần MEV–MRV — lưu 100% trực tiếp trên máy.",
      "btn.apk": "Tải APK Android ↓",
      "btn.live": "Mở App Tương Tác Trực Tiếp ↗",
      "btn.tryInline": "Ghi Thử Một Hiệp Bên Dưới",
      "btn.fullProto": "Mở Prototype Toàn Màn Hình ↗",
      "hero.t1": "Ghi Room SQLite WAL nội bộ",
      "hero.t2": "Bộ bánh tạ chuẩn thi đấu IPF",
      "hero.t3": "Món Việt có sẵn bảng dinh dưỡng",
      "hero.t4": "Lưu offline · không cần tài khoản",
      "stage.spec": "ĐÒN THÉP NHÁM 29MM · BÁNH TẠ CHUẨN IPF",
      "stage.explode": "TÁCH TRỤC TẠ",
      "s1.kicker": "KỊCH BẢN CUỘN GHIM · LƯU TRỮ OFFLINE-FIRST",
      "s1.h2": "KHI TẦNG HẦM MẤT SẠCH SÓNG",
      "s1.lead": "Khung squat nặng thường nằm sâu dưới tầng hầm bê tông nơi sóng 4G tắt hẳn. Cuộn hoặc bấm qua từng nấc bên dưới để thấy IronLog ghi tức thì hiệp 140 kg × 5 vào Room SQLite (WAL) trong < 2.4ms, trong khi app phụ thuộc đám mây treo cứng.",
      "s1.scrubHint": "Cuộn để mô phỏng rớt sóng · hoặc chọn từng mức:",
      "s1.ironSub": "Không mở kết nối mạng. Giao dịch DAO Kotlin Coroutine ghi thẳng vào bộ nhớ flash trên máy.",
      "s1.cloudLabel": "APP GHI TẬP ĐÁM MÂY",
      "band1.quote": "TẠ NẶNG KHÔNG ĐỢI VÒNG XOAY MẤT MẠNG.",
      "band1.sub": "KIẾN TRÚC ANDROID LOCAL-FIRST · KHÔNG TƯỜNG ĐĂNG NHẬP",
      "s2.kicker": "KỊCH BẢN CUỘN GHIM · ANDROID FOREGROUND SERVICE",
      "s2.h2": "ĐẾM CHUẨN NGAY CẢ KHI TẮT MÀN HÌNH",
      "s2.lead": "Các web-app bọc tạm sẽ bị Android Doze đóng băng ngay khi bạn khoá máy và xoa phấn. IronLog ghim thông báo ForegroundService chạy nền giúp đồng hồ nghỉ đếm chính xác trên màn hình khoá.",
      "s2.locked": "MÀN HÌNH KHOÁ ANDROID · ĐÃ TẮT SÁNG",
      "s2.notifLift": "Tiếp theo: Barbell Squat · 140 kg × 5",
      "s2.notifSub": "Mục tiêu RIR 1–2 · Chuông + Rung ở 00:00",
      "s2.pause": "Tạm dừng",
      "s2.resume": "Tiếp tục",
      "s2.skip": "Bỏ qua →",
      "s2.rightTitle": "ĐỒNG HỒ NGHỈ TRUNG THỰC GIỮA CÁC HIỆP NẶNG",
      "s2.rightLead": "Tiến bộ sức mạnh đòi hỏi thời gian nghỉ nhất quán. Dù bạn chuyển sang đổi nhạc, trả lời tin nhắn hay bỏ điện thoại đã khoá vào túi suốt hai phút rưỡi, bộ đếm vẫn bám chặt mốc thời gian phần cứng.",
      "s2.sp1t": "SystemClock.elapsedRealtime()",
      "s2.sp1d": "Không lệch nhịp khi CPU hạ xung — thời gian còn lại đối chiếu trực tiếp với mốc uptime phần cứng thay vì vòng lặp UI mỏng manh.",
      "s2.sp2t": "Thao Tác Ngay Trên Thông Báo",
      "s2.sp2d": "Cộng thêm +30 giây sau một hiệp vắt kiệt sức, tạm dừng rõ ràng hoặc bỏ qua ngay trên màn hình khoá Android.",
      "s2.sp3t": "Chốt Chặn Chống Nhập Nhầm",
      "s2.sp3d": "Bấm kết thúc khi chưa ghi hiệp nào hoặc gõ nhầm 600 kg thay vì 60 kg đều được chặn hỏi lại rõ ràng trước khi lưu.",
      "s3.kicker": "BÀN GHI TẬP TƯƠNG TÁC · THỬ TRỰC TIẾP",
      "s3.h2": "GHI THỬ MỘT HIỆP. XEM E1RM &amp; VOLUME CẬP NHẬT.",
      "s3.lead": "Chỉnh mức tạ, số rep và RPE bên dưới rồi bấm ghi hiệp. IronLog tính ngay Epley 1RM, đưa gợi ý tăng tạ buổi tới, kích hoạt đồng hồ nghỉ và cộng số hiệp vào thang MEV–MRV trong tuần.",
      "s3.sessionTag": "PHIÊN ĐANG CHẠY · UPPER / LOWER HYPERTROPHY",
      "s3.weight": "MỨC TẠ (KG)",
      "s3.reps": "SỐ REP",
      "s3.commitBtn": "Ghi Hiệp Vào Nhật Ký +",
      "s3.openDrawer": "Mở Ngăn Kéo Prototype Android ↗",
      "s3.reset": "Đặt lại",
      "s3.restIdle": "ĐỒNG HỒ NGHỈ CHỜ · TỰ CHẠY KHI GHI HIỆP",
      "s3.restRunning": "ĐANG NGHỈ · DỊCH VỤ NỀN ĐANG CHẠY",
      "s3.thLift": "Bài tập",
      "s3.teleTitle": "CHỈ SỐ EPLEY 1RM &amp; VOLUME TUẦN MEV–MRV",
      "s3.e1rmLabel": "1RM ƯỚC TÍNH (EPLEY)",
      "s3.overloadLabel": "QUYẾT ĐỊNH BUỔI TỚI",
      "s3.reasonPlus": "RIR ≥ 1.5 · Còn dư lực để tăng tạ",
      "s3.reasonHold": "Sát ngưỡng thất bại (RIR ≤ 1) · Giữ mức tạ",
      "s3.volHeading": "SỐ HIỆP TUẦN SO VỚI NGƯỠNG MEV–MRV",
      "m.quads": "Đùi trước &amp; Mông",
      "m.chest": "Ngực",
      "m.back": "Lưng &amp; Đùi sau",
      "m.shoulders": "Vai (Deltoids)",
      "s3.nutritionNote": "Tích hợp sẵn thẻ BMR/TDEE (Katch-McArdle, Mifflin-St Jeor) và bảng ~90 món ăn Việt offline (Cơm tấm, Phở bò, Ức gà) kèm nhận diện bữa ăn qua Gemini.",
      "band2.quote": "PHẤN TRẮNG TRÊN THÉP. SỐ LIỆU RÕ RÀNG.",
      "band2.sub": "XUẤT BẢNG TÍNH CSV · SAO LƯU &amp; KHÔI PHỤC JSON TOÀN DIỆN",
      "s4.kicker": "KIẾN TRÚC HỆ THỐNG · KOTLIN &amp; JETPACK COMPOSE",
      "s4.h2": "THIẾT KẾ GỌN CHẮC, KHÔNG DƯ THỪA",
      "s4.lead": "Do Trần Huy Hoàng trực tiếp thiết kế và lập trình từ nhật ký tập thực tế: luồng trạng thái Compose một chiều, công thức hypertrophy minh bạch, SQLite bền bỉ trên máy và không gắn bất kỳ trình theo dõi quảng cáo nào.",
      "s4.n1": "Giao diện Material 3 kết nối StateFlow theo vòng đời. Cập nhật bảng hiệp và thanh nghỉ mượt tức thì.",
      "s4.n2": "Bộ tính Kotlin thuần cho Epley e1RM, ngưỡng RIR tăng tạ, độ phục hồi nhóm cơ và khoảng MEV–MRV.",
      "s4.n3": "Cơ sở dữ liệu Room chế độ WAL cùng Coroutine DAO, 4 giáo án sẵn (PPL, Upper/Lower, Arnold, Full Body), xuất CSV &amp; JSON.",
      "s4.n4": "ForegroundService độc lập giữ thông báo đếm ngược sống sót qua khoá màn hình, chuyển ứng dụng và chế độ tiết kiệm pin.",
      "s4.cap1": "CẢM HỨNG VẬT LIỆU CƠ KHÍ — Thép đòn nhám kim cương, bánh tạ đỏ 25 kg chuẩn IPF và khoá đồng thau tiện CNC.",
      "s4.makerKicker": "PHÁT TRIỂN ĐỘC LẬP · MÃ NGUỒN MỞ MIT",
      "s4.makerTitle": "TỪ SÀN TẬP TỚI TỪNG DÒNG CODE KOTLIN.",
      "s4.makerBody": "Phát triển bởi Trần Huy Hoàng. Từng màn hình trong IronLog đều ra đời từ nhu cầu thật giữa các hiệp tập nặng — trả lời nhanh tuần trước đẩy bao nhiêu kg, nhóm cơ nào đã hồi phục và bao lâu nữa vào hiệp kế tiếp.",
      "drawer.expand": "Mở Trang Riêng ↗",
      "drawer.close": "Đóng ✕",
      "foot.copy": "Thiết kế &amp; phát triển bởi Trần Huy Hoàng. Android 8.1+ (API 27+) · Kotlin · Jetpack Compose · Room SQLite. Số liệu trên web là dữ liệu mẫu phục vụ trải nghiệm tương tác."
    }
  };

  var SIGNAL_STAGES = {
    en: [
      {
        level: "4",
        carrier: "4G LTE · -78 dBm",
        loc: "Ground Lobby · Entering Gym",
        badge: "ONLINE",
        badgeClass: "",
        title: "Approaching Top Set: Barbell Back Squat",
        desc: "Warm-ups complete. Pre-filled from last week’s local Room history: 137.5 kg × 5 @ RPE 8.5. Today’s loaded bar: 140.0 kg × 5.",
        ironLatency: "1.6 ms COMMIT",
        cloudLatency: "340 ms",
        cloudTitle: "Session active on 4G",
        cloudSub: "Cloud workout app fetches exercise catalog over HTTPS while reception still holds near street level.",
        cloudNet: "HTTP 200 OK · session token valid",
        cloudErr: "> Waiting for next set submission..."
      },
      {
        level: "1",
        carrier: "1 BAR EDGE · -114 dBm",
        loc: "Sub-Level B1 · Concrete Stairwell",
        badge: "PACKET LOSS 78%",
        badgeClass: "is-warn",
        title: "Unracking Under Weak Basement Signal",
        desc: "Reinforced concrete attenuates cellular signal to -114 dBm. Cloud-dependent apps stall on auth refresh and lock the set button behind a spinner.",
        ironLatency: "1.9 ms COMMIT",
        cloudLatency: "6,820 ms…",
        cloudTitle: "Retrying POST /v2/sets…",
        cloudSub: "TCP packets drop mid-handshake; UI blocks input while waiting on remote server acknowledgement.",
        cloudNet: "TLS handshake retry (2/3)...",
        cloudErr: "> WARN: Socket read timeout after 5000ms"
      },
      {
        level: "0",
        carrier: "0 BARS · AIRPLANE / DEAD ZONE",
        loc: "Basement Squat Rack · Zero Signal",
        badge: "0 BARS · OFFLINE",
        badgeClass: "is-zero",
        title: "140 kg × 5 @ RPE 8.5 — Committed in 1.8 ms",
        desc: "Zero cell reception. IronLog writes atomically to on-device Room SQLite (WAL mode), updates Epley e1RM to 163.3 kg, and fires the ForegroundService rest timer immediately.",
        ironLatency: "1.8 ms WAL OK",
        cloudLatency: "TIMEOUT (504)",
        cloudTitle: "Failed to save set",
        cloudSub: "Request aborted when screen locked; uncached set payload lost before reaching database.",
        cloudNet: "Retry 3/3 failed · Network unreachable",
        cloudErr: "> ERR_INTERNET_DISCONNECTED · Set lost"
      }
    ],
    vi: [
      {
        level: "4",
        carrier: "4G LTE · -78 dBm",
        loc: "Sảnh Tầng Trệt · Bước Vào Phòng Tập",
        badge: "CÓ MẠNG",
        badgeClass: "",
        title: "Chuẩn Bị Hiệp Chính: Barbell Back Squat",
        desc: "Khởi động xong. Dữ liệu buổi trước nạp sẵn từ Room nội bộ: 137.5 kg × 5 @ RPE 8.5. Mức tạ lắp trên đòn hôm nay: 140.0 kg × 5.",
        ironLatency: "1.6 ms ĐÃ LƯU",
        cloudLatency: "340 ms",
        cloudTitle: "Đang kết nối 4G ở sảnh",
        cloudSub: "App đám mây tải danh mục bài qua HTTPS khi vẫn còn đứng gần cửa tầng trệt.",
        cloudNet: "HTTP 200 OK · phiên đăng nhập hợp lệ",
        cloudErr: "> Đang chờ gửi hiệp tiếp theo..."
      },
      {
        level: "1",
        carrier: "1 VẠCH YẾU · -114 dBm",
        loc: "Cầu Thang Hầm B1 · Tường Bê Tông",
        badge: "RỚT GÓI 78%",
        badgeClass: "is-warn",
        title: "Ra Đòn Giữa Vùng Sóng Chập Chờn",
        desc: "Sàn bê tông cốt thép làm suy hao tín hiệu xuống -114 dBm. App phụ thuộc máy chủ bắt đầu xoay vòng làm mới token và khoá nút lưu.",
        ironLatency: "1.9 ms ĐÃ LƯU",
        cloudLatency: "6,820 ms…",
        cloudTitle: "Đang thử lại POST /v2/sets…",
        cloudSub: "Gói tin TCP rớt liên tục; giao diện treo cứng chờ phản hồi từ server từ xa.",
        cloudNet: "Thử lại bắt tay TLS (2/3)...",
        cloudErr: "> CẢNH BÁO: Quá hạn đọc socket 5000ms"
      },
      {
        level: "0",
        carrier: "0 VẠCH · MẤT SÓNG / MÁY BAY",
        loc: "Khung Squat Hầm B2 · Chết Sóng Hoàn Toàn",
        badge: "0 VẠCH · OFFLINE",
        badgeClass: "is-zero",
        title: "140 kg × 5 @ RPE 8.5 — Lưu Xong Trong 1.8 ms",
        desc: "Không còn một vạch sóng. IronLog ghi nguyên tử thẳng vào Room SQLite (chế độ WAL), cập nhật e1RM lên 163.3 kg và bật ngay đồng hồ nghỉ chạy nền.",
        ironLatency: "1.8 ms WAL OK",
        cloudLatency: "LỖI MẠNG (504)",
        cloudTitle: "Không thể lưu hiệp tập",
        cloudSub: "Yêu cầu bị huỷ khi khoá màn hình; dữ liệu hiệp chưa kịp đồng bộ bị mất trắng.",
        cloudNet: "Thất bại 3/3 · Không có kết nối Internet",
        cloudErr: "> ERR_INTERNET_DISCONNECTED · Mất hiệp"
      }
    ]
  };

  var currentLang = "en";
  var currentSignalStep = 2;

  /* ---------------- LANGUAGE SWITCHER ---------------- */
  var LANG_KEY = "ironlog-lang";
  var langToggle = document.getElementById("langToggle");
  var langMenu = document.getElementById("langMenu");
  var langCode = document.getElementById("langCode");
  var langOpts = langMenu ? Array.prototype.slice.call(langMenu.querySelectorAll(".lang-opt")) : [];

  function t(key) {
    var dict = I18N[currentLang] || I18N.en;
    return dict[key] != null ? dict[key] : (I18N.en[key] || key);
  }

  function applySignalStep(idx) {
    currentSignalStep = Math.max(0, Math.min(2, idx));
    var list = SIGNAL_STAGES[currentLang] || SIGNAL_STAGES.en;
    var st = list[currentSignalStep];
    var bars = document.getElementById("signalBars");
    var carrier = document.getElementById("signalCarrier");
    var loc = document.getElementById("signalLocation");
    var badge = document.getElementById("signalBadge");
    var title = document.getElementById("signalStageTitle");
    var desc = document.getElementById("signalStageDesc");
    var ironLat = document.getElementById("ironLatencyBadge");
    var cloudLat = document.getElementById("cloudLatencyBadge");
    var cloudHead = document.getElementById("cloudStateHeading");
    var cloudSub = document.getElementById("cloudStateSub");
    var cloudNet = document.getElementById("cloudNetLine");
    var cloudErr = document.getElementById("cloudErrLine");

    if (bars) bars.setAttribute("data-level", st.level);
    if (carrier) carrier.textContent = st.carrier;
    if (loc) loc.textContent = st.loc;
    if (badge) {
      badge.textContent = st.badge;
      badge.className = "signal-badge" + (st.badgeClass ? " " + st.badgeClass : "");
    }
    if (title) title.textContent = st.title;
    if (desc) desc.textContent = st.desc;
    if (ironLat) ironLat.textContent = st.ironLatency;
    if (cloudLat) cloudLat.textContent = st.cloudLatency;
    if (cloudHead) cloudHead.textContent = st.cloudTitle;
    if (cloudSub) cloudSub.textContent = st.cloudSub;
    if (cloudNet) cloudNet.textContent = st.cloudNet;
    if (cloudErr) cloudErr.textContent = st.cloudErr;

    document.querySelectorAll("[data-signal-step]").forEach(function (btn) {
      var active = Number(btn.getAttribute("data-signal-step")) === currentSignalStep;
      btn.classList.toggle("is-active", active);
    });
  }

  function applyLang(lang) {
    currentLang = lang === "vi" ? "vi" : "en";
    var dict = I18N[currentLang];
    document.documentElement.lang = currentLang;
    document.title = dict["meta.title"];
    var metaDesc = document.querySelector('meta[name="description"]');
    if (metaDesc) metaDesc.setAttribute("content", dict["meta.desc"]);

    document.querySelectorAll("[data-i18n]").forEach(function (el) {
      var k = el.getAttribute("data-i18n");
      if (dict[k] != null) el.innerHTML = dict[k];
    });

    if (langCode) langCode.textContent = currentLang.toUpperCase();
    langOpts.forEach(function (opt) {
      var isSel = opt.getAttribute("data-lang") === currentLang;
      opt.classList.toggle("is-active", isSel);
      opt.setAttribute("aria-selected", isSel ? "true" : "false");
    });

    applySignalStep(currentSignalStep);
    updateSandboxMetrics();
    updatePauseBtnLabel();
    try { localStorage.setItem(LANG_KEY, currentLang); } catch (e) { /* ignore */ }
  }

  if (langToggle && langMenu) {
    langToggle.addEventListener("click", function (ev) {
      ev.stopPropagation();
      var open = !langMenu.hidden;
      langMenu.hidden = open;
      langToggle.setAttribute("aria-expanded", open ? "false" : "true");
    });
    langOpts.forEach(function (opt) {
      opt.addEventListener("click", function () {
        applyLang(opt.getAttribute("data-lang"));
        langMenu.hidden = true;
        langToggle.setAttribute("aria-expanded", "false");
      });
    });
    document.addEventListener("click", function (ev) {
      if (!langMenu.hidden && !ev.target.closest(".lang-switch")) {
        langMenu.hidden = true;
        langToggle.setAttribute("aria-expanded", "false");
      }
    });
  }

  document.querySelectorAll("[data-signal-step]").forEach(function (btn) {
    btn.addEventListener("click", function () {
      applySignalStep(Number(btn.getAttribute("data-signal-step")));
    });
  });

  /* ---------------- 2. THREE.JS 3D OLYMPIC BARBELL & CALIBRATED PLATES ---------------- */
  var reduceMotion = window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  var explodeState = { value: 0.18 };
  var explodeSlider = document.getElementById("explodeSlider");
  var barbellReadout = document.getElementById("barbellLoadReadout");

  function updateBarbellHud(val) {
    var pct = Math.round(val * 100);
    if (explodeSlider && document.activeElement !== explodeSlider) {
      explodeSlider.value = String(pct);
    }
    if (barbellReadout) {
      if (pct <= 6) {
        barbellReadout.textContent = currentLang === "vi"
          ? "TẢI: 165.0 KG · ĐÃ KHÓA CỔ TẠ"
          : "LOAD: 165.0 KG · LOCKED & ASSEMBLED";
      } else {
        barbellReadout.textContent = (currentLang === "vi" ? "TÁCH TRỤC +" : "EXPLODED +") + pct + "% · 165.0 KG";
      }
    }
  }

  (function initOlympicBarbell3D() {
    var canvas = document.getElementById("barbellCanvas");
    var fallback = document.getElementById("barbellFallback");
    if (!canvas) return;

    function activateFallback() {
      canvas.style.display = "none";
      if (fallback) fallback.hidden = false;
    }

    if (!window.THREE || reduceMotion) {
      activateFallback();
      return;
    }

    var renderer;
    try {
      renderer = new THREE.WebGLRenderer({
        canvas: canvas,
        alpha: true,
        antialias: true,
        powerPreference: "high-performance"
      });
    } catch (err) {
      activateFallback();
      return;
    }

    renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2));
    if (renderer.toneMapping !== undefined && THREE.ACESFilmicToneMapping !== undefined) {
      renderer.toneMapping = THREE.ACESFilmicToneMapping;
      renderer.toneMappingExposure = 1.18;
    }

    var scene = new THREE.Scene();
    var camera = new THREE.PerspectiveCamera(34, 1, 0.1, 100);
    camera.position.set(0, 0.55, 8.6);

    /* Procedural diamond-knurl bump & roughness texture */
    var knurlTex = null;
    try {
      var kc = document.createElement("canvas");
      kc.width = 128;
      kc.height = 128;
      var kctx = kc.getContext("2d");
      kctx.fillStyle = "#808080";
      kctx.fillRect(0, 0, 128, 128);
      kctx.strokeStyle = "#d8d8d8";
      kctx.lineWidth = 2;
      for (var i = -128; i < 256; i += 8) {
        kctx.beginPath();
        kctx.moveTo(i, 0);
        kctx.lineTo(i + 128, 128);
        kctx.stroke();
        kctx.beginPath();
        kctx.moveTo(i + 128, 0);
        kctx.lineTo(i, 128);
        kctx.stroke();
      }
      knurlTex = new THREE.CanvasTexture(kc);
      knurlTex.wrapS = THREE.RepeatWrapping;
      knurlTex.wrapT = THREE.RepeatWrapping;
      knurlTex.repeat.set(18, 4);
    } catch (e) { /* optional procedural map */ }

    /* PBR Materials matching Cast-Iron, Knurled Steel, Brass & Calibrated Plates */
    var shaftSteelMat = new THREE.MeshStandardMaterial({
      color: 0xaab0ba,
      metalness: 0.9,
      roughness: 0.34,
      bumpMap: knurlTex,
      bumpScale: 0.025
    });
    var chromeSleeveMat = new THREE.MeshStandardMaterial({
      color: 0xc9ced6,
      metalness: 0.94,
      roughness: 0.2
    });
    var brassMat = new THREE.MeshStandardMaterial({
      color: 0xd99b26,
      metalness: 0.86,
      roughness: 0.28
    });
    var red25Mat = new THREE.MeshStandardMaterial({
      color: 0xc83e2b,
      metalness: 0.35,
      roughness: 0.44
    });
    var gunmetal20Mat = new THREE.MeshStandardMaterial({
      color: 0x262930,
      metalness: 0.72,
      roughness: 0.38
    });
    var amber15Mat = new THREE.MeshStandardMaterial({
      color: 0xd99b26,
      metalness: 0.45,
      roughness: 0.4
    });
    var iron10Mat = new THREE.MeshStandardMaterial({
      color: 0x16181c,
      metalness: 0.58,
      roughness: 0.55
    });

    var assembly = new THREE.Group();
    scene.add(assembly);

    /* 29mm Olympic Bar Shaft */
    var shaft = new THREE.Mesh(new THREE.CylinderGeometry(0.068, 0.068, 6.5, 36), shaftSteelMat);
    shaft.rotation.z = Math.PI / 2;
    assembly.add(shaft);

    /* IPF 81cm Grip Rings */
    [-0.95, 0.95].forEach(function (rx) {
      var gripRing = new THREE.Mesh(new THREE.TorusGeometry(0.07, 0.008, 12, 36), brassMat);
      gripRing.rotation.y = Math.PI / 2;
      gripRing.position.x = rx;
      assembly.add(gripRing);
    });

    /* Rotating Outer Sleeves + Brass Bushings */
    [-1, 1].forEach(function (side) {
      var flange = new THREE.Mesh(new THREE.CylinderGeometry(0.21, 0.21, 0.09, 36), brassMat);
      flange.rotation.z = Math.PI / 2;
      flange.position.x = side * 1.46;
      assembly.add(flange);

      var sleeve = new THREE.Mesh(new THREE.CylinderGeometry(0.115, 0.115, 1.78, 36), chromeSleeveMat);
      sleeve.rotation.z = Math.PI / 2;
      sleeve.position.x = side * 2.38;
      assembly.add(sleeve);

      var endCap = new THREE.Mesh(new THREE.CylinderGeometry(0.105, 0.105, 0.04, 32), brassMat);
      endCap.rotation.z = Math.PI / 2;
      endCap.position.x = side * 3.28;
      assembly.add(endCap);
    });

    /* Calibrated Competition Plate Stack (Mirrored Left/Right) */
    var plateDefs = [
      { kg: 25, r: 1.24, w: 0.16, baseX: 1.60, spread: 0.22, mat: red25Mat, rimMat: chromeSleeveMat },
      { kg: 20, r: 1.24, w: 0.14, baseX: 1.77, spread: 0.56, mat: gunmetal20Mat, rimMat: brassMat },
      { kg: 15, r: 1.08, w: 0.125, baseX: 1.92, spread: 0.92, mat: amber15Mat, rimMat: iron10Mat },
      { kg: 10, r: 0.90, w: 0.11, baseX: 2.06, spread: 1.26, mat: iron10Mat, rimMat: chromeSleeveMat }
    ];

    var animatedParts = [];

    [-1, 1].forEach(function (side) {
      plateDefs.forEach(function (spec, idx) {
        var pGroup = new THREE.Group();

        var disc = new THREE.Mesh(new THREE.CylinderGeometry(spec.r, spec.r, spec.w, 56), spec.mat);
        disc.rotation.z = Math.PI / 2;
        pGroup.add(disc);

        /* Raised Competition Perimeter Lip */
        var lip = new THREE.Mesh(new THREE.TorusGeometry(spec.r - 0.03, 0.028, 14, 64), spec.mat);
        lip.rotation.y = Math.PI / 2;
        lip.position.x = side * (spec.w * 0.45);
        pGroup.add(lip);

        /* Calibrated Center Hub Ring */
        var hub = new THREE.Mesh(new THREE.CylinderGeometry(0.24, 0.24, spec.w + 0.014, 32), spec.rimMat);
        hub.rotation.z = Math.PI / 2;
        pGroup.add(hub);

        pGroup.position.x = side * spec.baseX;
        assembly.add(pGroup);
        animatedParts.push({
          mesh: pGroup,
          side: side,
          baseX: spec.baseX,
          spread: spec.spread,
          spinFactor: (idx + 1) * 0.12
        });
      });

      /* 2.5 kg Competition Lock Collar + Spin Lever */
      var collarGroup = new THREE.Group();
      var collarBody = new THREE.Mesh(new THREE.CylinderGeometry(0.22, 0.22, 0.18, 32), brassMat);
      collarBody.rotation.z = Math.PI / 2;
      collarGroup.add(collarBody);

      var collarGrip = new THREE.Mesh(new THREE.TorusGeometry(0.22, 0.018, 12, 36), iron10Mat);
      collarGrip.rotation.y = Math.PI / 2;
      collarGroup.add(collarGrip);

      var lever = new THREE.Mesh(new THREE.BoxGeometry(0.06, 0.26, 0.045), chromeSleeveMat);
      lever.position.set(0, 0.24, 0);
      lever.rotation.z = side * -0.25;
      collarGroup.add(lever);

      collarGroup.position.x = side * 2.24;
      assembly.add(collarGroup);
      animatedParts.push({
        mesh: collarGroup,
        side: side,
        baseX: 2.24,
        spread: 1.62,
        spinFactor: 0.85,
        isCollar: true
      });
    });

    /* Soft Radial Lifting Chalk Dust Particles */
    var spriteTex = null;
    try {
      var sc = document.createElement("canvas");
      sc.width = 32;
      sc.height = 32;
      var sctx = sc.getContext("2d");
      var grad = sctx.createRadialGradient(16, 16, 1, 16, 16, 15);
      grad.addColorStop(0, "rgba(243,239,230,0.95)");
      grad.addColorStop(0.5, "rgba(243,239,230,0.35)");
      grad.addColorStop(1, "rgba(243,239,230,0)");
      sctx.fillStyle = grad;
      sctx.fillRect(0, 0, 32, 32);
      spriteTex = new THREE.CanvasTexture(sc);
    } catch (e) { /* ignore */ }

    var dustCount = 150;
    var dustGeo = new THREE.BufferGeometry();
    var dustPos = new Float32Array(dustCount * 3);
    for (var d = 0; d < dustCount; d++) {
      dustPos[d * 3] = (Math.random() - 0.5) * 9.5;
      dustPos[d * 3 + 1] = (Math.random() - 0.5) * 4.5;
      dustPos[d * 3 + 2] = (Math.random() - 0.5) * 3.5;
    }
    dustGeo.setAttribute("position", new THREE.BufferAttribute(dustPos, 3));
    var dustMat = new THREE.PointsMaterial({
      color: 0xf3efe6,
      size: 0.09,
      map: spriteTex,
      transparent: true,
      opacity: 0.55,
      depthWrite: false
    });
    var chalkDust = new THREE.Points(dustGeo, dustMat);
    scene.add(chalkDust);

    /* Studio 3-Point PBR Lighting */
    scene.add(new THREE.AmbientLight(0xf3efe6, 0.55));

    var keyLight = new THREE.DirectionalLight(0xfff4e0, 2.4);
    keyLight.position.set(4.5, 6.2, 6.5);
    scene.add(keyLight);

    var brassRim = new THREE.PointLight(0xd99b26, 16, 22);
    brassRim.position.set(-5.2, 2.4, -3.2);
    scene.add(brassRim);

    var redFill = new THREE.PointLight(0xc83e2b, 10, 18);
    redFill.position.set(4.8, -2.6, 3.0);
    scene.add(redFill);

    assembly.rotation.set(0.24, -0.46, 0.08);

    /* Pointer Inertia & Drag Rotation */
    var ptrX = 0;
    var ptrY = 0;
    var dragging = false;
    var dragYaw = -0.46;
    var lastX = 0;

    window.addEventListener("pointermove", function (ev) {
      ptrX = (ev.clientX / Math.max(1, window.innerWidth) - 0.5) * 0.42;
      ptrY = (ev.clientY / Math.max(1, window.innerHeight) - 0.5) * 0.26;
      if (dragging) {
        var dx = ev.clientX - lastX;
        lastX = ev.clientX;
        dragYaw += dx * 0.007;
      }
    }, { passive: true });

    canvas.addEventListener("pointerdown", function (ev) {
      dragging = true;
      lastX = ev.clientX;
    });
    window.addEventListener("pointerup", function () {
      dragging = false;
    });

    if (explodeSlider) {
      explodeSlider.addEventListener("input", function () {
        explodeState.value = Number(explodeSlider.value) / 100;
        updateBarbellHud(explodeState.value);
      });
    }

    function resize() {
      var wrap = canvas.parentElement;
      if (!wrap) return;
      var w = wrap.clientWidth;
      var h = wrap.clientHeight;
      if (!w || !h) return;
      renderer.setSize(w, h, false);
      camera.aspect = w / h;
      /* Responsive zoom so mobile fits entire exploded barbell */
      camera.position.z = w < 520 ? 11.2 : w < 780 ? 9.8 : 8.5;
      camera.updateProjectionMatrix();
    }
    resize();
    window.addEventListener("resize", resize);

    var isVisible = true;
    if ("IntersectionObserver" in window) {
      new IntersectionObserver(function (entries) {
        isVisible = entries[0].isIntersecting;
      }, { threshold: 0.02 }).observe(canvas);
    }

    var smoothExplode = explodeState.value;
    var clock = new THREE.Clock();

    (function renderLoop() {
      requestAnimationFrame(renderLoop);
      if (!isVisible || document.hidden) return;

      var tNow = clock.getElapsedTime();
      smoothExplode += (explodeState.value - smoothExplode) * 0.09;

      /* Position plates & spin-lock collars along bar axis */
      for (var i = 0; i < animatedParts.length; i++) {
        var p = animatedParts[i];
        var targetX = p.side * (p.baseX + smoothExplode * p.spread);
        p.mesh.position.x = targetX;
        p.mesh.rotation.x = smoothExplode * p.spinFactor * (p.isCollar ? 3.2 : 0.7);
      }

      if (!dragging) {
        dragYaw += 0.0018;
      }
      var targetRotY = dragYaw + ptrX;
      var targetRotX = 0.24 + ptrY + Math.sin(tNow * 0.55) * 0.03;

      assembly.rotation.y += (targetRotY - assembly.rotation.y) * 0.06;
      assembly.rotation.x += (targetRotX - assembly.rotation.x) * 0.06;
      assembly.position.y = Math.sin(tNow * 0.85) * 0.08;

      chalkDust.rotation.y = tNow * 0.025;
      chalkDust.position.y = Math.sin(tNow * 0.4) * 0.06;

      renderer.render(scene, camera);
    })();
  })();

  /* ---------------- 3. FOREGROUND SERVICE LOCK-SCREEN REST TIMER ---------------- */
  var lsTotalSec = 150;
  var lsRemainSec = 150;
  var lsPaused = false;
  var lsTimerEl = document.getElementById("lsTimerReadout");
  var lsBarEl = document.getElementById("lsProgressBar");
  var lsPauseBtn = document.getElementById("lsPauseBtn");
  var lsAdd30Btn = document.getElementById("lsAdd30Btn");
  var lsSkipBtn = document.getElementById("lsSkipBtn");
  var lsToggleAodBtn = document.getElementById("lsToggleAodBtn");
  var lockDevice = document.getElementById("lockDevice");
  var lsStatus = document.getElementById("lsServiceStatus");

  function fmtMMSS(sec) {
    var s = Math.max(0, Math.round(sec));
    var m = Math.floor(s / 60);
    var r = s % 60;
    return (m < 10 ? "0" : "") + m + ":" + (r < 10 ? "0" : "") + r;
  }

  function renderLockscreenTimer() {
    if (lsTimerEl) lsTimerEl.textContent = fmtMMSS(lsRemainSec);
    if (lsBarEl) {
      var pct = lsTotalSec > 0 ? Math.max(0, Math.min(100, (lsRemainSec / lsTotalSec) * 100)) : 0;
      lsBarEl.style.width = pct.toFixed(1) + "%";
    }
    if (lsStatus) {
      if (lsRemainSec <= 0) {
        lsStatus.textContent = currentLang === "vi" ? "00:00 · SẴN SÀNG VÀO HIỆP" : "00:00 · READY TO LIFT";
      } else if (lsPaused) {
        lsStatus.textContent = currentLang === "vi" ? "ĐÃ TẠM DỪNG · HIỆP 3/4" : "PAUSED · SET 3/4";
      } else {
        lsStatus.textContent = currentLang === "vi" ? "ĐANG CHẠY · HIỆP 3/4" : "ACTIVE · SET 3/4";
      }
    }
  }

  function updatePauseBtnLabel() {
    if (!lsPauseBtn) return;
    lsPauseBtn.textContent = lsPaused ? t("s2.resume") : t("s2.pause");
  }

  setInterval(function () {
    if (!lsPaused && lsRemainSec > 0) {
      lsRemainSec -= 1;
      renderLockscreenTimer();
    }
  }, 1000);

  if (lsPauseBtn) {
    lsPauseBtn.addEventListener("click", function () {
      lsPaused = !lsPaused;
      updatePauseBtnLabel();
      renderLockscreenTimer();
    });
  }
  if (lsAdd30Btn) {
    lsAdd30Btn.addEventListener("click", function () {
      lsRemainSec += 30;
      lsTotalSec = Math.max(lsTotalSec, lsRemainSec);
      renderLockscreenTimer();
    });
  }
  if (lsSkipBtn) {
    lsSkipBtn.addEventListener("click", function () {
      lsRemainSec = 150;
      lsTotalSec = 150;
      lsPaused = false;
      updatePauseBtnLabel();
      renderLockscreenTimer();
    });
  }
  if (lsToggleAodBtn && lockDevice) {
    lsToggleAodBtn.addEventListener("click", function () {
      lockDevice.classList.toggle("is-aod");
    });
  }

  /* ---------------- 4. INTERACTIVE EMBEDDED WORKOUT LOG SANDBOX ---------------- */
  var LIFTS = {
    squat: { name: "Barbell Squat", defaultKg: 140, defaultReps: 5, muscle: "quads" },
    bench: { name: "Bench Press", defaultKg: 92.5, defaultReps: 8, muscle: "chest" },
    deadlift: { name: "Deadlift", defaultKg: 175, defaultReps: 5, muscle: "back" },
    ohp: { name: "Overhead Press", defaultKg: 60, defaultReps: 8, muscle: "shoulders" }
  };

  var INITIAL_SETS = [
    { liftKey: "squat", lift: "Barbell Squat", kg: 130, reps: 5, rpe: 7.5 },
    { liftKey: "squat", lift: "Barbell Squat", kg: 135, reps: 5, rpe: 8.0 }
  ];

  var BASE_WEEKLY_SETS = { quads: 11, chest: 14, back: 12, shoulders: 10 };
  var MRV_CAPS = { quads: 18, chest: 20, back: 20, shoulders: 18 };

  var activeLift = "squat";
  var loggedSets = INITIAL_SETS.slice();
  var sbRestRemaining = 0;

  var weightIn = document.getElementById("sbWeightInput");
  var repsIn = document.getElementById("sbRepsInput");
  var rpeIn = document.getElementById("sbRpeInput");
  var setTableBody = document.getElementById("sbSetTableBody");
  var logSetBtn = document.getElementById("sbLogSetBtn");
  var resetBtn = document.getElementById("sbResetBtn");
  var walBadge = document.getElementById("sbWalCommitBadge");
  var restLabel = document.getElementById("sbRestLabel");
  var restClock = document.getElementById("sbRestClock");

  function calcEpley(kg, reps) {
    if (reps <= 1) return kg;
    return kg * (1 + reps / 30);
  }

  function renderSetRows() {
    if (!setTableBody) return;
    setTableBody.innerHTML = loggedSets.map(function (s, idx) {
      var e1rm = calcEpley(s.kg, s.reps).toFixed(1);
      return (
        "<tr>" +
        '<td class="num">' + (idx + 1) + "</td>" +
        "<td><strong>" + s.lift + "</strong></td>" +
        '<td class="num">' + s.kg.toFixed(1) + " kg × " + s.reps + "</td>" +
        '<td class="num">@' + s.rpe.toFixed(1) + "</td>" +
        '<td class="num" style="color:var(--brass);">' + e1rm + " kg</td>" +
        "</tr>"
      );
    }).join("");
  }

  function updateSandboxMetrics() {
    var kg = Math.max(1, parseFloat(weightIn ? weightIn.value : 140) || 140);
    var reps = Math.max(1, parseInt(repsIn ? repsIn.value : 5, 10) || 5);
    var rpe = Math.min(10, Math.max(5, parseFloat(rpeIn ? rpeIn.value : 8.5) || 8.5));

    var e1rm = calcEpley(kg, reps);
    var e1rmEl = document.getElementById("sbE1rmVal");
    var formEl = document.getElementById("sbFormulaReadout");
    var decVal = document.getElementById("sbDecisionVal");
    var decReason = document.getElementById("sbDecisionReason");
    var totalBadge = document.getElementById("sbTotalVolBadge");

    if (e1rmEl) e1rmEl.textContent = e1rm.toFixed(1) + " kg";
    if (formEl) formEl.textContent = kg + " × (1 + " + reps + "/30) · RIR " + (10 - rpe).toFixed(1);

    if (decVal && decReason) {
      if (rpe <= 8.5) {
        decVal.textContent = "+2.5 kg";
        decVal.style.color = "var(--ok)";
        decReason.textContent = t("s3.reasonPlus");
      } else {
        decVal.textContent = currentLang === "vi" ? "Giữ mức tạ" : "Hold load";
        decVal.style.color = "var(--brass)";
        decReason.textContent = t("s3.reasonHold");
      }
    }

    /* Session volume & weekly MEV-MRV muscle bars */
    var totalVol = 0;
    var counts = {
      quads: BASE_WEEKLY_SETS.quads - 2,
      chest: BASE_WEEKLY_SETS.chest,
      back: BASE_WEEKLY_SETS.back,
      shoulders: BASE_WEEKLY_SETS.shoulders
    };
    loggedSets.forEach(function (s) {
      totalVol += s.kg * s.reps;
      var m = (LIFTS[s.liftKey] && LIFTS[s.liftKey].muscle) || "quads";
      counts[m] = (counts[m] || 0) + 1;
    });

    if (totalBadge) {
      totalBadge.textContent = (currentLang === "vi" ? "VOLUME BUỔI: " : "SESSION VOL: ") + Math.round(totalVol).toLocaleString("en-US") + " KG";
    }

    ["quads", "chest", "back", "shoulders"].forEach(function (m) {
      var cap = MRV_CAPS[m];
      var val = counts[m];
      var keyCap = m.charAt(0).toUpperCase() + m.slice(1);
      var numEl = document.getElementById("vol" + keyCap + "Val");
      var barEl = document.getElementById("vol" + keyCap + "Bar");
      if (numEl) numEl.textContent = String(val);
      if (barEl) barEl.style.width = Math.min(100, Math.round((val / cap) * 100)) + "%";
    });
  }

  document.querySelectorAll(".lift-tab").forEach(function (btn) {
    btn.addEventListener("click", function () {
      activeLift = btn.getAttribute("data-lift") || "squat";
      document.querySelectorAll(".lift-tab").forEach(function (b) {
        b.classList.toggle("is-active", b === btn);
      });
      var spec = LIFTS[activeLift];
      if (spec && weightIn && repsIn) {
        weightIn.value = String(spec.defaultKg);
        repsIn.value = String(spec.defaultReps);
        updateSandboxMetrics();
      }
    });
  });

  document.querySelectorAll(".dial-step").forEach(function (btn) {
    btn.addEventListener("click", function () {
      var target = btn.getAttribute("data-step-target");
      var delta = parseFloat(btn.getAttribute("data-step")) || 0;
      var input = target === "weight" ? weightIn : target === "reps" ? repsIn : rpeIn;
      if (!input) return;
      var next = (parseFloat(input.value) || 0) + delta;
      if (target === "weight") next = Math.max(20, Math.min(400, next));
      if (target === "reps") next = Math.max(1, Math.min(30, Math.round(next)));
      if (target === "rpe") next = Math.max(6, Math.min(10, next));
      input.value = String(next);
      updateSandboxMetrics();
    });
  });

  [weightIn, repsIn, rpeIn].forEach(function (inp) {
    if (inp) inp.addEventListener("input", updateSandboxMetrics);
  });

  if (logSetBtn) {
    logSetBtn.addEventListener("click", function () {
      var t0 = performance.now();
      var kg = Math.max(1, parseFloat(weightIn.value) || 140);
      var reps = Math.max(1, parseInt(repsIn.value, 10) || 5);
      var rpe = Math.min(10, Math.max(6, parseFloat(rpeIn.value) || 8.5));
      var spec = LIFTS[activeLift] || LIFTS.squat;

      loggedSets.push({
        liftKey: activeLift,
        lift: spec.name,
        kg: kg,
        reps: reps,
        rpe: rpe
      });
      renderSetRows();
      updateSandboxMetrics();

      var elapsed = Math.max(0.6, Math.min(2.3, performance.now() - t0 + 0.9));
      if (walBadge) {
        walBadge.textContent = "WAL COMMITTED · " + elapsed.toFixed(1) + " ms (#TX-" + (840 + loggedSets.length) + ")";
      }

      /* Start sandbox rest countdown */
      sbRestRemaining = 150;
      if (restLabel) restLabel.textContent = t("s3.restRunning");
      if (restClock) restClock.textContent = fmtMMSS(sbRestRemaining);
    });
  }

  if (resetBtn) {
    resetBtn.addEventListener("click", function () {
      loggedSets = INITIAL_SETS.slice();
      activeLift = "squat";
      if (weightIn) weightIn.value = "140";
      if (repsIn) repsIn.value = "5";
      if (rpeIn) rpeIn.value = "8.5";
      sbRestRemaining = 0;
      if (restLabel) restLabel.textContent = t("s3.restIdle");
      if (restClock) restClock.textContent = "02:30";
      renderSetRows();
      updateSandboxMetrics();
    });
  }

  setInterval(function () {
    if (sbRestRemaining > 0) {
      sbRestRemaining -= 1;
      if (restClock) restClock.textContent = fmtMMSS(sbRestRemaining);
      if (sbRestRemaining === 0 && restLabel) {
        restLabel.textContent = t("s3.restIdle");
      }
    }
  }, 1000);

  renderSetRows();

  /* ---------------- 5. PROTOTYPE DRAWER ---------------- */
  var drawer = document.getElementById("protoDrawer");
  var iframe = document.getElementById("protoIframe");
  var openNavBtn = document.getElementById("openDrawerNavBtn");
  var openSbBtn = document.getElementById("openDrawerSandboxBtn");
  var closeDrawerBtn = document.getElementById("closeDrawerBtn");

  function openDrawer() {
    if (!drawer) return;
    if (iframe && !iframe.getAttribute("src")) {
      iframe.setAttribute("src", iframe.getAttribute("data-src") || "prototype.html");
    }
    drawer.hidden = false;
    document.body.style.overflow = "hidden";
  }

  function closeDrawer() {
    if (!drawer) return;
    drawer.hidden = true;
    document.body.style.overflow = "";
  }

  if (openNavBtn) openNavBtn.addEventListener("click", openDrawer);
  if (openSbBtn) openSbBtn.addEventListener("click", openDrawer);
  if (closeDrawerBtn) closeDrawerBtn.addEventListener("click", closeDrawer);
  if (drawer) {
    drawer.addEventListener("click", function (ev) {
      if (ev.target === drawer) closeDrawer();
    });
  }
  document.addEventListener("keydown", function (ev) {
    if (ev.key === "Escape" && drawer && !drawer.hidden) closeDrawer();
  });

  /* ---------------- 6. GSAP + SCROLLTRIGGER PINNED STORYTELLING ---------------- */
  if (window.gsap && window.ScrollTrigger && !reduceMotion) {
    gsap.registerPlugin(ScrollTrigger);

    /* Hero entrance choreography */
    gsap.from(".hero__copy > *", {
      y: 28,
      opacity: 0,
      duration: 0.85,
      stagger: 0.09,
      ease: "power3.out"
    });

    gsap.from(".barbell-stage", {
      y: 36,
      opacity: 0,
      duration: 1.0,
      delay: 0.15,
      ease: "power3.out"
    });

    /* Scroll-scrubbed explosion of the 3D Olympic Barbell across the Hero */
    ScrollTrigger.create({
      trigger: "#hero",
      start: "top top",
      end: "bottom top",
      scrub: 0.6,
      onUpdate: function (self) {
        explodeState.value = 0.12 + self.progress * 0.88;
        updateBarbellHud(explodeState.value);
      }
    });

    /* Hero background photo parallax */
    gsap.to("[data-hero-parallax]", {
      yPercent: 12,
      ease: "none",
      scrollTrigger: {
        trigger: "#hero",
        start: "top top",
        end: "bottom top",
        scrub: true
      }
    });

    /* Desktop Pinned Narrative 01: The Basement Signal Drop */
    var mm = gsap.matchMedia();
    mm.add("(min-width: 1024px)", function () {
      ScrollTrigger.create({
        trigger: "#signal-drop",
        start: "top top+=72",
        end: "+=1150",
        pin: true,
        scrub: 0.5,
        onUpdate: function (self) {
          var idx = self.progress < 0.34 ? 0 : self.progress < 0.68 ? 1 : 2;
          if (idx !== currentSignalStep) {
            applySignalStep(idx);
          }
        }
      });

      /* Desktop Pinned Narrative 02: ForegroundService Rest Timer scrub */
      ScrollTrigger.create({
        trigger: "#rest-service",
        start: "top top+=72",
        end: "+=900",
        pin: true,
        scrub: 0.5,
        onUpdate: function (self) {
          lsRemainSec = Math.round(150 * (1 - self.progress * 0.88));
          if (lockDevice) {
            lockDevice.classList.toggle("is-aod", self.progress > 0.45);
          }
          renderLockscreenTimer();
        }
      });
    });

    /* Full-bleed photography parallax bands */
    gsap.utils.toArray("[data-band-parallax]").forEach(function (img) {
      gsap.fromTo(img, { yPercent: -9 }, {
        yPercent: 9,
        ease: "none",
        scrollTrigger: {
          trigger: img.closest(".photo-band"),
          start: "top bottom",
          end: "bottom top",
          scrub: true
        }
      });
    });

    /* Architecture cards reveal */
    gsap.utils.toArray("[data-reveal]").forEach(function (el) {
      gsap.from(el, {
        y: 28,
        opacity: 0,
        duration: 0.75,
        ease: "power3.out",
        scrollTrigger: {
          trigger: el,
          start: "top 88%"
        }
      });
    });
  }

  /* Initialize saved language (default EN or saved preference) */
  var initLang = "en";
  try {
    var stored = localStorage.getItem(LANG_KEY);
    if (stored === "vi" || stored === "en") initLang = stored;
  } catch (e) { /* ignore */ }
  applyLang(initLang);
  updateBarbellHud(explodeState.value);
  renderLockscreenTimer();
})();
