'use strict';
/* IronLog interactive prototype — vanilla JS, in-session state only.
   All people, weights, meals and charts below are fictional demo data. */

var PREV_SETS = [
  { kg: 60, reps: 8 },
  { kg: 60, reps: 8 },
  { kg: 60, reps: 7 }
];
var BASE_WEEKS = [8200, 9100, 8750, 9400];
var GOAL_KCAL = 2800;
var GOAL_PROTEIN = 160;
var MEAL_KCAL = 450;
var MEAL_PROTEIN = 32;
var REST_SECONDS = 90;
var SCREENS = ['home', 'routines', 'unavailable', 'workout', 'review', 'done', 'progress', 'nutrition', 'profile'];
var TAB_FOR = {
  home: 'home', nutrition: 'home', done: 'home',
  routines: 'routines', unavailable: 'routines', workout: 'routines', review: 'routines',
  progress: 'progress', profile: 'profile'
};

function initialState() {
  return {
    sets: PREV_SETS.map(function (p) { return { kg: p.kg, reps: p.reps, done: false }; }),
    rir: 2,
    workoutError: '',
    sessionSaved: false,
    savedVolume: 0,
    mealsAdded: 0,
    kcal: 1850,
    protein: 96,
    weekView: 'now',
    timer: { visible: false, running: false, remaining: REST_SECONDS, total: REST_SECONDS }
  };
}

var state = initialState();
var timerId = null;

function fmt(n) { return n.toLocaleString('vi-VN'); }
function fmtKg(n) { return n.toLocaleString('vi-VN', { maximumFractionDigits: 1 }); }
function mmss(s) {
  var m = Math.floor(s / 60);
  var r = s % 60;
  return (m < 10 ? '0' : '') + m + ':' + (r < 10 ? '0' : '') + r;
}
function doneSets() { return state.sets.filter(function (s) { return s.done; }); }
function totalVolume() {
  return doneSets().reduce(function (acc, s) { return acc + s.kg * s.reps; }, 0);
}
function topSet() {
  var done = doneSets();
  if (!done.length) return null;
  return done.reduce(function (best, s) {
    return (s.kg > best.kg || (s.kg === best.kg && s.reps > best.reps)) ? s : best;
  }, done[0]);
}
function suggestion() {
  var done = doneSets();
  var allDone = done.length === state.sets.length;
  var allReps = allDone && done.every(function (s) { return s.reps >= 8; });
  var rirOk = state.rir === 1 || state.rir === 2;
  return (allReps && rirOk)
    ? { plus: true, text: '+2,5 kg buổi tới' }
    : { plus: false, text: 'Giữ nguyên mức tạ' };
}
function weekVolumes() {
  var w = BASE_WEEKS.slice();
  if (state.sessionSaved) w[w.length - 1] += state.savedVolume;
  return w;
}

/* ---------------- rest timer ---------------- */
function ensureTicker() {
  if (timerId !== null) return;
  timerId = setInterval(function () {
    if (!state.timer.running) return;
    if (state.timer.remaining > 0) state.timer.remaining -= 1;
    if (state.timer.remaining === 0) state.timer.running = false;
    updateTimerDom();
  }, 1000);
}
function startRest() {
  state.timer.visible = true;
  state.timer.remaining = REST_SECONDS;
  state.timer.total = REST_SECONDS;
  state.timer.running = true;
  ensureTicker();
}
function updateTimerDom() {
  var root = document.getElementById('screen');
  if (!root) return;
  var time = root.querySelector('[data-timer-time]');
  if (!time) return;
  time.textContent = mmss(state.timer.remaining);
  var fill = root.querySelector('[data-timer-fill]');
  if (fill) fill.style.width = (state.timer.total ? (state.timer.remaining / state.timer.total) * 100 : 0) + '%';
  var label = root.querySelector('[data-timer-label]');
  if (label) label.textContent = state.timer.remaining === 0 ? 'Hết giờ nghỉ' : (state.timer.running ? 'Đang nghỉ' : 'Đã tạm dừng');
  var pause = root.querySelector('[data-action="pause-timer"]');
  if (pause) pause.textContent = state.timer.running ? 'Tạm dừng' : 'Tiếp tục';
}
function timerHtml() {
  if (!state.timer.visible) return '';
  return '<div class="timer" role="timer" aria-label="Đồng hồ nghỉ">' +
    '<div class="timer__row"><span data-timer-label>' + (state.timer.remaining === 0 ? 'Hết giờ nghỉ' : (state.timer.running ? 'Đang nghỉ' : 'Đã tạm dừng')) + '</span>' +
    '<span class="timer__time num" data-timer-time>' + mmss(state.timer.remaining) + '</span></div>' +
    '<span class="bar"><span class="bar__fill" data-timer-fill style="width:' + (state.timer.total ? (state.timer.remaining / state.timer.total) * 100 : 0) + '%"></span></span>' +
    '<div class="timer__btns">' +
    '<button type="button" data-action="pause-timer" aria-label="Tạm dừng hoặc tiếp tục đồng hồ nghỉ">' + (state.timer.running ? 'Tạm dừng' : 'Tiếp tục') + '</button>' +
    '<button type="button" data-action="add30" aria-label="Cộng thêm 30 giây nghỉ">+30 giây</button>' +
    '<button type="button" data-action="skip-timer" aria-label="Bỏ qua thời gian nghỉ còn lại">Bỏ qua</button>' +
    '</div></div>';
}

