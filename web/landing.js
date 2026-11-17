/* IronLog landing — language switcher (VI/EN), GSAP motion, Three.js hero accent.
 * No-dependency fallbacks: if a CDN script fails, the page stays fully readable. */
(function () {
  "use strict";

  var I18N = {
    vi: {
      "meta.title": "IronLog — Mỗi buổi tập. Từng hiệp đều rõ ràng.",
      "meta.desc": "IronLog là ứng dụng Android ghi nhật ký tập: giáo án hypertrophy, ghi hiệp rep × kg, đồng hồ nghỉ, tiến bộ và bữa ăn tiếng Việt. Kèm prototype tương tác chạy ngay trên trình duyệt.",
      "skip": "Tới nội dung",
      "nav.proto": "Thử prototype tương tác ↗",
      "btn.proto": "Thử prototype tương tác ↗",
      "btn.apk": "Tải APK Android ↓",
      "hero.eyebrow": "IronLog / Ứng dụng Android ghi nhật ký tập",
      "hero.h1": "Mỗi buổi tập. Từng hiệp đều rõ ràng.",
      "hero.lead": "IronLog là nhật ký tập chạy trên Android: giáo án, kho bài tập tiếng Việt, ghi hiệp theo rep × kg và đồng hồ nghỉ. Trước khi bạn quyết định tăng tạ, màn hình đặt đủ ngữ cảnh cạnh nhau — buổi trước đã đẩy bao nhiêu rep ở bao nhiêu kg, RIR còn lại, nhóm cơ đã phục hồi tới đâu và thời gian nghỉ còn bao lâu.",
      "hero.honest": "Đây là một dự án cá nhân đang được phát triển công khai. Mọi con số trên trang này và trong prototype là dữ liệu mẫu, không phải kết quả tập luyện thật.",
      "hero.sample": "Xem buổi tập mẫu",
      "hero.meta1": "giáo án có sẵn",
      "hero.meta2": "món Việt trong bảng dinh dưỡng",
      "hero.meta3": "dữ liệu tập lưu offline",
      "hero.caption": "Màn hình Home dựng lại bằng HTML/CSS: buổi hôm nay, chuỗi ngày tập và mức phục hồi nằm trên cùng một mặt.",
      "phone.hi": "Chào Hoàng",
      "phone.today": "Hôm nay tập gì?",
      "phone.streak": "ngày liên tiếp<br>có buổi tập được ghi",
      "phone.cardlabel": "Buổi hôm nay · PPL",
      "phone.cardsub": "Ngực và vai trước · 5 bài · 18 hiệp",
      "phone.start": "Bắt đầu buổi tập →",
      "phone.recovery": "Phục hồi nhóm cơ",
      "phone.note": "Chân mới tập hôm qua — hôm nay để chân nghỉ là hợp lý.",
      "muscle.chest": "Ngực",
      "muscle.shoulders": "Vai",
      "muscle.frontdelts": "Vai trước",
      "muscle.triceps": "Tay sau",
      "muscle.back": "Lưng",
      "muscle.legs": "Chân",
      "tab.today": "Hôm nay",
      "tab.programs": "Giáo án",
      "tab.progress": "Tiến bộ",
      "tab.profile": "Cá nhân",
      "s01.t": "Ý tưởng sản phẩm rõ ràng",
      "s01.lead": "Người tập ở phòng gym không thiếu động lực, họ thiếu một chỗ ghi đủ nhanh và đọc lại đủ rõ. IronLog được thiết kế quanh đúng hai việc đó.",
      "s01.p1t": "Ghi trong vài giây",
      "s01.p1": "Một hiệp chỉ gồm hai con số: rep và kg. Mở buổi tập là thấy ngay bài đang tập, mức tạ buổi trước đã điền sẵn để sửa, không phải gõ lại từ đầu.",
      "s01.p2t": "Ngữ cảnh trước quyết định",
      "s01.p2": "Tăng tạ hay giữ nguyên không nên là cảm tính. Buổi trước, RIR, phục hồi nhóm cơ và đồng hồ nghỉ được đặt cạnh nhau ngay tại màn hình ghi hiệp.",
      "s01.p3t": "Số liệu nói thật",
      "s01.p3": "Tuần nào chưa đủ dữ liệu thì ứng dụng nói chưa đủ, thay vì vẽ ra nhận xét cho có. Tổng kết tuần chỉ viết khi có đủ buổi tập để đọc.",
      "s02.t": "Sản phẩm khi dùng thật",
      "s02.lead": "Home trả lời câu hỏi “hôm nay tập gì”, giáo án trả lời “tập theo lịch nào”, và thanh phục hồi cho biết nhóm cơ nào nên để yên hôm nay.",
      "s02.bt": "Bốn giáo án có sẵn",
      "s02.bp": "Chọn một lịch, buổi mở ra thành danh sách bài. Người dùng cũng có thể tự tạo giáo án riêng và lấy bài từ kho bài tập tiếng Việt kèm lưu ý kỹ thuật.",
      "s02.th1": "Giáo án",
      "s02.th2": "Ngày mỗi tuần",
      "s02.th3": "Độ dài buổi",
      "d6": "6 ngày",
      "d4": "4 ngày",
      "d3": "3 ngày",
      "m5065": "50-65 phút",
      "m6070": "60-70 phút",
      "m6575": "65-75 phút",
      "s02.c1": "6 ngày / tuần · đang theo",
      "s02.c2": "4 ngày / tuần",
      "s02.c3": "6 ngày / tuần",
      "s02.c4": "3 ngày / tuần",
      "s02.note": "Mức phục hồi tính từ lần gần nhất nhóm cơ đó được tập và khối lượng của buổi đó.",
      "band1.alt": "Không gian phòng gym công nghiệp tối với dãy tạ đơn và khung squat",
      "band1.cap": "Sức mạnh bắt đầu từ sự rõ ràng",
      "s03.t": "Một buổi tập mẫu trọn vẹn",
      "s03.lead": "Từ lúc mở buổi tới lúc đọc tổng kết, dòng chảy chỉ có ba bước: ghi hiệp, xem lại, nhận kết quả. Dưới đây là buổi Push Day A được rút gọn vào bài đầu tiên.",
      "s03.f1t": "Ghi hiệp",
      "s03.f1": "Bench Press, ba hiệp. Mỗi hiệp nhập kg và rep, bấm xong là đồng hồ nghỉ tự chạy.",
      "s03.f2t": "Xem lại",
      "s03.f2": "Màn hình kiểm tra liệt kê hiệp đã ghi, tổng volume và hiệp nặng nhất, kèm chọn RIR trước khi chốt.",
      "s03.f3t": "Kết quả",
      "s03.f3": "Buổi tập được lưu, xuất hiện ở đầu danh sách gần đây và cộng vào volume của tuần.",
      "s03.bt": "Push Day A — danh sách bài",
      "s03.th1": "Số",
      "s03.th2": "Bài trong giáo án",
      "s03.e1": "Đẩy ngực ngang đòn tạ",
      "s03.e2": "Đẩy ngực trên tạ đơn",
      "s03.e3": "Đẩy vai đứng tạ đòn",
      "s03.e4": "Xà kép có tải",
      "s03.e5": "Duỗi tay sau với cáp",
      "s03.exercises": "bài",
      "s03.sets": "hiệp",
      "s03.rir": "RIR mục tiêu",
      "s03.insession": "Đang tập · Push Day A",
      "s03.lasttime": "Buổi trước: 8 × 60 · 8 × 60 · 7 × 60",
      "s03.set": "Hiệp",
      "s03.done": "Xong",
      "s03.volume": "Volume tạm tính: 960 kg",
      "s03.logged": "2 trên 3 hiệp đã ghi",
      "s03.cta": "Tự tay ghi một buổi trong prototype ↗",
      "s04.t": "Đồng hồ nghỉ và các trạng thái cảnh báo",
      "s04.lead": "Nghỉ cũng là một phần của buổi tập. Đồng hồ chạy nền kèm thông báo, và những trạng thái dễ gây sai số được nói thẳng ngay lúc chúng xảy ra.",
      "s04.c1l": "Đang nghỉ",
      "s04.c1": "Hiệp 2 xong. Đồng hồ đếm tiếp kể cả khi khoá màn hình, nhờ dịch vụ chạy nền trên Android.",
      "s04.c2l": "Đã tạm dừng",
      "s04.c2": "Chạm để dừng, cộng thêm 30 giây hoặc bỏ qua. Trạng thái tạm dừng nhìn là biết, không âm thầm đếm tiếp.",
      "s04.c3l": "Cảnh báo",
      "s04.c3b": "Chưa ghi hiệp nào",
      "s04.c3": "Bấm “Xem lại” khi chưa có hiệp nào được ghi sẽ bị chặn kèm lý do. Mức tạ nhập cao hơn kỷ lục cá nhân cũng được hỏi lại trước khi lưu.",
      "band2.alt": "Hai bàn tay phủ phấn nắm chặt thanh đòn tạ trong bóng tối",
      "band2.cap": "Nắm chặt. Ghi rõ. Tiến bộ.",
      "s05.t": "Hệ quyết định",
      "s05.lead": "Gợi ý của IronLog không phải hộp đen. Mỗi con số đều truy được về phép tính đơn giản, và người tập luôn là người chốt cuối cùng.",
      "s05.bt1": "Ví dụ tính tay: Bench Press",
      "s05.calc1": "8 rep × 60 kg × 3 hiệp",
      "s05.calc2": "= 1.440 kg volume",
      "s05.calcp": "Ba hiệp đều đạt 8 rep ở RIR 1–2, tức là vẫn còn dư một đến hai rep trong khả năng. Gợi ý cho buổi tới: <strong class=\"volt\">+2,5 kg</strong>. Nếu rep tụt hoặc RIR bằng 0, gợi ý đổi thành giữ nguyên mức tạ.",
      "s05.bt2": "Khối lượng theo nhóm cơ",
      "s05.bp": "Tab Tiến bộ đối chiếu số hiệp mỗi nhóm cơ trong tuần với khoảng MEV–MRV — ngưỡng tối thiểu để tiến bộ và ngưỡng tối đa còn phục hồi nổi. AI Coach đọc cùng những con số này khi viết tổng kết 7 ngày.",
      "s05.vol1": "Ngực · 14 hiệp",
      "s05.vol2": "Lưng · 12 hiệp",
      "s05.vol3": "Chân · 10 hiệp",
      "s05.bt3": "Ăn uống dùng cùng một logic số",
      "s05.bp3": "Bảng gần 90 món Việt có sẵn trong máy; món lạ có thể chụp ảnh để Gemini ước lượng khối lượng rồi người dùng sửa lại trước khi lưu. Thẻ BMR và TDEE (Katch-McArdle, đối chiếu Mifflin-St Jeor) cho ra mốc calo để đối chiếu.",
      "s05.th1": "Món",
      "s05.th3": "Đạm, g",
      "s05.d1": "Cơm trắng",
      "s05.d2": "Ức gà",
      "s05.d3": "Phở bò",
      "s05.d4": "Cơm tấm sườn",
      "n27": "2,7",
      "n55": "5,5",
      "n75": "7,5",
      "s05.note": "Số liệu tính trên 100 g, lấy từ bảng dinh dưỡng trong ứng dụng.",
      "s06.t": "Hệ hình ảnh nhất quán",
      "s06.lead": "Một hệ duy nhất cho cả ứng dụng lẫn trang này: chữ nén cho tiêu đề, chữ thường dễ đọc cho nội dung, và số luôn dùng font đơn cách để cột số không nhảy khi đếm.",
      "s06.disp": "Saira Condensed — tiêu đề",
      "s06.body": "Albert Sans — nội dung, nhãn và giải thích. Đủ mềm để đọc đoạn dài, đủ gọn để đặt cạnh bảng số.",
      "s06.mono": "JetBrains Mono — 1.440 kg · 01:30 · RIR 1–2",
      "s06.bp": "Nguyên tắc “số trước, chữ sau”: mọi màn hình đều trả lời bằng con số chính trước, phần diễn giải đi kèm ngay bên dưới với cỡ chữ nhỏ hơn.",
      "s06.sw1": "Nền sáng, mặt màn hình app",
      "s06.sw2": "Nền tối, chữ chính trên nền sáng",
      "s06.sw3": "Điểm nhấn, hành động chính, số nổi bật",
      "s06.sw4": "Trạng thái focus, đường tiến trình",
      "s06.sw5": "Chữ phụ, ghi chú, số liệu thứ cấp",
      "s07.t": "Ứng dụng Android hoàn chỉnh",
      "s07.lead": "Trang này là bản trình bày; sản phẩm thật là ứng dụng Android chạy offline ngay trên điện thoại của bạn và không cần tài khoản — dữ liệu tập nằm trên máy của bạn, không nằm trên máy chủ của ai khác.",
      "s07.th1": "Hạng mục",
      "s07.th2": "Chi tiết",
      "s07.r1a": "Ngôn ngữ",
      "s07.r2a": "Giao diện",
      "s07.r3a": "Dữ liệu",
      "s07.r3b": "Room, lưu offline trên máy; xuất CSV và sao lưu JSON",
      "s07.r4a": "Đồng hồ nghỉ",
      "s07.r4b": "Dịch vụ chạy nền kèm thông báo",
      "s07.r5a": "Nền tảng",
      "s07.r5b": "Android 8.1 trở lên (API 27+)",
      "s07.r6a": "Giấy phép",
      "s07.r7b": "Dữ liệu hư cấu, chỉ để thử tương tác",
      "s07.bt": "Ba bước tới buổi đầu",
      "s07.bp": "Không cần cài đặt gì cầu kỳ và không cần tài khoản: mở ứng dụng, chọn một lịch có sẵn và ghi buổi đầu tiên của bạn.",
      "s07.st1t": "Chọn giáo án",
      "s07.st1": "PPL, Upper/Lower, Arnold hoặc Full body — chọn lịch khớp với số ngày bạn tới phòng tập mỗi tuần.",
      "s07.st2t": "Ghi buổi đầu",
      "s07.st2": "Ghi rep × kg cho từng hiệp; đồng hồ nghỉ tự chạy sau mỗi hiệp để bạn chỉ việc tập tiếp.",
      "s07.st3t": "Xem tiến bộ",
      "s07.st3": "Đường cong e1RM, số hiệp theo nhóm cơ và tổng kết tuần cho bạn biết nên tăng tạ ở đâu.",
      "s07.note": "File gymverse.apk (bản debug dùng thử) — tải về và cài trực tiếp trên Android 8.1 trở lên, không cần tài khoản.",
      "dev.eyebrow": "Nhà phát triển",
      "dev.t": "Một người làm — từ phòng tập tới từng dòng code.",
      "dev.p1": "IronLog do Trần Huy Hoàng thiết kế và phát triển. Anh là lập trình viên tại Việt Nam, đồng thời là người tập luyện theo đúng phương pháp mà ứng dụng ghi lại — nên mọi màn hình đều bắt đầu từ một câu hỏi rất thật: buổi tập hôm nay cần nhìn thấy gì, để buổi tập ngày mai tốt hơn?",
      "dev.p2": "Không có đội ngũ lớn đứng sau và cũng không có tính năng nào được thêm vào cho có. Mỗi phần của IronLog — giáo án, đồng hồ nghỉ, đường cong sức mạnh, bảng món ăn Việt — đều xuất phát từ những buổi tập thật của chính người viết ra nó, rồi được giữ lại chỉ khi nó giúp buổi tập sau rõ ràng hơn buổi tập trước.",
      "dev.role": "Thiết kế &amp; phát triển IronLog",
      "scope.t": "Phạm vi thiết kế và prototype tương tác",
      "scope.p1": "Nghiên cứu thiết kế này nối một vòng khép kín: màn hình tổng quan buổi sáng, buổi tập Push Day A với ba hiệp Bench Press có thể ghi thật, màn hình xem lại với lựa chọn RIR, và một kết quả mô phỏng được cộng vào tiến bộ của tuần. Các trạng thái ít vui hơn cũng được giữ nguyên và nói rõ: tạm dừng giữa giờ nghỉ, cảnh báo khi bấm xem lại mà chưa ghi hiệp nào, và một tuần cũ chưa đủ dữ liệu để kết luận.",
      "scope.p2": "Hệ hình ảnh dùng nền tối gần như đen cho phần trình bày, màu volt làm điểm nhấn có kiểm soát và khoảng trắng rộng quanh các con số. Chữ số dạng bảng, nhãn rõ ràng và các bước xem lại có thể sửa được là phần mang tương tác. Prototype trên trình duyệt chạy với tài khoản, số hiệp và mức tạ hư cấu; nó trình bày thiết kế giao diện và các trạng thái tương tác, không kết nối tập luyện thật, không đồng bộ dữ liệu và không hứa hẹn kết quả thể hình.",
      "foot.credit": "IronLog — Thiết kế và phát triển bởi Trần Huy Hoàng.",
      "foot.fine": "Prototype trên trình duyệt dùng dữ liệu hư cấu và chỉ mô phỏng tương tác — đó không phải ứng dụng Android thật.",
      "foot.n1": "Giáo án",
      "foot.n2": "Hệ quyết định",
      "foot.n3": "Nhà phát triển",
      "foot.n4": "Phạm vi"
    },
    en: {
      "meta.title": "IronLog — Every session. Every set, crystal clear.",
      "meta.desc": "IronLog is an Android workout log: hypertrophy programs, set logging in reps × kg, a rest timer, progress tracking and a Vietnamese food table. Includes an interactive prototype that runs right in your browser.",
      "skip": "Skip to content",
      "nav.proto": "Try the interactive prototype ↗",
      "btn.proto": "Try the interactive prototype ↗",
      "btn.apk": "Download Android APK ↓",
      "hero.eyebrow": "IronLog / An Android workout-logging app",
      "hero.h1": "Every session. Every set, crystal clear.",
      "hero.lead": "IronLog is a workout log that runs on Android: training programs, a Vietnamese exercise library, set logging in reps × kg and a rest timer. Before you decide to add weight, the screen lines up all the context side by side — how many reps you pressed at what load last time, how much RIR is left, how far each muscle group has recovered and how much rest remains.",
      "hero.honest": "This is a personal project developed in the open. Every figure on this page and in the prototype is sample data, not real training results.",
      "hero.sample": "See a sample session",
      "hero.meta1": "ready-made programs",
      "hero.meta2": "Vietnamese dishes in the nutrition table",
      "hero.meta3": "of workout data stored offline",
      "hero.caption": "The Home screen rebuilt in HTML/CSS: today's session, the day streak and recovery levels on a single surface.",
      "phone.hi": "Hi Hoàng",
      "phone.today": "What's on today?",
      "phone.streak": "day streak<br>of logged sessions",
      "phone.cardlabel": "Today's session · PPL",
      "phone.cardsub": "Chest & front delts · 5 exercises · 18 sets",
      "phone.start": "Start session →",
      "phone.recovery": "Muscle recovery",
      "phone.note": "Legs were trained yesterday — giving them a rest today is the right call.",
      "muscle.chest": "Chest",
      "muscle.shoulders": "Shoulders",
      "muscle.frontdelts": "Front delts",
      "muscle.triceps": "Triceps",
      "muscle.back": "Back",
      "muscle.legs": "Legs",
      "tab.today": "Today",
      "tab.programs": "Programs",
      "tab.progress": "Progress",
      "tab.profile": "Profile",
      "s01.t": "A clear product idea",
      "s01.lead": "People at the gym don't lack motivation — they lack a place to log fast enough and read back clearly enough. IronLog is designed around exactly those two jobs.",
      "s01.p1t": "Log in seconds",
      "s01.p1": "A set is just two numbers: reps and kg. Open a session and the current exercise is right there, with last time's load pre-filled to edit — never typed from scratch.",
      "s01.p2t": "Context before decisions",
      "s01.p2": "Adding weight or holding steady should never be a gut call. Last session, RIR, muscle recovery and the rest timer sit side by side on the set-logging screen itself.",
      "s01.p3t": "Numbers that tell the truth",
      "s01.p3": "When a week doesn't have enough data, the app says so instead of inventing commentary. The weekly summary is only written when there are enough sessions to read.",
      "s02.t": "The product in real use",
      "s02.lead": "Home answers “what's on today”, programs answer “which schedule am I following”, and the recovery bars show which muscle groups to leave alone today.",
      "s02.bt": "Four ready-made programs",
      "s02.bp": "Pick a schedule and the session opens as an exercise list. You can also build your own program and pull exercises from the Vietnamese library, complete with technique notes.",
      "s02.th1": "Program",
      "s02.th2": "Days per week",
      "s02.th3": "Session length",
      "d6": "6 days",
      "d4": "4 days",
      "d3": "3 days",
      "m5065": "50–65 min",
      "m6070": "60–70 min",
      "m6575": "65–75 min",
      "s02.c1": "6 days / week · following",
      "s02.c2": "4 days / week",
      "s02.c3": "6 days / week",
      "s02.c4": "3 days / week",
      "s02.note": "Recovery is computed from the last time that muscle group was trained and the volume of that session.",
      "band1.alt": "A dark industrial gym interior with dumbbell racks and squat racks",
      "band1.cap": "Strength starts with clarity",
      "s03.t": "One complete sample session",
      "s03.lead": "From opening the session to reading the summary, the flow has just three steps: log sets, review, get the result. Below, Push Day A is condensed into its first exercise.",
      "s03.f1t": "Log sets",
      "s03.f1": "Bench Press, three sets. Enter kg and reps for each set, and the rest timer starts on its own.",
      "s03.f2t": "Review",
      "s03.f2": "The review screen lists the logged sets, total volume and the heaviest set, with an RIR pick before you commit.",
      "s03.f3t": "Result",
      "s03.f3": "The session is saved, appears at the top of the recent list and adds to the week's volume.",
      "s03.bt": "Push Day A — exercise list",
      "s03.th1": "No.",
      "s03.th2": "Exercise in the program",
      "s03.e1": "Flat barbell chest press",
      "s03.e2": "Incline dumbbell press",
      "s03.e3": "Standing barbell shoulder press",
      "s03.e4": "Weighted parallel-bar dip",
      "s03.e5": "Cable triceps extension",
      "s03.exercises": "exercises",
      "s03.sets": "sets",
      "s03.rir": "target RIR",
      "s03.insession": "In session · Push Day A",
      "s03.lasttime": "Last time: 8 × 60 · 8 × 60 · 7 × 60",
      "s03.set": "Set",
      "s03.done": "Done",
      "s03.volume": "Running volume: 960 kg",
      "s03.logged": "2 of 3 sets logged",
      "s03.cta": "Log a session yourself in the prototype ↗",
      "s04.t": "The rest timer and its warning states",
      "s04.lead": "Rest is part of the session too. The timer runs in the background with notifications, and the states most likely to cause errors are called out the moment they happen.",
      "s04.c1l": "Resting",
      "s04.c1": "Set 2 done. The timer keeps counting even with the screen locked, thanks to an Android foreground service.",
      "s04.c2l": "Paused",
      "s04.c2": "Tap to pause, add 30 seconds or skip. A paused state looks paused — it never keeps counting in secret.",
      "s04.c3l": "Warning",
      "s04.c3b": "No sets logged yet",
      "s04.c3": "Tapping “Review” with no sets logged is blocked, with the reason stated. A load entered above your personal record is also confirmed before it saves.",
      "band2.alt": "Two chalked hands gripping a barbell in darkness",
      "band2.cap": "Grip hard. Log clearly. Progress.",
      "s05.t": "The decision system",
      "s05.lead": "IronLog's suggestions are not a black box. Every number traces back to simple arithmetic, and the lifter always makes the final call.",
      "s05.bt1": "A hand-worked example: Bench Press",
      "s05.calc1": "8 reps × 60 kg × 3 sets",
      "s05.calc2": "= 1,440 kg of volume",
      "s05.calcp": "All three sets hit 8 reps at RIR 1–2, meaning one to two reps were still in the tank. The suggestion for next time: <strong class=\"volt\">+2.5 kg</strong>. If reps drop or RIR hits 0, the suggestion switches to holding the load.",
      "s05.bt2": "Volume by muscle group",
      "s05.bp": "The Progress tab compares each muscle group's weekly sets against the MEV–MRV range — the minimum needed to progress and the maximum you can still recover from. The AI Coach reads the same numbers when writing the 7-day summary.",
      "s05.vol1": "Chest · 14 sets",
      "s05.vol2": "Back · 12 sets",
      "s05.vol3": "Legs · 10 sets",
      "s05.bt3": "Nutrition uses the same numeric logic",
      "s05.bp3": "A table of nearly 90 Vietnamese dishes is built in; unfamiliar dishes can be photographed so Gemini estimates the portion, which you then adjust before saving. BMR and TDEE cards (Katch-McArdle, cross-checked with Mifflin-St Jeor) give a calorie benchmark to compare against.",
      "s05.th1": "Dish",
      "s05.th3": "Protein, g",
      "s05.d1": "White rice",
      "s05.d2": "Chicken breast",
      "s05.d3": "Beef phở",
      "s05.d4": "Broken rice with pork chop",
      "n27": "2.7",
      "n55": "5.5",
      "n75": "7.5",
      "s05.note": "Values per 100 g, from the app's built-in nutrition table.",
      "s06.t": "One consistent visual system",
      "s06.lead": "A single system for the app and this page alike: condensed type for headings, a readable face for body copy, and a monospace font for numbers so digit columns never jump while counting.",
      "s06.disp": "Saira Condensed — headings",
      "s06.body": "Albert Sans — body copy, labels and explanations. Soft enough for long reads, compact enough to sit beside number tables.",
      "s06.mono": "JetBrains Mono — 1,440 kg · 01:30 · RIR 1–2",
      "s06.bp": "The “numbers first, words second” rule: every screen answers with the key figure first, with the explanation right below it in smaller type.",
      "s06.sw1": "Light background, the app screen surface",
      "s06.sw2": "Dark background, primary text on light",
      "s06.sw3": "Accent, primary actions, highlighted figures",
      "s06.sw4": "Focus states, progress tracks",
      "s06.sw5": "Secondary text, notes, secondary figures",
      "s07.t": "A complete Android app",
      "s07.lead": "This page is the presentation; the real product is an Android app that runs offline on your phone with no account — your training data lives on your device, not on anyone else's server.",
      "s07.th1": "Item",
      "s07.th2": "Details",
      "s07.r1a": "Language",
      "s07.r2a": "UI",
      "s07.r3a": "Data",
      "s07.r3b": "Room, stored offline on-device; CSV export and JSON backup",
      "s07.r4a": "Rest timer",
      "s07.r4b": "Background service with notifications",
      "s07.r5a": "Platform",
      "s07.r5b": "Android 8.1 and up (API 27+)",
      "s07.r6a": "License",
      "s07.r7b": "Fictional data, for trying the interactions only",
      "s07.bt": "Three steps to your first session",
      "s07.bp": "No elaborate setup and no account: open the app, pick a ready-made schedule and log your first session.",
      "s07.st1t": "Pick a program",
      "s07.st1": "PPL, Upper/Lower, Arnold or Full body — choose the schedule that matches how many days a week you get to the gym.",
      "s07.st2t": "Log your first session",
      "s07.st2": "Log reps × kg for every set; the rest timer starts after each set so you can just keep training.",
      "s07.st3t": "Watch the progress",
      "s07.st3": "The e1RM curve, sets per muscle group and the weekly summary show you exactly where to add weight.",
      "s07.note": "The gymverse.apk file (a trial debug build) — download it and install directly on Android 8.1 or later, no account needed.",
      "dev.eyebrow": "The developer",
      "dev.t": "One person — from the gym floor to every line of code.",
      "dev.p1": "IronLog is designed and developed by Trần Huy Hoàng, a developer in Vietnam who trains by the very method the app records — so every screen starts from a very real question: what does today's session need to show, so that tomorrow's session is better?",
      "dev.p2": "There is no large team behind it, and no feature was added just to fill space. Every part of IronLog — the programs, the rest timer, the strength curves, the Vietnamese food table — grew out of the author's own real training sessions, and stayed only if it made the next session clearer than the last.",
      "dev.role": "Design &amp; development of IronLog",
      "scope.t": "Design scope and the interactive prototype",
      "scope.p1": "This design study closes a full loop: a morning overview screen, a Push Day A session with three Bench Press sets you can actually log, a review screen with an RIR choice, and a simulated result added into the week's progress. The less cheerful states are kept and stated plainly too: pausing mid-rest, the warning when you tap review with no sets logged, and an old week without enough data to conclude anything.",
      "scope.p2": "The visual system uses a near-black dark background for the presentation, the volt color as a controlled accent and generous whitespace around the numbers. Tabular numerals, clear labels and editable review steps carry the interaction. The browser prototype runs on a fictional account, sets and loads; it presents the interface design and interaction states — it does not connect to real training, sync data, or promise physique results.",
      "foot.credit": "IronLog — Designed and developed by Trần Huy Hoàng.",
      "foot.fine": "The browser prototype uses fictional data and only simulates interaction — it is not the real Android app.",
      "foot.n1": "Programs",
      "foot.n2": "Decision system",
      "foot.n3": "Developer",
      "foot.n4": "Scope"
    }
  };

  /* ---------- language switcher ---------- */
  var LANG_KEY = "site-lang";
  var toggle = document.getElementById("langToggle");
  var menu = document.getElementById("langMenu");
  var codeEl = document.getElementById("langCode");
  var options = menu ? Array.prototype.slice.call(menu.querySelectorAll(".lang-opt")) : [];

  function applyLang(lang) {
    var dict = I18N[lang] || I18N.vi;
    document.documentElement.lang = lang === "en" ? "en" : "vi";
    document.title = dict["meta.title"];
    var metaDesc = document.querySelector('meta[name="description"]');
    if (metaDesc) metaDesc.setAttribute("content", dict["meta.desc"]);
    document.querySelectorAll("[data-i18n]").forEach(function (el) {
      var key = el.getAttribute("data-i18n");
      if (dict[key] != null) el.innerHTML = dict[key];
    });
    document.querySelectorAll("[data-i18n-alt]").forEach(function (el) {
      var key = el.getAttribute("data-i18n-alt");
      if (dict[key] != null) el.setAttribute("alt", dict[key]);
    });
    if (codeEl) codeEl.textContent = lang.toUpperCase();
    options.forEach(function (opt) {
      var active = opt.getAttribute("data-lang") === lang;
      opt.classList.toggle("is-active", active);
      opt.setAttribute("aria-selected", active ? "true" : "false");
    });
    try { localStorage.setItem(LANG_KEY, lang); } catch (e) { /* private mode */ }
  }

  function closeMenu() {
    if (!menu) return;
    menu.hidden = true;
    if (toggle) toggle.setAttribute("aria-expanded", "false");
  }
  function openMenu() {
    if (!menu) return;
    menu.hidden = false;
    if (toggle) toggle.setAttribute("aria-expanded", "true");
  }

  if (toggle && menu) {
    toggle.addEventListener("click", function (ev) {
      ev.stopPropagation();
      if (menu.hidden) openMenu(); else closeMenu();
    });
    options.forEach(function (opt) {
      opt.addEventListener("click", function () {
        applyLang(opt.getAttribute("data-lang"));
        closeMenu();
      });
    });
    document.addEventListener("click", function (ev) {
      if (!menu.hidden && !ev.target.closest(".lang-switch")) closeMenu();
    });
    document.addEventListener("keydown", function (ev) {
      if (ev.key === "Escape") closeMenu();
    });
  }

  var saved = "vi";
  try { saved = localStorage.getItem(LANG_KEY) || "vi"; } catch (e) { /* ignore */ }
  if (saved !== "vi" && saved !== "en") saved = "vi";
  applyLang(saved);

  /* ---------- GSAP motion (progressive enhancement) ---------- */
  var reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

  if (window.gsap && window.ScrollTrigger && !reduceMotion) {
    gsap.registerPlugin(ScrollTrigger);

    /* Hero entrance */
    gsap.timeline({ defaults: { ease: "power3.out" } })
      .from(".hero__copy .eyebrow", { y: 22, opacity: 0, duration: 0.7 }, 0.1)
      .from(".hero__copy h1", { y: 36, opacity: 0, duration: 0.9 }, "-=0.45")
      .from(".hero__copy .lead, .hero__copy .honest", { y: 24, opacity: 0, duration: 0.8, stagger: 0.12 }, "-=0.55")
      .from(".hero__copy .actions", { y: 20, opacity: 0, duration: 0.7 }, "-=0.5")
      .from(".hero__meta li", { y: 16, opacity: 0, duration: 0.6, stagger: 0.09 }, "-=0.45")
      .from(".hero__visual", { y: 56, opacity: 0, rotate: 1.5, duration: 1.05 }, 0.25)
      .from(".hero__caption", { opacity: 0, duration: 0.8 }, "-=0.5");

    /* Hero photo slow drift */
    gsap.to("[data-hero-parallax]", {
      yPercent: 12, ease: "none",
      scrollTrigger: { trigger: ".hero", start: "top top", end: "bottom top", scrub: true }
    });

    /* Scroll reveals */
    gsap.utils.toArray("[data-reveal]").forEach(function (el) {
      gsap.from(el, {
        y: 34, opacity: 0, duration: 0.9, ease: "power3.out",
        scrollTrigger: { trigger: el, start: "top 86%" }
      });
    });

    /* Section heads: number slides in, title follows */
    gsap.utils.toArray(".slide__head").forEach(function (head) {
      var no = head.querySelector(".slide__no");
      var rest = head.querySelector("div");
      var tl = gsap.timeline({ scrollTrigger: { trigger: head, start: "top 84%" } });
      if (no) tl.from(no, { x: -46, opacity: 0, duration: 0.8, ease: "power3.out" }, 0);
      if (rest) tl.from(rest, { y: 26, opacity: 0, duration: 0.8, ease: "power3.out" }, 0.12);
      if (no) {
        gsap.to(no, {
          yPercent: 18, ease: "none",
          scrollTrigger: { trigger: head.closest(".slide") || head, start: "top bottom", end: "bottom top", scrub: true }
        });
      }
    });

    /* Progress bars grow on entry */
    gsap.utils.toArray(".bar__fill").forEach(function (fill) {
      gsap.from(fill, {
        scaleX: 0, transformOrigin: "left center", duration: 1.1, ease: "power3.inOut",
        scrollTrigger: { trigger: fill, start: "top 92%" }
      });
    });

    /* Photo bands: image parallax + caption rise */
    gsap.utils.toArray(".band").forEach(function (band) {
      var img = band.querySelector("[data-band-parallax]");
      var cap = band.querySelector(".band__text");
      if (img) {
        gsap.fromTo(img, { yPercent: -8 }, {
          yPercent: 8, ease: "none",
          scrollTrigger: { trigger: band, start: "top bottom", end: "bottom top", scrub: true }
        });
      }
      if (cap) {
        gsap.from(cap, {
          y: 40, opacity: 0, duration: 0.9, ease: "power3.out",
          scrollTrigger: { trigger: band, start: "top 62%" }
        });
      }
    });

    /* Phone mockups: gentle settle-in tilt */
    gsap.utils.toArray(".phone--mini, .phone--center").forEach(function (phone) {
      gsap.from(phone, {
        y: 44, rotate: phone.classList.contains("phone--center") ? 0 : 1.2, opacity: 0,
        duration: 1, ease: "power3.out",
        scrollTrigger: { trigger: phone, start: "top 88%" }
      });
    });
  }

  /* ---------- Three.js hero accent: abstract dumbbell ---------- */
  (function initHeroScene() {
    var canvas = document.getElementById("heroCanvas");
    if (!canvas || !window.THREE || reduceMotion) return;
    if (window.innerWidth < 760) return; /* keep small screens light */

    var renderer;
    try {
      renderer = new THREE.WebGLRenderer({ canvas: canvas, alpha: true, antialias: true });
    } catch (e) {
      return; /* WebGL unavailable — hero photo + copy carry the section */
    }
    renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2));

    var scene = new THREE.Scene();
    var camera = new THREE.PerspectiveCamera(38, 1, 0.1, 100);
    camera.position.set(0, 0.6, 9);

    var VOLT = 0xd6f53c;
    var STEEL = 0x8f9a94;
    var DARK = 0x232b27;

    var group = new THREE.Group();

    var steelMat = new THREE.MeshStandardMaterial({ color: STEEL, metalness: 0.85, roughness: 0.35 });
    var darkMat = new THREE.MeshStandardMaterial({ color: DARK, metalness: 0.6, roughness: 0.5 });
    var voltMat = new THREE.MeshStandardMaterial({ color: VOLT, emissive: VOLT, emissiveIntensity: 0.55, metalness: 0.2, roughness: 0.4 });

    /* Bar */
    var bar = new THREE.Mesh(new THREE.CylinderGeometry(0.09, 0.09, 5.4, 24), steelMat);
    bar.rotation.z = Math.PI / 2;
    group.add(bar);

    /* Plates + lime rings, mirrored on both sides */
    var plateSpecs = [
      { r: 1.05, w: 0.30, x: 1.95 },
      { r: 0.92, w: 0.26, x: 2.28 },
      { r: 0.78, w: 0.22, x: 2.58 }
    ];
    [1, -1].forEach(function (side) {
      plateSpecs.forEach(function (spec) {
        var plate = new THREE.Mesh(new THREE.CylinderGeometry(spec.r, spec.r, spec.w, 48), darkMat);
        plate.rotation.z = Math.PI / 2;
        plate.position.x = spec.x * side;
        group.add(plate);
        var ring = new THREE.Mesh(new THREE.TorusGeometry(spec.r - 0.015, 0.022, 12, 64), voltMat);
        ring.rotation.y = Math.PI / 2;
        ring.position.x = (spec.x + spec.w / 2 + 0.005) * side;
        group.add(ring);
      });
      var collar = new THREE.Mesh(new THREE.CylinderGeometry(0.17, 0.17, 0.22, 24), steelMat);
      collar.rotation.z = Math.PI / 2;
      collar.position.x = 2.86 * side;
      group.add(collar);
    });

    /* Floating dust particles */
    var pCount = 130;
    var pGeo = new THREE.BufferGeometry();
    var pos = new Float32Array(pCount * 3);
    for (var i = 0; i < pCount; i++) {
      pos[i * 3] = (Math.random() - 0.5) * 11;
      pos[i * 3 + 1] = (Math.random() - 0.5) * 6;
      pos[i * 3 + 2] = (Math.random() - 0.5) * 4 - 1;
    }
    pGeo.setAttribute("position", new THREE.BufferAttribute(pos, 3));
    var particles = new THREE.Points(pGeo, new THREE.PointsMaterial({
      color: VOLT, size: 0.045, transparent: true, opacity: 0.55, depthWrite: false
    }));
    scene.add(particles);

    group.rotation.set(0.35, -0.5, 0.12);
    scene.add(group);

    scene.add(new THREE.AmbientLight(0xffffff, 0.5));
    var key = new THREE.DirectionalLight(0xffffff, 1.6);
    key.position.set(4, 6, 6);
    scene.add(key);
    var rim = new THREE.PointLight(VOLT, 12, 30);
    rim.position.set(-5, -2, 4);
    scene.add(rim);

    var targetRX = 0, targetRY = 0;
    window.addEventListener("pointermove", function (ev) {
      targetRY = (ev.clientX / window.innerWidth - 0.5) * 0.35;
      targetRX = (ev.clientY / window.innerHeight - 0.5) * 0.22;
    }, { passive: true });

    function resize() {
      var w = canvas.clientWidth, h = canvas.clientHeight;
      if (!w || !h) return;
      renderer.setSize(w, h, false);
      camera.aspect = w / h;
      camera.updateProjectionMatrix();
    }
    resize();
    window.addEventListener("resize", resize);

    var running = true;
    if ("IntersectionObserver" in window) {
      new IntersectionObserver(function (entries) {
        running = entries[0].isIntersecting;
      }, { threshold: 0.02 }).observe(canvas);
    }
    document.addEventListener("visibilitychange", function () {
      running = !document.hidden;
    });

    var clock = new THREE.Clock();
    (function animate() {
      requestAnimationFrame(animate);
      if (!running) return;
      var t = clock.getElapsedTime();
      group.rotation.y += 0.0032;
      group.rotation.x += ((0.35 + targetRX + Math.sin(t * 0.4) * 0.05) - group.rotation.x) * 0.04;
      group.rotation.z = 0.12 + Math.sin(t * 0.3) * 0.03;
      group.position.y = Math.sin(t * 0.8) * 0.14;
      group.position.x += (targetRY * 1.4 - group.position.x) * 0.03;
      particles.rotation.y = t * 0.02;
      renderer.render(scene, camera);
    })();
  })();
})();
