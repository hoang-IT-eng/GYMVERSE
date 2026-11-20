/* IronLog landing: language switch, scroll story, small details.
   English lives in index.html; this file only carries the Vietnamese copy. */
(function () {
  "use strict";

  var root = document.documentElement;
  var LANG_KEY = "ironlog-lang";

  var VI = {
    "doc.title": "IronLog: sổ tập gym trên Android, tính sẵn hiệp tiếp theo",
    "doc.desc": "IronLog (GYMVERSE) là sổ tập gym offline trên Android. Ghi hiệp theo kg và rep, nhận mức tạ buổi sau theo double progression, đồng hồ nghỉ trên màn hình khóa và ghi bữa ăn Việt.",
    "skip": "Tới nội dung",
    "nav.overload": "Mức tạ",
    "nav.rest": "Nghỉ",
    "nav.progress": "Tiến bộ",
    "nav.food": "Ăn uống",
    "nav.apk": "Tải APK",
    "hero.title": '<span class="line"><span>Ghi hiệp này.</span></span><span class="line"><span>Biết trước hiệp sau.</span></span>',
    "hero.lead": "IronLog là sổ tập gym trên Android. Ghi từng hiệp theo kg và rep, ứng dụng gợi ý mức tạ cho buổi sau dựa trên buổi trước. Mọi dữ liệu nằm trên điện thoại của bạn.",
    "cta.apk": "Tải APK",
    "cta.proto": "Dùng thử trên trình duyệt",
    "hero.note": "Android 8.1 trở lên. Miễn phí, không cần tài khoản.",
    "log.title": "Ghi hiệp trước khi phấn kịp khô.",
    "log.body": "Chỉnh kg và rep bằng nút tăng giảm nhanh rồi ghi hiệp. Hiệp được lưu ngay trên máy và đồng hồ nghỉ tự chạy.",
    "log.f1": "bài tập có sẵn, bài nào cũng có tên tiếng Việt, các bước vào thế, mẹo tập và những lỗi form cần tránh.",
    "log.f2": "giáo án dựng sẵn, cộng thêm giáo án bạn tự tạo.",
    "log.f3": "nghỉ mặc định, chỉnh được từ 15 giây đến 15 phút.",
    "ladder.title": "Đọc buổi tập trước, chọn mức tạ cho hôm nay.",
    "ladder.body": "IronLog dùng double progression trên các hiệp nặng nhất: làm đầy khoảng rep rồi mới tăng tạ. Ví dụ dưới đây là Bench Press, khoảng 6–10 rep, mỗi bước 2.5&nbsp;kg.",
    "ladder.note": "Tắt tự tăng tạ trong mục Cá nhân thì ứng dụng giữ nguyên mức tạ. Mức tạ được làm tròn theo bánh tạ nhỏ nhất phòng tập của bạn có: 0.5, 1.25 hoặc 2.5&nbsp;kg.",
    "ladder.range": "6–10 rep, +2.5 kg",
    "forge.title": "Thanh đòn, đã lắp tạ.",
    "forge.body": "Hai mươi, mười, năm và khóa tạ, lắp theo đúng thứ tự đó ở mỗi bên của thanh 20 kg. Cuộn tiếp, thanh đòn sẽ xoay và tháo ra theo cùng thứ tự — thứ tự IronLog dùng khi làm tròn mức tạ tiếp theo theo bánh tạ nhỏ nhất phòng bạn có.",
    "forge.capFull": "Trên thanh: 95 kg",
    "forge.note": "Vẽ 3D ngay trong trình duyệt. Nếu bạn tắt chuyển động, chương này chỉ còn chữ.",
    "scene.last": "Buổi trước",
    "scene.next": "Hôm nay",
    "s1.name": "Đã đầy khoảng rep",
    "s1.rule": "Mọi set đạt 10 rep → tăng 2.5 kg",
    "s2.name": "Đang tích rep",
    "s2.rule": "Giữ tạ, nhắm 8 rep",
    "s3.name": "Tăng hơi sớm",
    "s3.rule": "Chưa đạt 6 rep → giảm 2.5 kg",
    "rest.title": "Màn hình khóa rồi, giờ nghỉ vẫn chạy.",
    "rest.body": "Đồng hồ chạy dưới dạng dịch vụ nền, đếm ngược ngay trong thông báo, nên vẫn chạy khi bạn chuyển ứng dụng hay khóa máy. Hết giờ, máy rung và nhắc bạn vào set tiếp theo.",
    "rest.notif": "Thông báo của IronLog: Nghỉ giữa set, chạm để mở buổi tập",
    "rest.caption": "Thông báo đúng như trong ứng dụng.",
    "progress.title": "Sức mạnh hiện rõ qua từng buổi tập.",
    "progress.body": "Với mỗi bài, IronLog ước tính 1RM bằng công thức Epley và vẽ biểu đồ cạnh khối lượng tập. Hiệp trên 12 rep không được tính vì công thức sẽ thổi phồng kết quả.",
    "chart.title": "1RM ước tính của Bench Press qua mười hai buổi, tăng từ 92 lên 103.1 kg (số liệu minh hoạ)",
    "chart.first": "Buổi 1",
    "chart.last": "Buổi 12",
    "chart.caption": "Bench Press, 1RM ước tính theo kg. Số liệu minh hoạ.",
    "volume.title": "Số hiệp mỗi tuần cho từng nhóm cơ",
    "volume.body": "Mỗi nhóm cơ có một vùng mục tiêu, từ khối lượng tối thiểu để cơ còn phát triển (MEV) đến mức tối đa bạn còn hồi phục được (MRV). Màn hình Hôm nay còn cho thấy chuỗi ngày tập và mức hồi phục của từng nhóm cơ.",
    "volume.caption": "Vùng mục tiêu lấy đúng từ ứng dụng. Số hiệp là số liệu minh hoạ.",
    "m.chest": "Ngực",
    "m.back": "Lưng xô",
    "m.quads": "Đùi trước",
    "m.hams": "Đùi sau và mông",
    "m.shoulders": "Vai",
    "m.arms": "Tay",
    "m.calves": "Bắp chân và bụng",
    "programs.title": "Bốn giáo án dùng được ngay từ ngày đầu.",
    "programs.caption": "Giáo án có sẵn, số ngày mỗi tuần và thời lượng mỗi buổi",
    "programs.th1": "Giáo án",
    "programs.th2": "Ngày mỗi tuần",
    "programs.th3": "Mỗi buổi",
    "programs.d1": "50–65 phút",
    "programs.d2": "60–70 phút",
    "programs.d3": "65–75 phút",
    "programs.after": "Hoặc tự tạo giáo án. Màn hình Hôm nay tự biết tới buổi nào dựa trên những bài bạn tập lần trước.",
    "food.title": "Bát phở cũng được tính.",
    "food.body": "Chụp ảnh bữa ăn, chọn ảnh có sẵn hoặc chỉ cần mô tả. Gemini tách từng món và ước lượng số gram, bạn sửa lại rồi lưu. Hoặc tự nhập từ bảng khoảng 90 món Việt, hay theo số trên nhãn.",
    "food.body2": "Mục tiêu mỗi ngày dựa trên BMR và TDEE, tính bằng Katch-McArdle (dựa trên khối lượng nạc) và đối chiếu với Mifflin-St Jeor.",
    "food.note": "Phân tích bữa ăn và huấn luyện viên tuần dùng Gemini, nên cần mạng và khóa API được thêm vào lúc build ứng dụng. Bản APK công khai được build không kèm khóa; nhập từ bảng món ăn vẫn dùng được.",
    "yours.title": "Tuần tập được tổng kết. Dữ liệu vẫn là của bạn.",
    "yours.h1": "Huấn luyện viên mỗi tuần",
    "yours.p1": "Ở tab Tiến bộ, Gemini tổng kết bảy ngày qua: số hiệp từng nhóm cơ so với MEV–MRV, bài đang chững lại, calo và đạm, rồi gợi ý điều cần đổi cho tuần tới.",
    "yours.h2": "Dữ liệu của bạn",
    "yours.p2": "Không cần tạo tài khoản. Buổi tập lưu trong cơ sở dữ liệu ngay trên máy. Xuất CSV để mở bằng Excel, hoặc sao lưu và khôi phục toàn bộ bằng một file JSON.",
    "close.title": "Hẹn bạn dưới thanh đòn.",
    "close.body": "Cài APK trên điện thoại Android 8.1 trở lên, hoặc dùng thử bản prototype trên trình duyệt trước. Prototype chạy bằng dữ liệu hư cấu.",
    "close.note": "APK là bản debug do GitHub Actions build tự động từ nhánh main.",
    "foot.made": "IronLog (GYMVERSE) do Trần Huy Hoàng thiết kế và xây dựng bằng Kotlin, Jetpack Compose và Room. Giấy phép MIT.",
    "foot.photos": "Ảnh trên Unsplash của",
    "foot.and": "và",
    "foot.top": "Lên đầu trang",
    "alt.hero": "Một người tập cúi xuống bên thanh đòn đã lắp tạ trong phòng gym tối, ánh đèn rọi từ trên",
    "alt.chalk": "Hai bàn tay xoa phấn, bụi trắng bay lên",
    "alt.load": "Bàn tay lắp bánh tạ màu đen vào thanh đòn",
    "alt.racks": "Phòng gym vắng với khung squat và thanh đòn đã lắp tạ",
    "alt.pho": "Bát phở bò với bánh phở và hành lá, nhìn từ trên xuống",
    "alt.phone": "Người đội mũ, đeo tai nghe xem điện thoại giữa hai hiệp",
    "alt.close": "Thanh đòn nằm trên sàn phòng gym, vệt nắng và bóng cửa sổ cắt ngang",
    "alt.none": ""
  };

  /* ---------- Language ---------- */
  var EN = {};
  var nodes = Array.prototype.slice.call(document.querySelectorAll("[data-i18n]"));
  var altNodes = Array.prototype.slice.call(document.querySelectorAll("[data-i18n-alt]"));
  var attrNodes = Array.prototype.slice.call(document.querySelectorAll("[data-i18n-attr]"));
  var metaDesc = document.querySelector('meta[name="description"]');
  nodes.forEach(function (el) { var k = el.getAttribute("data-i18n"); if (!(k in EN)) EN[k] = el.innerHTML; });
  altNodes.forEach(function (el) { EN[el.getAttribute("data-i18n-alt")] = el.getAttribute("alt"); });
  attrNodes.forEach(function (el) {
    el.getAttribute("data-i18n-attr").split(";").forEach(function (pair) {
      var p = pair.split(":"); EN[p[1]] = el.getAttribute(p[0]);
    });
  });
  EN["doc.title"] = document.title;
  EN["doc.desc"] = metaDesc ? metaDesc.getAttribute("content") : "";

  var current = "en";
  function tr(key) { var d = current === "vi" ? VI : EN; return d[key] != null ? d[key] : EN[key]; }

  function applyLang(lang, persist) {
    current = lang === "vi" ? "vi" : "en";
    root.lang = current;
    nodes.forEach(function (el) { var v = tr(el.getAttribute("data-i18n")); if (v != null) el.innerHTML = v; });
    altNodes.forEach(function (el) { var v = tr(el.getAttribute("data-i18n-alt")); if (v != null) el.setAttribute("alt", v); });
    attrNodes.forEach(function (el) {
      el.getAttribute("data-i18n-attr").split(";").forEach(function (pair) {
        var p = pair.split(":"); var v = tr(p[1]); if (v != null) el.setAttribute(p[0], v);
      });
    });
    document.title = tr("doc.title");
    if (metaDesc) metaDesc.setAttribute("content", tr("doc.desc"));
    document.querySelectorAll(".lang__btn").forEach(function (b) {
      b.setAttribute("aria-pressed", String(b.getAttribute("data-lang") === current));
    });
    if (persist) { try { localStorage.setItem(LANG_KEY, current); } catch (e) { /* private mode */ } }
    if (window.ScrollTrigger) window.ScrollTrigger.refresh();
  }

  var initial = "en";
  try {
    var saved = localStorage.getItem(LANG_KEY);
    if (saved === "vi" || saved === "en") initial = saved;
    else if (/^vi\b/i.test(navigator.language || "")) initial = "vi";
  } catch (e) { /* ignore */ }
  applyLang(initial, false);
  document.querySelectorAll(".lang__btn").forEach(function (b) {
    b.addEventListener("click", function () { applyLang(b.getAttribute("data-lang"), true); });
  });

  /* ---------- Nav ---------- */
  var nav = document.getElementById("nav");
  function onScrollNav() { nav.classList.toggle("is-solid", window.scrollY > 40); }
  onScrollNav();
  window.addEventListener("scroll", onScrollNav, { passive: true });

  var links = Array.prototype.slice.call(document.querySelectorAll(".nav__links a"));
  if ("IntersectionObserver" in window) {
    var io = new IntersectionObserver(function (entries) {
      entries.forEach(function (en) {
        if (!en.isIntersecting) return;
        links.forEach(function (a) { a.classList.toggle("is-current", a.getAttribute("href") === "#" + en.target.id); });
      });
    }, { rootMargin: "-45% 0px -50% 0px" });
    links.forEach(function (a) { var t = document.querySelector(a.getAttribute("href")); if (t) io.observe(t); });
  }

  /* ---------- Motion ---------- */
  var gsap = window.gsap;
  var ST = window.ScrollTrigger;
  if (!root.classList.contains("motion")) return;
  if (!gsap || !ST) { root.classList.remove("motion", "pin"); return; }
  gsap.registerPlugin(ST);

  // Smooth scroll
  var lenis = null;
  if (window.Lenis) {
    lenis = new window.Lenis({ duration: 1.1, smoothWheel: true });
    lenis.on("scroll", ST.update);
    gsap.ticker.add(function (t) { lenis.raf(t * 1000); });
    gsap.ticker.lagSmoothing(0);
    document.querySelectorAll('a[href^="#"]').forEach(function (a) {
      a.addEventListener("click", function (ev) {
        var id = a.getAttribute("href");
        var target = id === "#top" ? 0 : document.querySelector(id);
        if (target === null) return;
        ev.preventDefault();
        lenis.scrollTo(target, { offset: 0 });
        if (id !== "#top" && target.focus) { target.setAttribute("tabindex", "-1"); target.focus({ preventScroll: true }); }
      });
    });
  }

  // Opening: the one orchestrated moment
  var heroImg = document.querySelector(".hero__media img");
  var intro = gsap.timeline({ defaults: { ease: "expo.out" } });
  intro.to(heroImg, { scale: 1, duration: 2.4, ease: "power2.out" }, 0)
       .to(".hero__title .line > span", { yPercent: 0, y: 0, duration: 1.3, stagger: 0.14 }, 0.25)
       .from(".hero__lead", { y: 16, duration: 1.2 }, 0.6)
       .to(".hero__actions", { opacity: 1, duration: 1 }, 1.0)
       .to(".hero__note", { opacity: 1, duration: 1 }, 1.1);
  gsap.set(".hero__title .line > span", { yPercent: 105, y: 0 });
  // Once the opening has played, drop the hidden start state so a language
  // switch (which rebuilds the title spans) shows the new title at rest.
  intro.eventCallback("onComplete", function () {
    root.classList.add("intro-done");
    gsap.set(".hero__title .line > span", { clearProps: "transform" });
  });
  document.querySelectorAll(".lang__btn").forEach(function (b) {
    b.addEventListener("click", function () {
      if (root.classList.contains("intro-done")) return;
      intro.progress(1);
    });
  });

  // Hero drifts as it leaves
  gsap.to(".hero__media", { yPercent: 14, ease: "none", scrollTrigger: { trigger: ".hero", start: "top top", end: "bottom top", scrub: true } });
  gsap.to(".hero__copy", { yPercent: -10, opacity: 0.2, ease: "none", scrollTrigger: { trigger: ".hero", start: "40% top", end: "bottom top", scrub: true } });

  // Gentle parallax on full-bleed photography
  ["log", "programs", "yours", "close"].forEach(function (cls) {
    var img = document.querySelector("." + cls + "__media img");
    if (!img) return;
    gsap.fromTo(img, { yPercent: -6, scale: 1.12 }, { yPercent: 6, scale: 1.12, ease: "none", scrollTrigger: { trigger: "." + cls + "__media", start: "top bottom", end: "bottom top", scrub: true } });
  });

  // Double progression: pinned, the three sessions play in order
  if (root.classList.contains("pin")) {
    var scenes = gsap.utils.toArray(".scene");
    var ticks = gsap.utils.toArray(".ticks i");
    var tl = gsap.timeline({
      scrollTrigger: { trigger: ".ladder", start: "top top", end: "+=" + Math.round(window.innerHeight * 2.4), pin: ".ladder__pin", scrub: 0.6, anticipatePin: 1 }
    });
    scenes.forEach(function (scene, i) {
      var at = i * 1.0;
      var parts = scene.querySelectorAll(".scene__name, .sets li, .scene__rule, .load");
      if (i > 0) {
        tl.to(scenes[i - 1], { autoAlpha: 0, y: -24, duration: 0.25, ease: "power2.in" }, at - 0.05);
        tl.fromTo(scene, { autoAlpha: 0, y: 24 }, { autoAlpha: 1, y: 0, duration: 0.3, ease: "power2.out" }, at + 0.15);
      }
      if (i > 0) tl.from(parts, { opacity: 0, y: 12, stagger: 0.06, duration: 0.25, ease: "power2.out" }, at + 0.2);
      tl.fromTo(ticks[i], { "--p": 0 }, { "--p": 1, duration: 0.8, ease: "none" }, at);
    });
    tl.to({}, { duration: 0.25 });
    gsap.fromTo(".ladder__media img", { scale: 1.15, yPercent: -4 }, { scale: 1.02, yPercent: 4, ease: "none", scrollTrigger: { trigger: ".ladder", start: "top top", end: "+=" + Math.round(window.innerHeight * 2.4), scrub: true } });
  }

  // Rest: the clock runs down from the app's default 90 s as you scroll
  var clock = document.querySelector(".clock");
  var cm = document.querySelector(".clock__m");
  var cs = document.querySelector(".clock__s");
  var notifTime = document.querySelector("[data-clock]");
  function setClock(sec) {
    var s = Math.max(0, Math.round(sec));
    var m = Math.floor(s / 60), r = s % 60;
    cm.textContent = String(m);
    cs.textContent = (r < 10 ? "0" : "") + r;
    if (notifTime) notifTime.textContent = m + ":" + (r < 10 ? "0" : "") + r;
    clock.classList.toggle("is-zero", s === 0);
  }
  var restPin = root.classList.contains("pin");
  var restState = { t: 90 };
  gsap.to(restState, {
    t: 0, ease: "none",
    onUpdate: function () { setClock(restState.t); },
    scrollTrigger: restPin
      ? { trigger: ".rest", start: "top top", end: "+=" + Math.round(window.innerHeight * 1.6), pin: ".rest__pin", scrub: 0.4, anticipatePin: 1 }
      : { trigger: ".rest", start: "top 70%", end: "bottom 60%", scrub: 0.4 }
  });
  gsap.fromTo(".rest__media img", { scale: 1.12 }, { scale: 1, ease: "none", scrollTrigger: { trigger: ".rest", start: "top bottom", end: restPin ? "+=" + Math.round(window.innerHeight * 2.6) : "bottom top", scrub: true } });

  // Progress: line draws in, bars fill
  gsap.fromTo(".chart__line", { strokeDasharray: 1, strokeDashoffset: 1 }, { strokeDashoffset: 0, ease: "none", scrollTrigger: { trigger: ".progress__chart", start: "top 80%", end: "bottom 55%", scrub: 0.5 } });
  gsap.from(".chart__dot, .chart__last", { opacity: 0, stagger: 0.04, duration: 0.3, scrollTrigger: { trigger: ".progress__chart", start: "top 75%" } });
  gsap.from(".bar__fill", { scaleX: 0, duration: 1.2, ease: "expo.out", stagger: 0.06, scrollTrigger: { trigger: ".bars", start: "top 80%" } });

  window.addEventListener("load", function () { ST.refresh(); });
})();