/* ---------------- shared bits ---------------- */
function barRow(label, pct, valueText, low) {
  return '<div class="bar-row"><span>' + label + '</span>' +
    '<span class="bar"><span class="bar__fill' + (low ? ' bar__fill--low' : '') + '" style="width:' + pct + '%"></span></span>' +
    '<span class="num">' + valueText + '</span></div>';
}
function recentSessionsHtml() {
  var items = '';
  if (state.sessionSaved) {
    items += '<li><span><strong>Push Day A</strong> · Bench Press</span><span class="when">Hôm nay · ' + fmt(state.savedVolume) + ' kg</span></li>';
  }
  items += '<li><span><strong>Pull Day A</strong> · Lưng và tay trước</span><span class="when">Hôm qua · 5.980 kg</span></li>' +
    '<li><span><strong>Leg Day</strong> · Chân</span><span class="when">3 ngày trước · 7.420 kg</span></li>';
  return items;
}

/* ---------------- screens ---------------- */
function screenHome() {
  var kcalPct = Math.min(100, Math.round((state.kcal / GOAL_KCAL) * 100));
  var savedCard = state.sessionSaved
    ? '<div class="card card--volt"><p class="card__label">Vừa lưu</p><p class="card__title">Push Day A hoàn thành</p><p class="card__sub">' + fmt(state.savedVolume) + ' kg volume đã cộng vào tuần này. Xem ở tab Tiến bộ.</p></div>'
    : '';
  return '<p class="scr__hi">Chào buổi sáng, Hoàng</p>' +
    '<h2 class="scr__title">Hôm nay tập gì?</h2>' +
    '<div class="streak"><span class="num">12</span><span>ngày liên tiếp<br>có buổi tập được ghi</span></div>' +
    savedCard +
    '<div class="card card--volt"><p class="card__label">Buổi hôm nay · PPL</p>' +
    '<p class="card__title">Push Day A</p><p class="card__sub">Ngực và vai trước · 5 bài · 18 hiệp</p>' +
    '<a class="btn btn--secondary card__cta" href="#workout" aria-label="Bắt đầu buổi tập Push Day A">Bắt đầu buổi tập</a></div>' +
    '<h3 class="scr__title" style="font-size:var(--text-lg)">Phục hồi nhóm cơ</h3>' +
    barRow('Ngực', 92, '92%') + barRow('Vai', 84, '84%') + barRow('Lưng', 67, '67%') + barRow('Chân', 38, '38%', true) +
    '<p class="scr__sub">Chân mới tập hôm qua — hôm nay để chân nghỉ là hợp lý.</p>' +
    '<a class="card" href="#nutrition" aria-label="Mở màn hình dinh dưỡng"><p class="card__label">Dinh dưỡng hôm nay</p>' +
    '<p class="card__title num">' + fmt(state.kcal) + ' / ' + fmt(GOAL_KCAL) + ' kcal</p>' +
    '<span class="bar" style="margin-top:var(--space-sm)"><span class="bar__fill" style="width:' + kcalPct + '%"></span></span>' +
    '<p class="card__sub">Đạm: ' + fmt(state.protein) + ' / ' + fmt(GOAL_PROTEIN) + ' g · chạm để xem bữa ăn</p></a>' +
    '<h3 class="scr__title" style="font-size:var(--text-lg)">Buổi gần đây</h3>' +
    '<ul class="list">' + recentSessionsHtml() + '</ul>';
}

