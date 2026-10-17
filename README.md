<div align="center">

# GYMVERSE
### IronLog — track workouts, routines & hypertrophy progress

Ứng dụng Android ghi nhật ký tập gym: giáo án, kho bài tập tiếng Việt, rest timer và theo dõi tiến bộ.

[Tính năng](#-tính-năng) · [Công nghệ](#-công-nghệ) · [Cài đặt](#-cài-đặt) · [Cấu trúc](#-cấu-trúc-dự-án) · [Đóng góp](#-đóng-góp)

<br/>

![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Android](https://img.shields.io/badge/Android-API%2027%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Room](https://img.shields.io/badge/Room-Local%20DB-FF9800?style=for-the-badge&logo=sqlite&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

<br/>

`git clone https://github.com/hoang-IT-eng/GYMVERSE.git`

</div>

---

> [!IMPORTANT]
> Repo GitHub: **GYMVERSE** · App name trong máy: **IronLog**  
> Mở project bằng **Android Studio** (Ladybug / phiên bản mới) rồi Run trên emulator hoặc thiết bị thật.

---

## ✨ Tính năng

| Module | Mô tả |
|--------|--------|
| **Home** | Tổng quan ngày tập, streak, gợi ý hypertrophy, phục hồi nhóm cơ |
| **Routines & Library** | Giáo án có sẵn + tạo giáo án tùy chỉnh; kho bài tập tiếng Việt kèm tip / cảnh báo form |
| **Workout** | Ghi set (rep × kg), chọn bài nhanh, theo dõi phiên tập đang chạy |
| **Rest Timer** | Đếm giờ nghỉ nền + notification, banner khi đang rest |
| **Progress** | Hub tiến bộ, biểu đồ volume / sức mạnh theo thời gian |
| **Profile** | Thông tin cá nhân & cài đặt nhanh |

---

## 🛠 Công nghệ

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Navigation Compose** — tab Home / Routines / Progress / Profile
- **Room** + Coroutines — lưu workout, set, routine offline
- **Foreground Service** — rest timer chạy nền
- **MVVM** — ViewModel + StateFlow / collectAsStateWithLifecycle
- **minSdk 27** · **targetSdk 36** · **AGP 8.13**

---

## 🚀 Cài đặt

### Yêu cầu

- Android Studio (khuyến nghị bản mới nhất)
- JDK 11+
- Thiết bị/emulator Android 8.1+ (API 27)

### Chạy project

```bash
git clone https://github.com/hoang-IT-eng/GYMVERSE.git
cd GYMVERSE
```

1. Mở thư mục bằng **Android Studio**
2. Đợi Gradle sync xong
3. Chọn emulator hoặc máy thật → **Run ▶**

Hoặc build từ terminal:

```bash
./gradlew :app:assembleDebug
```

APK debug: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📱 Screenshots

> Thêm ảnh chụp màn hình vào `docs/` rồi bỏ comment các dòng dưới.

<!--
<p align="center">
  <img src="docs/home.png" width="220" alt="Home" />
  <img src="docs/routines.png" width="220" alt="Routines" />
  <img src="docs/workout.png" width="220" alt="Workout" />
</p>
-->

```
docs/
├── home.png
├── routines.png
├── workout.png
└── progress.png
```

---

## 📂 Cấu trúc dự án

```
app/src/main/java/com/example/gymapp/
├── data/           # Room entities, DAO, Database, Repository
├── domain/         # Catalog bài tập, routine plans, models
├── service/        # Rest timer service & controller
└── ui/
    ├── home/       # Màn hình hôm nay
    ├── routines/   # Giáo án + kho bài + editor
    ├── workout/    # Phiên tập đang chạy
    ├── progress/   # Tiến bộ & biểu đồ
    ├── profile/    # Cá nhân
    ├── components/ # Shell, charts, rest banner
    └── theme/      # IronLog design system
```

---

## 🗺 Roadmap

- [ ] Thêm screenshot UI vào README
- [ ] Export / import lịch sử tập
- [ ] Widget rest timer
- [ ] Dark / light theme toggle
- [ ] Đa ngôn ngữ (VI / EN)

---

## 🤝 Đóng góp

Pull request và issue đều chào đón.

1. Fork repo
2. Tạo branch: `git checkout -b feature/ten-tinh-nang`
3. Commit: `git commit -m "Add: mô tả ngắn"`
4. Push: `git push origin feature/ten-tinh-nang`
5. Mở Pull Request trên GitHub

---

## 📄 License

MIT — tự do dùng, sửa và chia sẻ. Ghi credit khi fork thì càng tốt.

---

<div align="center">

**GYMVERSE / IronLog** · Built with Kotlin & Jetpack Compose

[⬆ Back to top](#gymverse)

</div>