/* ---------- Forge: the barbell in 3D ----------
   A separate, self-gating layer on top of the page above. It loads
   three.js and the engine lazily, borrows the page's ScrollTrigger for
   progress, and only then adds .forge-on to <body>. If anything is
   unavailable — ?forge=off, reduced motion, a low-end device, saved
   data, software WebGL — nothing here runs and the page stands exactly
   as it does without this block. */
(function () {
  "use strict";

  var root = document.documentElement;
  var body = document.body;
  var params = new URLSearchParams(location.search);
  var force = params.get("forge") === "on";
  if (params.get("forge") === "off") return;

  /* Capability gates. ?forge=on (QA) bypasses them all; the engine
     still refuses software renderers unless forced. The page's own head
     script withholds .motion when the user prefers reduced motion, so
     that class doubles as the reduced-motion gate. */
  if (!force) {
    if (!root.classList.contains("motion")) return;
    if (navigator.webdriver) return;
    if ((navigator.hardwareConcurrency || 8) <= 2) return;
    if ((navigator.deviceMemory || 8) <= 2) return;
    if (navigator.connection && navigator.connection.saveData) return;
  }

  var hosts = {};
  document.querySelectorAll("[data-forge-host]").forEach(function (el) {
    hosts[el.getAttribute("data-forge-host")] = el;
  });
  if (!hosts.hero || !hosts.forge || !hosts.closing) return;

  var capEl = document.querySelector("[data-forge-cap]");
  var repsEl = document.querySelector("[data-forge-reps]");

  /* The two strings this layer owns (the rest of the chapter goes
     through the page's own data-i18n, above). Index 0 is the full
     stack; 1–4 follow the claimed unit: 20, 10, 5, collars. */
  var CAPS = {
    en: ["On the bar: 95 kg", "Off first: the 20s", "Then the 10s", "Then the 5s", "Last, the collars"],
    vi: ["Trên thanh: 95 kg", "Rời trước: bánh 20", "Rồi đến bánh 10", "Tiếp theo: bánh 5", "Cuối cùng: khóa tạ"]
  };
  var capUnit = -1;
  var reps = 0;
  var mode = null;
  var canvas = null;
  var started = false;

  function lang() { return root.lang === "vi" ? "vi" : "en"; }
  function renderCap() {
    if (!capEl) return;
    var set = CAPS[lang()];
    capEl.textContent = set[capUnit + 1] || set[0];
  }
  function renderReps() {
    if (!repsEl) return;
    if (reps < 1 || mode !== "closing") { repsEl.hidden = true; return; }
    repsEl.textContent = lang() === "vi"
      ? "Số rep của bạn — " + reps + " · chạm vào thanh để nâng"
      : "Your reps — " + reps + " · tap the bar to lift";
    repsEl.hidden = false;
  }

  function loadScript(src) {
    return new Promise(function (resolve, reject) {
      var s = document.createElement("script");
      s.src = src;
      s.onload = function () { resolve(); };
      s.onerror = function () { reject(new Error(src)); };
      document.head.appendChild(s);
    });
  }

  function boot() {
    if (started) return;
    started = true;
    loadScript("vendor/three.min.js")
      .then(function () { return loadScript("forge3d.js"); })
      .then(function () {
        /* plate faces are drawn on canvas at init: give Archivo a
           moment to arrive so the numerals use the page's own type */
        if (document.fonts && document.fonts.ready) {
          return Promise.race([
            document.fonts.ready,
            new Promise(function (r) { setTimeout(r, 1200); })
          ]);
        }
      })
      .then(function () {
        if (!window.GymForge) return;
        canvas = document.createElement("canvas");
        canvas.setAttribute("aria-hidden", "true");
        hosts.hero.appendChild(canvas);
        var ok = window.GymForge.init(canvas, {
          force: force,
          onPlateChange: function (unit) { capUnit = unit; renderCap(); },
          onRep: function () { reps += 1; renderReps(); },
          onFirstFrame: function () { body.classList.add("forge-on"); },
          onDead: function () { body.classList.remove("forge-on"); }
        });
        if (!ok) {
          if (canvas.parentElement) canvas.parentElement.removeChild(canvas);
          canvas = null;
          return;
        }
        wire();
      })
      .catch(function () { /* engine unavailable: the page stands as-is */ });
  }

  function wire() {
    var F = window.GymForge;

    function setMode(next) {
      if (!hosts[next]) return;
      if (mode !== next) {
        mode = next;
        hosts[next].appendChild(canvas);
        F.setMode(next);
        F.observeHost();
      }
      F.resize();
      renderReps();
    }

    /* The canvas lives in whichever host is most on screen. */
    var ratios = { hero: 0, forge: 0, closing: 0 };
    function pick() {
      var best = null, bestR = 0.22;
      ["hero", "forge", "closing"].forEach(function (name) {
        if (ratios[name] > bestR) { bestR = ratios[name]; best = name; }
      });
      if (best) { setMode(best); F.setActive(true); }
      else F.setActive(false);
    }
    setMode("hero");
    if ("IntersectionObserver" in window) {
      var io = new IntersectionObserver(function (entries) {
        entries.forEach(function (en) {
          ratios[en.target.getAttribute("data-forge-host")] = en.isIntersecting ? en.intersectionRatio : 0;
        });
        pick();
      }, { threshold: [0, 0.1, 0.22, 0.35, 0.5, 0.75, 1] });
      Object.keys(hosts).forEach(function (name) { io.observe(hosts[name]); });
    } else {
      F.setActive(true);
    }

    /* Scroll progress, on the page's own ScrollTrigger. */
    var gsap = window.gsap, ST = window.ScrollTrigger;
    if (gsap && ST) {
      ST.create({
        trigger: ".hero", start: "top top", end: "bottom top", scrub: true,
        onUpdate: function (self) { F.setProgress("hero", self.progress); }
      });
      var pinOK = root.classList.contains("pin");
      ST.create({
        trigger: ".forge",
        start: pinOK ? "top top" : "top bottom",
        end: pinOK ? "+=140%" : "bottom top",
        pin: pinOK ? ".forge__pin" : false,
        scrub: 0.6,
        anticipatePin: pinOK ? 1 : 0,
        onUpdate: function (self) { F.setProgress("forge", self.progress); }
      });
    }

    /* Pointer leans the camera; a tap on the bar's sections lifts. */
    window.addEventListener("pointermove", function (ev) {
      F.setPointer(
        (ev.clientX / window.innerWidth) * 2 - 1,
        (ev.clientY / window.innerHeight) * 2 - 1
      );
    }, { passive: true });
    [".hero", ".close"].forEach(function (sel) {
      var sec = document.querySelector(sel);
      if (!sec) return;
      sec.addEventListener("click", function (ev) {
        if (ev.target.closest("a, button")) return;
        F.doRep();
      });
    });

    /* The page's language switch flips <html lang>: follow it for the
       two strings owned here. */
    if (window.MutationObserver) {
      new MutationObserver(function () { renderCap(); renderReps(); })
        .observe(root, { attributes: true, attributeFilter: ["lang"] });
    }
    renderCap();
  }

  /* Lazy start: when the hero approaches, or on idle — never on the
     critical path. */
  var heroSeen = false;
  function maybeBoot() { if (heroSeen) boot(); }
  if ("IntersectionObserver" in window) {
    var bio = new IntersectionObserver(function (entries) {
      entries.forEach(function (en) {
        if (en.isIntersecting) { heroSeen = true; bio.disconnect(); maybeBoot(); }
      });
    }, { rootMargin: "600px" });
    bio.observe(hosts.hero);
  } else {
    heroSeen = true;
    maybeBoot();
  }
  if ("requestIdleCallback" in window) {
    requestIdleCallback(function () { heroSeen = true; maybeBoot(); }, { timeout: 2500 });
  } else {
    setTimeout(function () { heroSeen = true; maybeBoot(); }, 1800);
  }

  /* QA hooks, in the open like the rest of the page. */
  window.__forgeState = function () {
    return {
      mode: mode,
      reps: reps,
      ready: !!(window.GymForge && window.GymForge.isReady()),
      on: body.classList.contains("forge-on")
    };
  };
  window.__forgeRep = function () { if (window.GymForge) window.GymForge.doRep(); };
})();