function screenRoutines() {
  return '<p class="scr__hi">Giáo án</p><h2 class="scr__title">Chọn buổi tập</h2>' +
    '<div class="card card--volt"><p class="card__label">Đang theo · 6 ngày / tuần</p>' +
    '<p class="card__title">PPL Hypertrophy</p><p class="card__sub">Push · Pull · Legs, lặp lại hai vòng mỗi tuần</p></div>' +
    '<a class="card" href="#workout" aria-label="Bắt đầu Push Day A"><p class="card__title">Push Day A</p>' +
    '<p class="card__sub">Ngực và vai trước · 5 bài · 18 hiệp · buổi mẫu của demo</p><p class="card__cta">Bắt đầu →</p></a>' +
    '<div class="card"><p class="card__title">Upper / Lower</p><p class="card__sub">4 ngày / tuần · 60–70 phút mỗi buổi</p></div>' +
    '<div class="card"><p class="card__title">Arnold Split</p><p class="card__sub">6 ngày / tuần · 65–75 phút mỗi buổi</p></div>' +
    '<div class="card"><p class="card__title">Full body</p><p class="card__sub">3 ngày / tuần · 65–75 phút mỗi buổi</p></div>' +
    '<a class="card" href="#unavailable" aria-label="Mở giáo án tùy chỉnh, chưa có trong bản demo"><p class="card__title">Giáo án tùy chỉnh<span class="badge badge--lock">Khoá trong demo</span></p>' +
    '<p class="card__sub">Tự sắp xếp bài và hiệp theo ý bạn</p></a>';
}

function screenUnavailable() {
  return '<p class="scr__hi">Giáo án</p><h2 class="scr__title">Chưa mở trong bản demo</h2>' +
    '<div class="lowdata"><p style="font-size:2rem" aria-hidden="true">🔒</p>' +
    '<p>Trình soạn giáo án tùy chỉnh là một phần của ứng dụng Android. Bản prototype trên trình duyệt chỉ chạy một buổi mẫu cố định để bạn thử dòng ghi hiệp.</p>' +
    '<a class="btn btn--primary" href="#workout">Chọn Push Day A thay thế</a>' +
    '<a class="btn btn--secondary" href="#routines">Quay lại danh sách giáo án</a></div>';
}

function setHtml(s, i) {
  var warn = s.kg > 200 ? 'Mức tạ trên 200 kg — kiểm tra lại số đã nhập.' : '';
  return '<div class="set' + (s.done ? ' is-done' : '') + '">' +
    '<div class="set__head"><span class="set__name">Hiệp ' + (i + 1) + '</span>' +
    '<span class="set__prev">Buổi trước: ' + PREV_SETS[i].reps + ' × ' + fmtKg(PREV_SETS[i].kg) + ' kg</span></div>' +
    '<div class="field-row">' +
    '<div class="field"><label for="kg-' + i + '">Kg</label><div class="stepper">' +
    '<button type="button" data-step="kg" data-dir="-1" data-set="' + i + '" aria-label="Giảm kg hiệp ' + (i + 1) + '">−</button>' +
    '<input id="kg-' + i + '" class="set-input" data-field="kg" data-set="' + i + '" type="number" inputmode="decimal" min="0" step="2.5" value="' + s.kg + '" aria-label="Số kg hiệp ' + (i + 1) + '">' +
    '<button type="button" data-step="kg" data-dir="1" data-set="' + i + '" aria-label="Tăng kg hiệp ' + (i + 1) + '">+</button></div></div>' +
    '<div class="field"><label for="reps-' + i + '">Rep</label><div class="stepper">' +
    '<button type="button" data-step="reps" data-dir="-1" data-set="' + i + '" aria-label="Giảm rep hiệp ' + (i + 1) + '">−</button>' +
    '<input id="reps-' + i + '" class="set-input" data-field="reps" data-set="' + i + '" type="number" inputmode="numeric" min="0" step="1" value="' + s.reps + '" aria-label="Số rep hiệp ' + (i + 1) + '">' +
    '<button type="button" data-step="reps" data-dir="1" data-set="' + i + '" aria-label="Tăng rep hiệp ' + (i + 1) + '">+</button></div></div>' +
    '<button type="button" class="done-toggle" data-toggle="' + i + '" aria-pressed="' + s.done + '" aria-label="Đánh dấu hiệp ' + (i + 1) + ' đã xong">' + (s.done ? 'Đã xong ✓' : 'Xong') + '</button>' +
    '</div>' +
    '<p class="warn" data-warn-for="' + i + '"' + (warn ? '' : ' hidden') + '>' + warn + '</p>' +
    '</div>';
}

