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

  /* ---------- Motion pass: a way in for every section ----------
     One recurring device (a short plate rule draws itself above each
     section head) and a different entrance per section. Everything is
     a from-state or a CSS variable: delete this block and the page is
     exactly the page without it. Under automation nothing is hidden —
     the pass simply does not run. */
  if (!navigator.webdriver) {
    var SMALL = window.matchMedia("(max-width: 700px)").matches;
    var DY = SMALL ? 16 : 28;

    function rise(targets, opts) {
      opts = opts || {};
      var els = gsap.utils.toArray(targets);
      if (!els.length) return;
      gsap.from(els, {
        y: opts.y != null ? opts.y : DY,
        autoAlpha: 0,
        duration: opts.duration || 1.05,
        ease: opts.ease || "expo.out",
        stagger: opts.stagger || 0,
        delay: opts.delay || 0,
        clearProps: "transform,opacity,visibility",
        scrollTrigger: { trigger: opts.trigger || els[0], start: opts.start || "top 84%", once: true }
      });
    }

    // The recurring device: one gold rule per section head
    gsap.utils.toArray(".log__copy, .ladder__head, .forge__head, .rest__copy, .progress__copy, .volume__head, .programs__body, .food__copy, .yours__body").forEach(function (head) {
      head.classList.add("mp-head");
      gsap.to(head, { "--mp-rule": 1, duration: 1.2, ease: "expo.out", scrollTrigger: { trigger: head, start: "top 86%", once: true } });
    });

    // Nav settles in with the opening
    gsap.from(".nav", { y: -28, autoAlpha: 0, duration: 1.1, ease: "expo.out", delay: 0.1, clearProps: "transform,opacity,visibility" });

    // The log: the chalk photo wipes in, the facts count up
    gsap.fromTo(".log__media", { clipPath: "inset(0% 100% 0% 0%)" }, { clipPath: "inset(0% 0% 0% 0%)", duration: 1.4, ease: "expo.out", scrollTrigger: { trigger: ".log", start: "top 78%", once: true } });
    rise(".log__copy .h2, .log__copy .body", { trigger: ".log__copy", start: "top 82%", stagger: 0.12 });
    rise(".facts__row", { trigger: ".facts", start: "top 88%", stagger: 0.1, y: 18 });
    document.querySelectorAll(".facts dt").forEach(function (dt) {
      var m = /^(\d+(?:\.\d+)?)([\s\S]*)$/.exec(dt.textContent.trim());
      if (!m) return;
      var target = parseFloat(m[1]), rest = m[2], dec = (m[1].split(".")[1] || "").length, obj = { v: 0 };
      gsap.to(obj, {
        v: target, duration: 1.5, ease: "expo.out",
        scrollTrigger: { trigger: dt, start: "top 90%", once: true },
        onUpdate: function () { dt.textContent = obj.v.toFixed(dec) + rest; },
        onComplete: function () { dt.textContent = m[1] + rest; }
      });
    });

    // Next load: head and stage arrive before the pin takes over
    rise(".ladder__head .h2, .ladder__head .body, .ladder__head .small", { trigger: ".ladder__head", start: "top 84%", stagger: 0.12 });
    rise(".ladder__stage", { trigger: ".ladder__stage", start: "top 84%", y: SMALL ? 20 : 40, duration: 1.2 });
    gsap.fromTo(".ladder__media", { clipPath: "inset(0% 0% 0% 100%)" }, { clipPath: "inset(0% 0% 0% 0%)", duration: 1.4, ease: "expo.out", scrollTrigger: { trigger: ".ladder", start: "top 78%", once: true } });

    // The bar, loaded: entry dressing only — the 3D sequence is untouched
    rise(".forge__head .h2, .forge__head .body, .forge__cap, .forge__head .small", { trigger: ".forge", start: "top 74%", stagger: 0.12, duration: 1.15 });

    // Rest: everything moves slowly; the notification drops in like one
    var restTl = gsap.timeline({ defaults: { ease: "power2.out" }, scrollTrigger: { trigger: ".rest", start: "top 68%", once: true } });
    restTl.from(".clock", { autoAlpha: 0, duration: 1.8, clearProps: "opacity,visibility" }, 0)
      .from(".rest__copy .h2", { y: 26, autoAlpha: 0, duration: 1.6, clearProps: "transform,opacity,visibility" }, 0.1)
      .from(".rest__copy .body", { y: 22, autoAlpha: 0, duration: 1.6, clearProps: "transform,opacity,visibility" }, 0.28)
      .from(".notif", { y: -16, scale: 0.97, autoAlpha: 0, duration: 1.1, ease: "power3.out", clearProps: "transform,opacity,visibility" }, 0.6)
      .from(".rest__copy .small", { autoAlpha: 0, duration: 1.2, clearProps: "opacity,visibility" }, 0.85);

    // Progress: copy and formula arrive; the values join the bar fills
    rise(".progress__copy .h2, .progress__copy .body", { trigger: ".progress__copy", start: "top 84%", stagger: 0.12 });
    gsap.from(".formula", { x: -24, autoAlpha: 0, duration: 1, ease: "expo.out", clearProps: "transform,opacity,visibility", scrollTrigger: { trigger: ".formula", start: "top 88%", once: true } });
    rise(".progress__chart", { trigger: ".progress__chart", start: "top 84%", y: 32, duration: 1.2 });
    rise(".volume__head .h3, .volume__head .body", { trigger: ".volume__head", start: "top 86%", stagger: 0.1 });
    gsap.from(".bar__val", { autoAlpha: 0, x: 10, duration: 0.7, ease: "power2.out", stagger: 0.06, clearProps: "transform,opacity,visibility", scrollTrigger: { trigger: ".bars", start: "top 80%", once: true } });
    gsap.from(".volume > .small", { autoAlpha: 0, duration: 1, clearProps: "opacity,visibility", scrollTrigger: { trigger: ".bars", start: "top 82%", once: true } });

    // Programs: the table deals its rows in
    rise(".programs__body .h2", { trigger: ".programs__body", start: "top 84%" });
    gsap.from(".plans thead tr, .plans tbody tr", { autoAlpha: 0, y: 14, duration: 0.8, ease: "power2.out", stagger: 0.09, clearProps: "transform,opacity,visibility", scrollTrigger: { trigger: ".plans", start: "top 86%", once: true } });
    rise(".programs__after", { trigger: ".programs__after", start: "top 92%", y: 18 });

    // Food: the phở wipes in from the right and settles
    rise(".food__copy .h2, .food__copy .body, .food__copy .small", { trigger: ".food__copy", start: "top 84%", stagger: 0.11 });
    var foodMedia = document.querySelector(".food__media");
    if (foodMedia) {
      gsap.fromTo(foodMedia, { clipPath: "inset(0% 0% 0% 100%)" }, { clipPath: "inset(0% 0% 0% 0%)", duration: 1.4, ease: "expo.out", scrollTrigger: { trigger: ".food", start: "top 76%", once: true } });
      gsap.from(".food__media img", { scale: 1.08, duration: 1.6, ease: "expo.out", clearProps: "transform", scrollTrigger: { trigger: ".food", start: "top 76%", once: true }, onComplete: function () { foodMedia.classList.add("mp-settled"); } });
    }

    // Yours: the two columns rise in turn
    rise(".yours__body .h2", { trigger: ".yours__body", start: "top 84%" });
    rise(".yours__cols > div", { trigger: ".yours__cols", start: "top 86%", stagger: 0.16, y: 30 });

    // Closing: the room darkens as you arrive, the big line wipes up
    gsap.fromTo(".close__shade", { opacity: 0.45 }, { opacity: 1, ease: "none", scrollTrigger: { trigger: ".close", start: "top bottom", end: "bottom bottom", scrub: true } });
    gsap.fromTo(".close__title", { clipPath: "inset(0% 0% 100% 0%)", y: 44 }, { clipPath: "inset(0% 0% 0% 0%)", y: 0, duration: 1.35, ease: "expo.out", clearProps: "transform", scrollTrigger: { trigger: ".close", start: "top 58%", once: true } });
    rise(".close__copy .body, .close__copy .actions, .close__copy .small", { trigger: ".close__copy", start: "top 74%", stagger: 0.13, y: 24 });

    // The closing APK button leans toward the cursor
    var magnet = document.querySelector(".close__copy .btn--plate");
    if (magnet && window.matchMedia("(hover: hover) and (pointer: fine)").matches) {
      magnet.classList.add("btn--magnet");
      var mX = gsap.quickTo(magnet, "x", { duration: 0.4, ease: "power3.out" });
      var mY = gsap.quickTo(magnet, "y", { duration: 0.4, ease: "power3.out" });
      magnet.addEventListener("pointermove", function (ev) {
        var r = magnet.getBoundingClientRect();
        mX(gsap.utils.clamp(-6, 6, (ev.clientX - r.left - r.width / 2) * 0.18));
        mY(gsap.utils.clamp(-5, 5, (ev.clientY - r.top - r.height / 2) * 0.3));
      });
      magnet.addEventListener("pointerleave", function () { mX(0); mY(0); });
    }

    // Footer: a quiet rise
    rise(".foot__grid > *", { trigger: ".foot", start: "top 95%", stagger: 0.1, y: 18, duration: 0.9 });
  }

  window.addEventListener("load", function () { ST.refresh(); });
})();