function screenWorkout() {
  var err = state.workoutError ? '<p class="error" role="alert">' + state.workoutError + '</p>' : '';
  return '<p class="scr__hi">Đang tập · Push Day A</p><h2 class="scr__title">Bench Press</h2>' +
    '<p class="scr__sub">Đẩy ngực ngang đòn tạ · buổi trước: 8 × 60 · 8 × 60 · 7 × 60</p>' +
    err + timerHtml() +
    '<div class="sets">' + state.sets.map(setHtml).join('') + '</div>' +
    '<div class="total-bar"><span>Tổng volume<br><span class="num" data-live-volume>' + fmt(totalVolume()) + ' kg</span></span>' +
    '<button type="button" class="btn btn--primary" data-action="review" aria-label="Xem lại buổi tập">Xem lại</button></div>';
}

function screenReview() {
  var done = doneSets();
  var top = topSet();
  var sug = suggestion();
  var rows = state.sets.map(function (s, i) {
    return '<tr><td class="num">' + (i + 1) + '</td><td class="num">' + (s.done ? fmtKg(s.kg) : '—') + '</td>' +
      '<td class="num">' + (s.done ? s.reps : '—') + '</td><td>' + (s.done ? 'Đã ghi' : 'Chưa ghi') + '</td></tr>';
  }).join('');
  var chips = [0, 1, 2, 3].map(function (r) {
    return '<button type="button" class="chip" data-rir="' + r + '" aria-pressed="' + (state.rir === r) + '" aria-label="Chọn RIR ' + r + '">' + r + '</button>';
  }).join('');
  return '<p class="scr__hi">Xem lại · Push Day A</p><h2 class="scr__title">Kiểm tra trước khi lưu</h2>' +
    '<table class="sum"><thead><tr><th scope="col">Hiệp</th><th scope="col">kg</th><th scope="col">Rep</th><th scope="col">Trạng thái</th></tr></thead><tbody>' + rows + '</tbody></table>' +
    '<div><div class="kv"><span>Hiệp đã ghi</span><strong class="num">' + done.length + ' / ' + state.sets.length + '</strong></div>' +
    '<div class="kv"><span>Tổng volume</span><strong class="num">' + fmt(totalVolume()) + ' kg</strong></div>' +
    '<div class="kv"><span>Hiệp nặng nhất</span><strong class="num">' + (top ? fmtKg(top.kg) + ' kg × ' + top.reps + ' rep' : '—') + '</strong></div></div>' +
    '<div><p class="scr__sub" id="rir-label">RIR của hiệp cuối — còn đẩy thêm được mấy rep?</p>' +
    '<div class="chips" role="group" aria-labelledby="rir-label" style="margin-top:var(--space-xs)">' + chips + '</div></div>' +
    '<div class="suggest"><p class="scr__sub">Gợi ý buổi tới</p><p class="num">' + sug.text + '</p>' +
    '<p class="scr__sub">' + (sug.plus
      ? 'Cả 3 hiệp đều đạt ít nhất 8 rep và RIR đang ở 1–2, nên mức tạ có thể nhích lên một bước nhỏ nhất.'
      : 'Chưa đủ điều kiện tăng tạ: cần ghi đủ 3 hiệp, mỗi hiệp ít nhất 8 rep và RIR ở mức 1–2.') + '</p></div>' +
    '<div class="btn-row"><a class="btn btn--secondary" href="#workout">Quay lại</a>' +
    '<button type="button" class="btn btn--primary" data-action="confirm" aria-label="Xác nhận và lưu buổi tập">Xác nhận</button></div>';
}

function screenDone() {
  return '<div class="center" style="margin-top:var(--space-lg)"><div class="done-check" aria-hidden="true">✓</div></div>' +
    '<h2 class="scr__title center">Buổi tập đã lưu</h2>' +
    '<p class="scr__sub center">Push Day A · Bench Press — bản mô phỏng, không ghi dữ liệu thật.</p>' +
    '<div><div class="kv"><span>Hiệp đã ghi</span><strong class="num">' + doneSets().length + ' / ' + state.sets.length + '</strong></div>' +
    '<div class="kv"><span>Tổng volume</span><strong class="num">' + fmt(state.savedVolume) + ' kg</strong></div>' +
    '<div class="kv"><span>Gợi ý buổi tới</span><strong>' + suggestion().text + '</strong></div>' +
    '<div class="kv"><span>Mã tham chiếu</span><strong class="num">IRONLOG-DEMO</strong></div></div>' +
    '<a class="btn btn--primary" href="#home">Về tổng quan</a>' +
    '<a class="btn btn--secondary" href="#progress">Xem tiến bộ tuần này</a>';
}

function screenProgress() {
  if (state.weekView === 'old') {
    return '<p class="scr__hi">Tiến bộ</p><h2 class="scr__title">Khối lượng theo tuần</h2>' + weekSegHtml() +
      '<div class="lowdata"><p><strong>Chưa đủ dữ liệu…</strong></p>' +
      '<p>Tuần 12–18/8 chỉ có một buổi được ghi. IronLog không vẽ biểu đồ và không đưa nhận xét khi dữ liệu chưa đủ — quay lại tuần này để xem bức tranh đầy đủ.</p>' +
      '<button type="button" class="btn btn--secondary btn--small" data-week="now" aria-label="Quay lại tuần này">Quay lại tuần này</button></div>';
  }
  var weeks = weekVolumes();
  var max = Math.max.apply(null, weeks);
  var labels = ['Tuần -3', 'Tuần -2', 'Tuần -1', 'Tuần này'];
  var cols = weeks.map(function (v, i) {
    var h = Math.round((v / max) * 100);
    return '<div class="chart__col"><span class="num">' + fmt(v) + '</span>' +
      '<div class="chart__bar' + (i === weeks.length - 1 ? ' chart__bar--now' : '') + '" style="height:' + h + '%"></div>' +
      '<span class="wk">' + labels[i] + '</span></div>';
  }).join('');
  var chest = 14 + (state.sessionSaved ? 3 : 0);
  return '<p class="scr__hi">Tiến bộ</p><h2 class="scr__title">Khối lượng theo tuần</h2>' + weekSegHtml() +
    '<div class="chart" role="img" aria-label="Biểu đồ volume 4 tuần: ' + weeks.map(fmt).join(', ') + ' kg">' + cols + '</div>' +
    '<p class="scr__sub">Volume tính bằng tổng kg × rep của mọi hiệp đã ghi trong tuần.</p>' +
    '<h3 class="scr__title" style="font-size:var(--text-lg)">Hiệp theo nhóm cơ · so với MEV–MRV</h3>' +
    muscleRow('Ngực', chest, 8, 20) + muscleRow('Lưng', 12, 8, 20) + muscleRow('Chân', 10, 6, 18) + muscleRow('Vai', 9, 6, 16) +
    '<p class="scr__sub">MEV là số hiệp tối thiểu để còn tiến bộ, MRV là mức tối đa cơ thể phục hồi nổi trong tuần.</p>';
}
function weekSegHtml() {
  return '<div class="seg" role="group" aria-label="Chọn tuần">' +
    '<button type="button" data-week="now" aria-pressed="' + (state.weekView === 'now') + '">Tuần này</button>' +
    '<button type="button" data-week="old" aria-pressed="' + (state.weekView === 'old') + '">Tuần 12–18/8</button></div>';
}
function muscleRow(name, sets, mev, mrv) {
  var pct = Math.min(100, Math.round((sets / mrv) * 100));
  return '<div class="bar-row" style="grid-template-columns:4.6rem minmax(0,1fr) auto"><span>' + name + ' · ' + sets + '</span>' +
    '<span class="bar"><span class="bar__fill" style="width:' + pct + '%"></span></span>' +
    '<span class="num">MEV ' + mev + ' · MRV ' + mrv + '</span></div>';
}