/* ---------- Forge: the barbell in 3D — the journey ----------
   A separate, self-gating layer on top of the page above. It loads
   three.js and the engine lazily, borrows the page's ScrollTrigger
   for progress, and only then adds .forge-on to <body>. The one
   canvas tours every stage on the page (see JOURNEY below) so the
   scroll reads as a single take, with film grain over the whole
   page and a hairline metering the journey. If anything is
   unavailable — ?forge=off, reduced motion, a low-end device, saved
   data, software WebGL — nothing here runs, no overlay is ever
   created, and the page stands exactly as it does without this
   block. */
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

  /* The forge chapter's pin is created NOW, at parse time — not in
     wire() after the engine's lazy boot. ScrollTrigger measures
     every trigger against the layout at its first refresh, and a
     pin that joins afterwards leaves every trigger below it (the
     rest pin, the chart, the reveals) measuring short by exactly
     this pin's distance; no later refresh corrects them. Created
     here — after the gates, before any refresh — its spacer is
     part of the layout all of them measure against. Its onUpdate
     simply no-ops until the engine exists. If the engine then
     fails to boot, the pin is killed again (see boot/onDead), so
     a page without the engine keeps no pin either. */
  var forgePinST = null;
  function killForgePin() {
    if (forgePinST) { forgePinST.kill(); forgePinST = null; }
    if (window.ScrollTrigger) window.ScrollTrigger.refresh();
  }
  if (window.gsap && window.ScrollTrigger) {
    var pinEarly = root.classList.contains("pin");
    forgePinST = window.ScrollTrigger.create({
      trigger: ".forge",
      start: pinEarly ? "top top" : "top bottom",
      end: pinEarly ? "+=140%" : "bottom top",
      pin: pinEarly ? ".forge__pin" : false,
      scrub: 0.6,
      anticipatePin: pinEarly ? 1 : 0,
      onUpdate: function (self) {
        if (window.GymForge) window.GymForge.setProgress("forge", self.progress);
      }
    });
  }

  var capEl = document.querySelector("[data-forge-cap]");
  var repsEl = document.querySelector("[data-forge-reps]");

  /* The journey: every stage the bar visits, in scroll order. The
     canvas lives in one at a time and is re-parented as the next
     takes over the viewport — one take, start to finish. Programs
     sits the tour out: its photograph is already full of racked
     barbells, so the motif rests there in print. */
  var JOURNEY = ["hero", "log", "ladder", "forge", "rest", "progress", "food", "yours", "closing"];

  /* Filmic unity, created only while the journey is live: a grain +
     vignette layer over the whole page, a black layer for the film
     cuts, and the plate-yellow hairline metering the whole scroll.
     If the engine never boots, none of these elements ever exist. */
  var filmEl = null, cutEl = null, progEl = null;
  function updateJourneyProgress() {
    if (!progEl) return;
    var max = document.documentElement.scrollHeight - window.innerHeight;
    var p = max > 0 ? window.scrollY / max : 0;
    progEl.style.transform = "scaleX(" + Math.max(0, Math.min(1, p)).toFixed(4) + ")";
  }
  function createOverlays() {
    if (filmEl) return;
    filmEl = document.createElement("div");
    filmEl.className = "journey-film";
    filmEl.setAttribute("aria-hidden", "true");
    cutEl = document.createElement("div");
    cutEl.className = "journey-cut";
    cutEl.setAttribute("aria-hidden", "true");
    progEl = document.createElement("div");
    progEl.className = "journey-progress";
    progEl.setAttribute("aria-hidden", "true");
    body.appendChild(filmEl);
    body.appendChild(cutEl);
    body.appendChild(progEl);
    body.classList.add("journey-on");
    updateJourneyProgress();
    window.addEventListener("scroll", updateJourneyProgress, { passive: true });
  }
  function removeOverlays() {
    [filmEl, cutEl, progEl].forEach(function (el) {
      if (el && el.parentElement) el.parentElement.removeChild(el);
    });
    filmEl = cutEl = progEl = null;
    body.classList.remove("journey-on");
    window.removeEventListener("scroll", updateJourneyProgress);
  }

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
          onDead: function () { body.classList.remove("forge-on"); removeOverlays(); killForgePin(); }
        });
        if (!ok) {
          if (canvas.parentElement) canvas.parentElement.removeChild(canvas);
          canvas = null;
          killForgePin();
          return;
        }
        createOverlays();
        wire();
      })
      .catch(function () { killForgePin(); /* engine unavailable: the page stands as-is */ });
  }

  function wire() {
    var F = window.GymForge;

    function applyMode(next) {
      if (!hosts[next]) return;
      if (mode !== next) {
        mode = next;
        hosts[next].appendChild(canvas);
        F.setMode(next);
        F.observeHost();
        /* the forge chapter is the still centre of the film: the
           pin holds it centred, so its seam drift rests at zero */
        if (next === "forge") F.setSceneProgress(0.5);
      }
      F.resize();
      renderReps();
    }

    /* Film cuts: at the seams where a travelling match would lie —
       into Rest and back out of it — the switch happens under a fast
       dip to black instead. The dip is triggered by the switch
       itself, so it always lands exactly on the pop it is hiding:
       black in ~140 ms, the canvas moves, black out ~160 ms. */
    var CUT_PAIRS = { "forge>rest": 1, "rest>forge": 1, "rest>progress": 1, "progress>rest": 1 };
    var cutting = false, lastCutAt = 0;
    function setMode(next) {
      if (!hosts[next]) return;
      if (next === mode) { F.resize(); renderReps(); return; }
      var pair = (mode || "") + ">" + next;
      if (cutEl && CUT_PAIRS[pair] && !cutting && performance.now() - lastCutAt > 700) {
        cutting = true;
        lastCutAt = performance.now();
        cutEl.style.opacity = "1";
        setTimeout(function () {
          applyMode(next);
          setTimeout(function () { cutEl.style.opacity = "0"; cutting = false; }, 160);
        }, 150);
      } else {
        applyMode(next);
      }
    }

    /* The canvas lives in whichever host is most on screen, with
       hysteresis: a challenger must clearly beat the incumbent, so
       the bar never flickers between two half-seen stages. */
    var ratios = {};
    JOURNEY.forEach(function (name) { ratios[name] = 0; });
    function pick() {
      var best = null, bestR = 0.22;
      JOURNEY.forEach(function (name) {
        if (hosts[name] && ratios[name] > bestR) { bestR = ratios[name]; best = name; }
      });
      if (best) {
        if (best !== mode && mode && hosts[mode] && ratios[mode] >= 0.15 && ratios[best] < ratios[mode] + 0.06) {
          F.setActive(true);
          return;
        }
        setMode(best);
        F.setActive(true);
      }
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
      /* The forge pin itself was already created at parse time
         (see forgePinST above), so its spacer is part of the layout
         every other trigger measures against. Only if gsap was
         somehow unavailable back then do we create it here. */
      if (!forgePinST) {
        var pinOK = root.classList.contains("pin");
        forgePinST = ST.create({
          trigger: ".forge",
          start: pinOK ? "top top" : "top bottom",
          end: pinOK ? "+=140%" : "bottom top",
          pin: pinOK ? ".forge__pin" : false,
          scrub: 0.6,
          anticipatePin: pinOK ? 1 : 0,
          onUpdate: function (self) { F.setProgress("forge", self.progress); }
        });
      }
      /* Every other host reports its own traversal through the
         viewport — the engine drifts the bar within the frame from
         it (the seam choreography), and the log push-in and the
         progress dolly are scrubbed by it directly. */
      JOURNEY.forEach(function (name) {
        if (!hosts[name] || name === "forge") return;
        ST.create({
          trigger: hosts[name], start: "top bottom", end: "bottom top", scrub: true,
          onUpdate: function (self) { if (mode === name) F.setSceneProgress(self.progress); }
        });
      });
      /* The ladder's load builds on the chapter's own pinned range:
         the same span the three bench sessions play over. */
      if (hosts.ladder) {
        ST.create({
          trigger: ".ladder", start: "top top",
          end: "+=" + Math.round(window.innerHeight * 2.4),
          scrub: true,
          onUpdate: function (self) { F.setProgress("ladder", self.progress); }
        });
      }
      /* Re-measure EVERY trigger individually, here and once more
         after load (this layer's listener registers after the
         page's own, so it runs last). The page's triggers were
         created before the forge pin existed, so they measured a
         layout without its spacer and sit a pin-distance too high
         — the rest pin would engage while the forge chapter is
         still on screen. A global ScrollTrigger.refresh() leaves
         those creation-time measurements in place (verified); a
         per-trigger refresh re-measures against the live layout
         and corrects them. */
      function remeasure() {
        ST.getAll().forEach(function (st) { try { st.refresh(); } catch (e) {} });
      }
      requestAnimationFrame(remeasure);
      if (document.readyState !== "complete") {
        window.addEventListener("load", remeasure);
      }
    }

    /* QA hooks for the journey, beside the forge ones below. */
    window.__journeyMode = function (name) {
      if (!hosts[name]) return null;
      JOURNEY.forEach(function (n) { ratios[n] = 0; });
      ratios[name] = 1;
      pick();
      return window.__forgeState ? window.__forgeState() : null;
    };
    window.__journeySceneP = function (p) {
      F.setSceneProgress(Math.max(0, Math.min(1, +p || 0)));
    };

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
      host: canvas && canvas.parentElement ? canvas.parentElement.getAttribute("data-forge-host") : null,
      reps: reps,
      ready: !!(window.GymForge && window.GymForge.isReady()),
      bend: window.GymForge && window.GymForge.getBend ? +window.GymForge.getBend().toFixed(3) : null,
      prog: window.GymForge && window.GymForge.getProgress ? +window.GymForge.getProgress().toFixed(3) : null,
      on: body.classList.contains("forge-on"),
      journey: body.classList.contains("journey-on")
    };
  };
  window.__forgeRep = function () { if (window.GymForge) window.GymForge.doRep(); };
  window.__forgeProgress = function (p) {
    if (window.GymForge) window.GymForge.setProgress("forge", Math.max(0, Math.min(1, +p || 0)));
  };
})();