function screenNutrition() {
  var kcalPct = Math.min(100, Math.round((state.kcal / GOAL_KCAL) * 100));
  var proPct = Math.min(100, Math.round((state.protein / GOAL_PROTEIN) * 100));
  var meals = [
    ['Cơm trắng', 130, '2,7'], ['Ức gà', 165, '31'], ['Phở bò', 85, '5,5'], ['Cơm tấm sườn', 163, '7,5']
  ].map(function (m) {
    return '<tr><td>' + m[0] + '</td><td class="num">' + m[1] + '</td><td class="num">' + m[2] + '</td></tr>';
  }).join('');
  var added = state.mealsAdded
    ? '<p class="scr__sub">Đã thêm ' + state.mealsAdded + ' bữa mẫu trong phiên demo này.</p>' : '';
  return '<p class="scr__hi">Dinh dưỡng</p><h2 class="scr__title">Hôm nay ăn gì?</h2>' +
    '<div><div class="kv"><span>Năng lượng</span><strong class="num">' + fmt(state.kcal) + ' / ' + fmt(GOAL_KCAL) + ' kcal</strong></div>' +
    '<span class="bar"><span class="bar__fill" style="width:' + kcalPct + '%"></span></span></div>' +
    '<div><div class="kv"><span>Đạm</span><strong class="num">' + fmt(state.protein) + ' / ' + fmt(GOAL_PROTEIN) + ' g</strong></div>' +
    '<span class="bar"><span class="bar__fill" style="width:' + proPct + '%"></span></span></div>' +
    '<button type="button" class="btn btn--primary" data-action="add-meal" aria-label="Thêm bữa mẫu cộng 450 kcal và 32 gam đạm">Thêm bữa mẫu +450 kcal</button>' +
    '<p class="scr__sub">Bữa mẫu: cơm tấm + ức gà, cộng ' + MEAL_KCAL + ' kcal và ' + MEAL_PROTEIN + ' g đạm.</p>' + added +
    '<h3 class="scr__title" style="font-size:var(--text-lg)">Món Việt trên 100 g</h3>' +
    '<table class="sum"><thead><tr><th scope="col">Món</th><th scope="col">kcal</th><th scope="col">Đạm, g</th></tr></thead><tbody>' + meals + '</tbody></table>' +
    '<p class="scr__sub">Trong ứng dụng thật, bảng có gần 90 món và món lạ có thể được Gemini ước lượng từ ảnh trước khi bạn sửa lại.</p>' +
    '<a class="btn btn--secondary" href="#home">Quay lại Home</a>';
}

function screenProfile() {
  return '<p class="scr__hi">Hồ sơ</p><h2 class="scr__title">Người dùng demo</h2>' +
    '<p class="scr__sub">Tài khoản hư cấu dùng riêng cho prototype trên trình duyệt.</p>' +
    '<div><div class="kv"><span>Chuỗi ngày tập</span><strong class="num">12 ngày</strong></div>' +
    '<div class="kv"><span>Hiệp Bench Press đã ghi (phiên này)</span><strong class="num">' + doneSets().length + ' / ' + state.sets.length + '</strong></div>' +
    '<div class="kv"><span>Volume buổi đang tập</span><strong class="num">' + fmt(totalVolume()) + ' kg</strong></div>' +
    '<div class="kv"><span>Buổi Push Day A đã lưu</span><strong>' + (state.sessionSaved ? 'Rồi' : 'Chưa') + '</strong></div>' +
    '<div class="kv"><span>Bữa mẫu đã thêm</span><strong class="num">' + state.mealsAdded + '</strong></div>' +
    '<div class="kv"><span>Năng lượng hôm nay</span><strong class="num">' + fmt(state.kcal) + ' kcal</strong></div></div>' +
    '<button type="button" class="btn btn--primary" data-action="reset" aria-label="Đặt lại toàn bộ dữ liệu demo">Reset demo</button>' +
    '<p class="scr__sub">Reset đưa mọi hiệp, đồng hồ nghỉ, bữa ăn và tiến bộ về đúng trạng thái ban đầu.</p>' +
    '<a class="btn btn--secondary" href="index.html">← Quay lại trang giới thiệu</a>';
}

var RENDERERS = {
  home: screenHome, routines: screenRoutines, unavailable: screenUnavailable,
  workout: screenWorkout, review: screenReview, done: screenDone,
  progress: screenProgress, nutrition: screenNutrition, profile: screenProfile
};

function currentScreen() {
  var h = (location.hash || '#home').slice(1);
  return SCREENS.indexOf(h) >= 0 ? h : 'home';
}

function render() {
  var name = currentScreen();
  var root = document.getElementById('screen');
  root.innerHTML = RENDERERS[name]();
  var tabs = document.querySelectorAll('#tabbar [data-tab]');
  tabs.forEach(function (a) {
    if (a.getAttribute('data-tab') === TAB_FOR[name]) a.classList.add('is-on');
    else a.classList.remove('is-on');
  });
  root.scrollTop = 0;
  if (name === 'workout') updateTimerDom();
}

/* ---------------- events ---------------- */
function updateLiveBits(i) {
  var root = document.getElementById('screen');
  var vol = root.querySelector('[data-live-volume]');
  if (vol) vol.textContent = fmt(totalVolume()) + ' kg';
  if (typeof i === 'number') {
    var warn = root.querySelector('[data-warn-for="' + i + '"]');
    if (warn) {
      var over = state.sets[i].kg > 200;
      warn.hidden = !over;
      warn.textContent = over ? 'Mức tạ trên 200 kg — kiểm tra lại số đã nhập.' : '';
    }
  }
}

document.getElementById('screen').addEventListener('input', function (ev) {
  var el = ev.target;
  if (!el.classList || !el.classList.contains('set-input')) return;
  var i = Number(el.getAttribute('data-set'));
  var field = el.getAttribute('data-field');
  var v = field === 'kg' ? parseFloat(el.value) : parseInt(el.value, 10);
  state.sets[i][field] = isNaN(v) ? 0 : Math.max(0, v);
  updateLiveBits(i);
});

document.getElementById('screen').addEventListener('click', function (ev) {
  var stepBtn = ev.target.closest('[data-step]');
  if (stepBtn) {
    var si = Number(stepBtn.getAttribute('data-set'));
    var field = stepBtn.getAttribute('data-step');
    var dir = Number(stepBtn.getAttribute('data-dir'));
    var step = field === 'kg' ? 2.5 : 1;
    var next = state.sets[si][field] + dir * step;
    state.sets[si][field] = Math.max(0, Math.round(next * 10) / 10);
    render();
    return;
  }
  var toggle = ev.target.closest('[data-toggle]');
  if (toggle) {
    var ti = Number(toggle.getAttribute('data-toggle'));
    var s = state.sets[ti];
    if (!s.done && (s.reps <= 0 || s.kg <= 0)) {
      state.workoutError = 'Hiệp ' + (ti + 1) + ' cần số rep và kg lớn hơn 0 trước khi đánh dấu xong.';
      render();
      return;
    }
    s.done = !s.done;
    state.workoutError = '';
    if (s.done) startRest();
    render();
    return;
  }
  var rirBtn = ev.target.closest('[data-rir]');
  if (rirBtn) {
    state.rir = Number(rirBtn.getAttribute('data-rir'));
    render();
    return;
  }
  var weekBtn = ev.target.closest('[data-week]');
  if (weekBtn) {
    state.weekView = weekBtn.getAttribute('data-week');
    render();
    return;
  }
  var act = ev.target.closest('[data-action]');
  if (!act) return;
  var action = act.getAttribute('data-action');
  if (action === 'pause-timer') {
    if (state.timer.remaining > 0) state.timer.running = !state.timer.running;
    ensureTicker();
    updateTimerDom();
  } else if (action === 'add30') {
    state.timer.remaining += 30;
    state.timer.total += 30;
    updateTimerDom();
  } else if (action === 'skip-timer') {
    state.timer.visible = false;
    state.timer.running = false;
    render();
  } else if (action === 'review') {
    var done = doneSets();
    if (done.length === 0) {
      state.workoutError = 'Chưa ghi hiệp nào. Ghi ít nhất một hiệp để tiếp tục.';
      render();
      return;
    }
    var bad = done.some(function (x) { return x.reps <= 0 || x.kg <= 0; });
    if (bad) {
      state.workoutError = 'Có hiệp đã đánh dấu xong nhưng số rep hoặc kg đang bằng 0. Kiểm tra lại trước khi tiếp tục.';
      render();
      return;
    }
    state.workoutError = '';
    location.hash = '#review';
  } else if (action === 'confirm') {
    if (!state.sessionSaved) {
      state.sessionSaved = true;
      state.savedVolume = totalVolume();
    }
    location.hash = '#done';
  } else if (action === 'add-meal') {
    state.mealsAdded += 1;
    state.kcal += MEAL_KCAL;
    state.protein += MEAL_PROTEIN;
    render();
  } else if (action === 'reset') {
    state = initialState();
    if (timerId !== null) { clearInterval(timerId); timerId = null; }
    if (currentScreen() === 'profile') render();
    else location.hash = '#home';
  }
});

window.addEventListener('hashchange', render);
render();
