'use strict';
/* IronLog interactive prototype — mirrors the Android app's flow and labels.
   Vanilla JS, in-session state only. All people, weights, meals and charts
   below are fictional demo data, consistent with the app's demo content. */

var GOAL_KCAL = 2800;
var GOAL_PROTEIN = 160;
var MEAL_KCAL = 450;
var MEAL_PROTEIN = 32;
var TARGET_SETS = 3;
var TARGET_REPS = 8;
var TARGET_KG = 60;
var PREV_BEST_E1RM = 60 * (1 + 8 / 30); /* previous best set: 60 x 8 -> 76,0 kg e1RM */
var BASE_E1RM = [76.0, 78.5, 80.0];
var BASE_WEEK_VOLUME = 9400;
var MUSCLES_NOW = [
  { name: 'Ngực', sets: 14, mrv: 20 },
  { name: 'Vai', sets: 9, mrv: 20 },
  { name: 'Tay sau', sets: 8, mrv: 20 },
  { name: 'Lưng', sets: 12, mrv: 20 },
  { name: 'Chân', sets: 10, mrv: 18 }
];
var MUSCLES_OLD = [
  { name: 'Ngực', sets: 11, mrv: 20 },
  { name: 'Vai', sets: 8, mrv: 20 },
  { name: 'Tay sau', sets: 6, mrv: 20 },
  { name: 'Lưng', sets: 10, mrv: 20 },
  { name: 'Chân', sets: 9, mrv: 18 }
];
var SCREENS = ['home', 'routines', 'unavailable', 'workout', 'review', 'done', 'progress', 'nutrition', 'profile'];
var TAB_FOR = {
  home: 'home', nutrition: 'home', done: 'home',
  routines: 'routines', unavailable: 'routines',
  workout: 'workout', review: 'workout',
  progress: 'progress', profile: 'profile'
};
var TITLES = {
  home: 'Hôm Nay', routines: 'Giáo Án & Kho Bài', unavailable: 'Giáo Án & Kho Bài',
  workout: 'Hôm Nay', review: 'Hôm Nay', done: 'Hôm Nay',
  progress: 'Tiến Bộ', nutrition: 'Dinh Dưỡng', profile: 'Cá Nhân'
};

function initialMeals() {
  return [
    { name: 'Cơm tấm sườn', meta: '1 phần · 520 kcal · Đạm 24 g' },
    { name: 'Ức gà + cơm trắng', meta: '150 g ức gà · 380 kcal · Đạm 46 g' }
  ];
}

function initialState() {
  return {
    logged: [],                    /* [{kg, reps, rir}] — Bench Press demo sets */
    draft: { kg: TARGET_KG, reps: TARGET_REPS },
    rir: 2,
    workoutError: '',
    sessionSaved: false,
    savedVolume: 0,
    savedSets: 0,
    savedBest: null,               /* {kg, reps, rir} */
    savedSuggestion: null,         /* {plus, headline, reason} */
    meals: initialMeals(),
    mealsAdded: 0,
    kcal: 1400,
    protein: 88,
    weekView: 'now',
    restSeconds: 90,
    toggles: { sound: true, haptic: true },
    timer: { visible: false, running: false, finished: false, remaining: 0, total: 0 }
  };
}

var state = initialState();
var timerId = null;

/* ---------------- formatting ---------------- */
function fmt(n) { return n.toLocaleString('vi-VN'); }
function fmtKg(n) { return n.toLocaleString('vi-VN', { maximumFractionDigits: 1 }); }
function fmt1(n) { return n.toLocaleString('vi-VN', { minimumFractionDigits: 1, maximumFractionDigits: 1 }); }
function fmtK(v) { return (v / 1000).toLocaleString('vi-VN', { maximumFractionDigits: 1 }) + 'k'; }
function mmss(s) {
  var m = Math.floor(s / 60);
  var r = s % 60;
  return (m < 10 ? '0' : '') + m + ':' + (r < 10 ? '0' : '') + r;
}
function e1rm(s) { return s.kg * (1 + s.reps / 30); }
function isPR(s) { return e1rm(s) > PREV_BEST_E1RM + 0.001; }
function esc(s) { return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;'); }

/* ---------------- derived state ---------------- */
function totalVolume() {
  return state.logged.reduce(function (acc, s) { return acc + s.kg * s.reps; }, 0);
}
function bestSet() {
  if (!state.logged.length) return null;
  return state.logged.reduce(function (best, s) { return e1rm(s) > e1rm(best) ? s : best; }, state.logged[0]);
}
function computeSuggestion() {
  var logged = state.logged;
  var topKg = logged.length ? Math.max.apply(null, logged.map(function (s) { return s.kg; })) : TARGET_KG;
  if (logged.length >= TARGET_SETS && logged.every(function (s) { return s.reps >= TARGET_REPS; }) &&
      logged.every(function (s) { return s.rir <= 2; })) {
    return {
      plus: true,
      headline: 'TĂNG TẠ — buổi tới: ' + fmtKg(topKg + 2.5) + ' × 8 reps @ RIR 2',
      reason: 'Đủ ' + TARGET_SETS + ' hiệp mục tiêu, mọi hiệp đều đạt 8 rep ở RIR ≤ 2 — đúng chuẩn Progressive Overload của ứng dụng.'
    };
  }
  var reason;
  if (logged.length < TARGET_SETS) {
    reason = 'Mới ghi ' + logged.length + '/' + TARGET_SETS + ' hiệp mục tiêu — cần ghi đủ ' + TARGET_SETS + ' hiệp để xét tăng tạ.';
  } else if (!logged.every(function (s) { return s.reps >= TARGET_REPS; })) {
    reason = 'Có hiệp chưa đạt 8 rep — đạt trần rep ở mức tạ hiện tại trước, rồi mới tăng tạ.';
  } else {
    reason = 'Có hiệp ở RIR trên 2 — giữ tạ để các hiệp tới đạt rep mục tiêu ở vùng RIR 1–2.';
  }
  return {
    plus: false,
    headline: 'Giữ nguyên mức tạ — buổi tới: ' + fmtKg(topKg) + ' × 8 reps @ RIR 2',
    reason: reason
  };
}
function weekVolume() { return BASE_WEEK_VOLUME + (state.sessionSaved ? state.savedVolume : 0); }

/* ---------------- rest timer ---------------- */
function ensureTicker() {
  if (timerId !== null) return;
  timerId = setInterval(function () {
    if (!state.timer.running) return;
    if (state.timer.remaining > 0) state.timer.remaining -= 1;
    if (state.timer.remaining === 0) { state.timer.running = false; state.timer.finished = true; }
    updateTimerDom();
  }, 1000);
}
function startRest() {
  state.timer.visible = true;
  state.timer.finished = false;
  state.timer.remaining = state.restSeconds;
  state.timer.total = state.restSeconds;
  state.timer.running = true;
  ensureTicker();
}
function restbarHtml() {
  var t = state.timer;
  if (!t.visible) return '';
  var label = t.finished ? 'HẾT GIỜ NGHỈ — VÀO HIỆP TIẾP' : 'NGHỈ GIỮA HIỆP (CHẠY CẢ KHI TẮT MÀN)';
  var pct = t.total ? Math.round((t.remaining / t.total) * 100) : 0;
  return '<div class="restbar' + (t.finished ? ' is-finished' : '') + '" role="timer" aria-label="Đồng hồ nghỉ giữa hiệp">' +
    '<div class="restbar__row"><span class="restbar__label" data-timer-label>' + label + '</span>' +
    '<span class="restbar__time" data-timer-time>' + mmss(t.remaining) + '</span></div>' +
    '<span class="bar"><span class="bar__fill" data-timer-fill style="width:' + pct + '%"></span></span>' +
    '<div class="restbar__btns">' +
    '<button type="button" data-action="minus15" aria-label="Bớt 15 giây nghỉ">-15s</button>' +
    '<button type="button" data-action="add30" aria-label="Thêm 30 giây nghỉ">+30s</button>' +
    '<button type="button" data-action="pause-timer" aria-label="Tạm dừng hoặc tiếp tục đồng hồ nghỉ">' + (t.running ? 'Tạm dừng' : 'Tiếp tục') + '</button>' +
    '<button type="button" class="skip" data-action="skip-timer" aria-label="Bỏ qua thời gian nghỉ còn lại">Bỏ qua</button>' +
    '</div></div>';
}
function renderRest() {
  document.getElementById('restbar').innerHTML = restbarHtml();
}
function updateTimerDom() {
  var root = document.getElementById('restbar');
  if (!root) return;
  var bar = root.querySelector('.restbar');
  if (!bar) return;
  var t = state.timer;
  bar.classList.toggle('is-finished', t.finished);
  var time = root.querySelector('[data-timer-time]');
  if (time) time.textContent = mmss(t.remaining);
  var fill = root.querySelector('[data-timer-fill]');
  if (fill) fill.style.width = (t.total ? (t.remaining / t.total) * 100 : 0) + '%';
  var label = root.querySelector('[data-timer-label]');
  if (label) label.textContent = t.finished ? 'HẾT GIỜ NGHỈ — VÀO HIỆP TIẾP' : 'NGHỈ GIỮA HIỆP (CHẠY CẢ KHI TẮT MÀN)';
  var pause = root.querySelector('[data-action="pause-timer"]');
  if (pause) pause.textContent = t.running ? 'Tạm dừng' : 'Tiếp tục';
}

/* ---------------- shared bits ---------------- */
var ICO = {
  dumbbell: '<svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor" aria-hidden="true"><rect x="0.8" y="9" width="3" height="6" rx="1.2"/><rect x="4.3" y="6" width="3.2" height="12" rx="1.2"/><rect x="16.5" y="6" width="3.2" height="12" rx="1.2"/><rect x="20.2" y="9" width="3" height="6" rx="1.2"/><rect x="7" y="10.4" width="10" height="3.2" rx="1.2"/></svg>',
  calendar: '<svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor" aria-hidden="true"><path d="M7 2v2H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2h-2V2h-2.5v2h-5V2H7zm12 18H5V9h14v11z"/></svg>',
  food: '<svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor" aria-hidden="true"><path d="M11 2v20c-2.21 0-4-1.79-4-4v-6H5V4h2V2h2v8h1V2h1zm10 1.31L15.86 8.43c-.74.74-1.14 1.72-1.14 2.76V22h2.5V11.19c0-1.04.4-2.02 1.14-2.76L21 5.83V3.31z" transform="scale(0.92) translate(1 0)"/></svg>',
  book: '<svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor" aria-hidden="true"><path d="M18 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2zM6 4h5v8l-2.5-1.5L6 12V4z"/></svg>',
  search: '<svg viewBox="0 0 24 24" width="19" height="19" fill="currentColor" aria-hidden="true"><path d="M15.5 14h-.79l-.28-.27a6.5 6.5 0 1 0-.7.7l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0A4.5 4.5 0 1 1 14 9.5 4.5 4.5 0 0 1 9.5 14z"/></svg>',
  check: '<svg viewBox="0 0 24 24" width="36" height="36" fill="currentColor" aria-hidden="true"><path d="M9 16.17 4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg>',
  lock: '<svg viewBox="0 0 24 24" width="30" height="30" fill="currentColor" aria-hidden="true"><path d="M12 2a5 5 0 0 0-5 5v3H6a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8a2 2 0 0 0-2-2h-1V7a5 5 0 0 0-5-5zm-3 8V7a3 3 0 1 1 6 0v3H9z"/></svg>'
};
function dateLine() {
  var s = new Date().toLocaleDateString('vi-VN', { weekday: 'long', day: 'numeric', month: 'long' });
  return s.charAt(0).toUpperCase() + s.slice(1) + ' • Chu kỳ Hypertrophy';
}
function recentSessionsHtml() {
  var items = '';
  if (state.sessionSaved) {
    items += '<li><span><strong>Push Day A</strong> · Ngực và vai trước</span><span class="when">Hôm nay · ' + fmt(state.savedVolume) + ' kg · ' + state.savedSets + ' hiệp</span></li>';
  }
  items += '<li><span><strong>Pull Day A</strong> · Lưng và tay trước</span><span class="when">Hôm qua · 5.980 kg</span></li>' +
    '<li><span><strong>Leg Day</strong> · Chân</span><span class="when">3 ngày trước · 7.420 kg</span></li>';
  return items;
}

/* ---------------- screens: home ---------------- */
function screenHome() {
  var trained = state.sessionSaved ? 4 : 3;
  var goalMsg = state.sessionSaved
    ? 'Đã hoàn thành mục tiêu 4 buổi/tuần! Duy trì phong độ Hypertrophy.'
    : 'Chỉ còn ' + (4 - trained) + ' buổi nữa để hoàn thành mục tiêu 4 buổi/tuần!';
  var next = [
    ['Bench Press', 4], ['Incline Dumbbell Press', 4], ['Overhead Press', 3], ['Dip', 3], ['Triceps Pushdown', 4]
  ].map(function (x, i) {
    return '<li><span><span class="n">' + (i + 1) + '.</span>' + x[0] + '</span><span class="sets">' + x[1] + ' hiệp</span></li>';
  }).join('');
  return '<p class="date-line">' + dateLine() + '</p>' +
    '<h2 class="greet">Chào Hoàng</h2>' +
    '<div class="search-pill">' + ICO.search + '<span>Tìm bài tập, giáo án, nhóm cơ...</span></div>' +
    '<div class="quick" role="group" aria-label="Thao tác nhanh">' +
    '<a href="#workout" aria-label="Bắt đầu tập">' + ICO.dumbbell + 'Tập</a>' +
    '<a href="#routines" aria-label="Mở Giáo án">' + ICO.calendar + 'Giáo án</a>' +
    '<a href="#nutrition" aria-label="Mở Ăn uống">' + ICO.food + 'Ăn uống</a>' +
    '<a href="#routines" aria-label="Mở Kho bài tập">' + ICO.book + 'Kho bài</a></div>' +
    '<section class="card" aria-label="Buổi tập hôm nay">' +
    '<div class="card__head"><div><p class="card__label">Buổi hôm nay · PPL</p>' +
    '<p class="card__title">Push Day A</p></div><span class="chip chip--lime">Chu kỳ 12 tuần</span></div>' +
    '<div class="stats-grid">' +
    '<div class="stat"><span>Bài tập</span><b>5 bài</b></div>' +
    '<div class="stat"><span>Tổng hiệp</span><b>18 hiệp</b></div>' +
    '<div class="stat"><span>Thời gian</span><b>~55p</b></div>' +
    '<div class="stat"><span>Target RIR</span><b>1 - 2</b></div></div>' +
    '<p class="sec-title" style="margin-top:0.85rem">Bài sắp tới</p>' +
    '<ul class="upnext">' + next + '</ul>' +
    '<a class="btn-lime" href="#workout" style="margin-top:0.9rem" aria-label="Bắt đầu buổi tập Push Day A">Bắt đầu buổi tập</a>' +
    '<p class="caption">Mở thẳng màn hình ghi hiệp &amp; kích hoạt Rest Timer</p></section>' +
    '<section class="card" aria-label="Mục tiêu tuần"><div class="goal-row">' +
    '<b>Mục tiêu tuần: ' + trained + '/4 buổi hoàn tất</b><span class="chip chip--emerald">' + Math.round(trained / 4 * 100) + '%</span></div>' +
    '<span class="bar"><span class="bar__fill" style="width:' + (trained / 4 * 100) + '%"></span></span>' +
    '<p class="card__sub">' + goalMsg + '</p></section>' +
    '<section class="card" aria-label="Phục hồi cơ bắp"><div class="card__head">' +
    '<p class="sec-title">Trạng thái phục hồi cơ bắp</p><span class="chip chip--emerald">Tối ưu</span></div>' +
    '<p class="card__sub" style="margin-top:0.4rem">Nhóm cơ đẩy (Push) đã hồi phục. Đây là <strong>thời điểm vàng</strong> để kích hoạt cơ chế Progressive Overload!</p>' +
    '<div class="kv" style="margin-top:0.4rem"><span>Volume tuần</span><strong class="num">' + fmtK(weekVolume()) + ' kg</strong></div>' +
    '<div class="kv"><span>Giai đoạn</span><strong>Chu kỳ tích lũy MAV</strong></div></section>' +
    '<h3 class="sec-title">Buổi tập gần đây</h3>' +
    '<ul class="list">' + recentSessionsHtml() + '</ul>';
}

/* ---------------- screens: routines ---------------- */
function routineCard(name, meta, badge) {
  return '<div class="card"><div class="card__head"><p class="card__title" style="font-size:1.08rem">' + name + '</p>' +
    (badge ? '<span class="chip chip--lime">' + badge + '</span>' : '') + '</div>' +
    '<p class="card__sub">' + meta + '</p></div>';
}
function screenRoutines() {
  var library = [
    ['Bench Press', 'Đẩy ngực ngang đòn tạ · Ngực'],
    ['Incline Dumbbell Press', 'Đẩy ngực trên tạ đơn · Ngực'],
    ['Overhead Press', 'Đẩy vai đứng tạ đòn · Vai'],
    ['Dip', 'Xà kép có tải · Ngực · Tay sau'],
    ['Triceps Pushdown', 'Duỗi tay sau với cáp · Tay sau']
  ].map(function (x) {
    return '<li><span><strong>' + x[0] + '</strong></span><span class="when">' + x[1] + '</span></li>';
  }).join('');
  return routineCard('PPL Hypertrophy', '6 ngày / tuần · 50–65 phút mỗi buổi · Push / Pull / Legs × 2 vòng', 'Đang theo') +
    routineCard('Upper-Lower', '4 ngày / tuần · 60–70 phút mỗi buổi') +
    routineCard('Arnold', '6 ngày / tuần · 65–75 phút mỗi buổi') +
    routineCard('Full body', '3 ngày / tuần · 65–75 phút mỗi buổi') +
    '<h3 class="sec-title">Ngày tập — PPL Hypertrophy</h3>' +
    '<section class="card" aria-label="Push Day A"><div class="card__head"><div>' +
    '<p class="card__title" style="font-size:1.08rem">Push Day A</p>' +
    '<p class="card__sub">Ngực và vai trước · 5 bài · 18 hiệp</p></div>' +
    '<span class="chip">Hôm nay</span></div>' +
    '<a class="btn-lime" href="#workout" style="margin-top:0.8rem" aria-label="Bắt đầu Push Day A">Bắt đầu buổi tập</a></section>' +
    '<a class="card" href="#unavailable" aria-label="Mở giáo án tùy chỉnh"><div class="card__head">' +
    '<p class="card__title" style="font-size:1.08rem">Tạo giáo án tùy chỉnh</p><span class="chip">Trong app thật</span></div>' +
    '<p class="card__sub">Tự sắp xếp bài, số hiệp và thứ tự tập theo ý bạn</p></a>' +
    '<h3 class="sec-title">Thư viện bài tập</h3>' +
    '<ul class="list">' + library + '</ul>';
}

function screenUnavailable() {
  return '<div class="lock-wrap"><span class="lock-ico">' + ICO.lock + '</span>' +
    '<h2 class="greet" style="font-size:1.35rem">Giáo án tùy chỉnh</h2>' +
    '<p class="muted" style="max-width:17rem">Tính năng này có đầy đủ trong ứng dụng Android — bạn tự sắp xếp bài, số hiệp và thứ tự tập. Bản demo trên web chỉ chạy một buổi mẫu <strong>Push Day A</strong> để bạn thử dòng ghi hiệp.</p>' +
    '<a class="btn-lime" href="#workout">Chạy Push Day A thay thế</a>' +
    '<a class="btn-ghost" href="#routines">Quay lại Giáo án</a></div>';
}

/* ---------------- screens: workout ---------------- */
function loggedRowsHtml() {
  if (!state.logged.length) {
    return '<tr class="empty-row"><td colspan="6">Chưa có hiệp nào — ghi hiệp đầu tiên ở ô dưới đây.</td></tr>';
  }
  return state.logged.map(function (s, i) {
    return '<tr><td class="num">' + (i + 1) + '</td><td class="num">60kg × 8</td>' +
      '<td class="num"><strong>' + fmtKg(s.kg) + '</strong></td><td class="num"><strong>' + s.reps + '</strong></td>' +
      '<td class="num">' + (s.rir === 4 ? '4+' : s.rir) + '</td>' +
      '<td class="c">Đã xong' + (isPR(s) ? '<span class="pr-chip">PR</span>' : '') + '</td></tr>';
  }).join('');
}
function draftLabel() { return 'Ghi hiệp (' + fmtKg(state.draft.kg) + ' × ' + state.draft.reps + ')'; }
function screenWorkout() {
  var err = state.workoutError ? '<p class="error" role="alert">' + esc(state.workoutError) + '</p>' : '';
  var rirChips = [0, 1, 2, 3, 4].map(function (r) {
    return '<button type="button" class="chip" data-rir="' + r + '" aria-pressed="' + (state.rir === r) + '" aria-label="Chọn RIR thực tế ' + (r === 4 ? '4+' : r) + '">' + (r === 4 ? '4+' : r) + '</button>';
  }).join('');
  return '<p><span class="live-chip">ĐANG GHI BUỔI TẬP</span></p>' +
    '<h2 class="wo-title">BUỔI TẬP: NGỰC • VAI TRƯỚC</h2>' +
    '<div class="wo-stats">' +
    '<div class="wo-stat"><span>Tổng volume</span><b class="num" data-live-volume>' + fmt(totalVolume()) + ' kg</b></div>' +
    '<div class="wo-stat"><span>Tiến độ hiệp</span><b class="num">' + state.logged.length + ' / ' + TARGET_SETS + ' hiệp</b></div></div>' +
    err +
    '<section class="ex-card" aria-label="Bài Bench Press">' +
    '<div class="ex-head"><span class="ex-order">1</span><div>' +
    '<p class="ex-name">Bench Press</p><p class="ex-muscle">NGỰC (CHÍNH)</p></div></div>' +
    '<div class="suggest-box"><p class="card__label">Gợi ý Progressive Overload</p>' +
    '<p>Buổi trước: 60kg × 8 • 60kg × 8 • 60kg × 7</p>' +
    '<p><strong>Mục tiêu hôm nay: 60 × 8 reps @ RIR 2</strong></p></div>' +
    '<table class="sets"><thead><tr><th scope="col">SET</th><th scope="col">MỤC TIÊU</th><th scope="col">KG</th><th scope="col">REPS</th><th scope="col">RIR</th><th scope="col" class="c">XONG</th></tr></thead>' +
    '<tbody>' + loggedRowsHtml() + '</tbody></table>' +
    '<div class="draft" aria-label="Ghi hiệp mới">' +
    '<div class="draft__grid">' +
    '<div class="field"><label for="draft-kg">Tạ (kg)</label>' +
    '<input id="draft-kg" class="set-input" data-field="kg" type="number" inputmode="decimal" min="0" step="2.5" value="' + state.draft.kg + '" aria-label="Số kg của hiệp sắp ghi"></div>' +
    '<div class="field"><label for="draft-reps">Số rep</label>' +
    '<input id="draft-reps" class="set-input" data-field="reps" type="number" inputmode="numeric" min="0" step="1" value="' + state.draft.reps + '" aria-label="Số rep của hiệp sắp ghi"></div></div>' +
    '<div><p class="draft__label">CHỈNH NHANH:</p><div class="chip-row" style="margin-top:0.4rem">' +
    '<button type="button" class="chip" data-step="kg" data-delta="-2.5" aria-label="Giảm tạ 2,5 kg">−2.5kg</button>' +
    '<button type="button" class="chip" data-step="kg" data-delta="2.5" aria-label="Tăng tạ 2,5 kg">+2.5kg</button>' +
    '<button type="button" class="chip" data-step="reps" data-delta="-1" aria-label="Giảm 1 rep">−1 rep</button>' +
    '<button type="button" class="chip" data-step="reps" data-delta="1" aria-label="Tăng 1 rep">+1 rep</button></div></div>' +
    '<div><p class="draft__label">CHỌN RIR THỰC TẾ: <span class="hint">Gợi ý: 2</span></p>' +
    '<div class="chip-row" role="group" aria-label="Chọn RIR thực tế" style="margin-top:0.4rem">' + rirChips + '</div></div>' +
    '<button type="button" class="btn-lime" data-action="log-set" data-log-btn aria-label="Ghi hiệp hiện tại">' + draftLabel() + '</button>' +
    '</div></section>' +
    '<button type="button" class="btn-ghost" data-action="review" aria-label="Kết thúc buổi tập">KẾT THÚC BUỔI TẬP</button>';
}

/* ---------------- screens: review / done ---------------- */
function screenReview() {
  var best = bestSet();
  var sug = computeSuggestion();
  return '<h2 class="wo-title">Kết thúc buổi tập?</h2>' +
    '<p class="muted">Các hiệp đã ghi sẽ được lưu vào lịch sử và tính toán mục tiêu Progressive Overload cho buổi tới.</p>' +
    '<section class="review-card" aria-label="Tóm tắt buổi tập">' +
    '<div class="kv"><span>Hiệp đã ghi (Bench Press)</span><strong class="num">' + state.logged.length + ' / ' + TARGET_SETS + ' hiệp</strong></div>' +
    '<div class="kv"><span>Tổng volume</span><strong class="num">' + fmt(totalVolume()) + ' kg</strong></div>' +
    '<div class="kv"><span>Set tốt nhất</span><strong class="num">' + (best ? fmtKg(best.kg) + ' × ' + best.reps + ' reps @ RIR ' + (best.rir === 4 ? '4+' : best.rir) : '—') + '</strong></div>' +
    '<div class="suggest-result ' + (sug.plus ? 'suggest-result--plus' : 'suggest-result--keep') + '">' +
    '<b>' + sug.headline + '</b><span>' + sug.reason + '</span></div></section>' +
    '<div class="btn-row"><a class="btn-ghost" href="#workout">Hủy</a>' +
    '<button type="button" class="btn-lime" data-action="confirm" aria-label="Hoàn tất và lưu buổi tập">Hoàn tất</button></div>';
}

function screenDone() {
  var sug = state.savedSuggestion || computeSuggestion();
  var best = state.savedBest;
  return '<div class="center"><span class="done-check">' + ICO.check + '</span></div>' +
    '<p class="center"><span class="live-chip" style="color:var(--app-emerald)">ĐÃ HOÀN THÀNH</span></p>' +
    '<h2 class="wo-title center">Buổi tập đã lưu</h2>' +
    '<p class="muted center">Push Day A · Bench Press — bản mô phỏng, không ghi dữ liệu thật.</p>' +
    '<section class="review-card" aria-label="Kết quả buổi tập">' +
    '<div class="kv"><span>Hiệp đã ghi</span><strong class="num">' + state.savedSets + ' hiệp</strong></div>' +
    '<div class="kv"><span>Tổng volume</span><strong class="num">' + fmt(state.savedVolume) + ' kg</strong></div>' +
    '<div class="kv"><span>Set tốt nhất</span><strong class="num">' + (best ? fmtKg(best.kg) + ' × ' + best.reps + ' reps' : '—') + '</strong></div>' +
    '<div class="kv"><span>Gợi ý buổi tới</span><strong>' + (sug.plus ? 'Tăng tạ' : 'Giữ nguyên tạ') + '</strong></div>' +
    '<div class="kv"><span>Mã tham chiếu</span><strong class="num">IRONLOG-DEMO</strong></div></section>' +
    '<a class="btn-lime" href="#home">Về Hôm nay</a>' +
    '<a class="btn-ghost" href="#progress">Xem Tiến bộ</a>';
}

/* ---------------- screens: progress ---------------- */
function e1rmPoints() {
  var pts = BASE_E1RM.slice();
  if (state.sessionSaved && state.savedBest) pts.push(Math.round(e1rm(state.savedBest) * 10) / 10);
  return pts;
}
function muscleRows(list) {
  var chestBonus = (state.weekView === 'now' && state.sessionSaved) ? state.savedSets : 0;
  return list.map(function (m) {
    var sets = m.sets + (m.name === 'Ngực' ? chestBonus : 0);
    var pct = Math.min(100, Math.round((sets / m.mrv) * 100));
    return '<div class="muscle-row"><span>' + m.name + '</span>' +
      '<span class="bar"><span class="bar__fill" style="width:' + pct + '%"></span></span>' +
      '<span class="val">' + sets + ' / ' + m.mrv + ' sets</span></div>';
  }).join('');
}
function screenProgress() {
  var pts = e1rmPoints();
  var max = 85;
  var cols = pts.map(function (v, i) {
    var h = Math.max(12, Math.round((v / max) * 100));
    return '<div class="e1rm-col"><span class="v">' + fmt1(v) + '</span>' +
      '<div class="e1rm-bar' + (i === pts.length - 1 ? ' e1rm-bar--last' : '') + '" style="height:' + h + '%"></div>' +
      '<span class="wk">Buổi ' + (i + 1) + '</span></div>';
  }).join('');
  var best = state.sessionSaved && state.savedBest ? state.savedBest : { kg: 60, reps: 8, rir: 2 };
  var list = state.weekView === 'old' ? MUSCLES_OLD : MUSCLES_NOW;
  return '<div class="card__head"><p class="card__label" style="font-size:0.78rem;color:var(--app-ink)">Dữ liệu Hypertrophy</p>' +
    '<span class="chip chip--lime">Chu kỳ 12W</span></div>' +
    '<section class="card" aria-label="Bài tập và sức mạnh"><p class="sec-title">Bài tập &amp; Sức mạnh</p>' +
    '<p style="margin-top:0.5rem"><span class="chip">Bench Press • Ngực</span></p>' +
    '<p class="card__label" style="margin-top:0.8rem">Đường cong e1RM &amp; kỷ lục tạ (PR)</p>' +
    '<div class="e1rm-chart" role="img" aria-label="Biểu đồ e1RM Bench Press qua các buổi: ' + pts.map(fmt1).join(', ') + ' kg">' + cols + '</div>' +
    '<div class="kv"><span>Set tốt nhất</span><strong class="num">' + fmtKg(best.kg) + ' × ' + best.reps + ' reps @ RIR ' + (best.rir === 4 ? '4+' : best.rir) + '</strong></div>' +
    '<p style="margin-top:0.4rem"><span class="chip chip--emerald">Đạt chuẩn Progressive Overload</span></p></section>' +
    '<section class="card" aria-label="Khối lượng và nhóm cơ"><p class="sec-title">Khối lượng &amp; Nhóm cơ</p>' +
    '<div class="seg" role="group" aria-label="Chọn tuần" style="margin:0.7rem 0 0.4rem">' +
    '<button type="button" data-week="now" aria-pressed="' + (state.weekView === 'now') + '">Tuần này</button>' +
    '<button type="button" data-week="old" aria-pressed="' + (state.weekView === 'old') + '">Tuần trước</button></div>' +
    '<p class="card__label" style="margin:0.4rem 0 0.3rem">Khối lượng tập theo nhóm cơ — Sets/Tuần</p>' +
    muscleRows(list) +
    '<p class="card__sub">Khoảng Hypertrophy khuyến nghị: 10 – 20 sets/tuần (tối thiểu MEV – tối đa MRV).</p></section>' +
    '<h3 class="sec-title">Lịch sử buổi tập</h3>' +
    '<ul class="list">' + recentSessionsHtml() + '</ul>';
}

/* ---------------- screens: nutrition ---------------- */
function screenNutrition() {
  var kcalPct = Math.min(100, Math.round((state.kcal / GOAL_KCAL) * 100));
  var foods = [
    ['Cơm trắng', 130, '2,7'], ['Ức gà', 165, '31'], ['Phở bò', 85, '5,5'], ['Cơm tấm sườn', 163, '7,5']
  ].map(function (m) {
    return '<tr><td>' + m[0] + '</td><td class="n">' + m[1] + '</td><td class="n">' + m[2] + '</td></tr>';
  }).join('');
  var mealItems = state.meals.map(function (m) {
    return '<li><span><strong>' + esc(m.name) + '</strong></span><span class="when">' + esc(m.meta) + '</span></li>';
  }).join('');
  return '<h2 class="wo-title">Dinh dưỡng hôm nay</h2>' +
    '<section class="card" aria-label="Tổng dinh dưỡng hôm nay">' +
    '<p class="nutri-big"><span data-kcal>' + fmt(state.kcal) + '</span> / ' + fmt(GOAL_KCAL) + ' kcal</p>' +
    '<p class="card__sub">Đạm <span data-protein>' + state.protein + '</span>/' + GOAL_PROTEIN + ' g</p>' +
    '<span class="bar" style="margin-top:0.6rem"><span class="bar__fill bar__fill--lime" style="width:' + kcalPct + '%"></span></span></section>' +
    '<div class="chip-row" aria-label="Cách thêm bữa ăn">' +
    '<span class="chip chip-static">Chụp ảnh</span><span class="chip chip-static">Thư viện</span>' +
    '<span class="chip chip-static">Mô tả</span><span class="chip chip-static">Nhập tay</span>' +
    '<span class="chip chip-static">Ăn lại bữa cũ</span></div>' +
    '<button type="button" class="btn-lime" data-action="add-meal" aria-label="Thêm bữa mẫu bằng nhập tay, cộng 450 kcal và 32 gam đạm">Nhập tay — thêm bữa mẫu (+450 kcal, +32 g đạm)</button>' +
    '<h3 class="sec-title">BỮA ĂN (' + state.meals.length + ')</h3>' +
    '<ul class="list">' + mealItems + '</ul>' +
    '<section class="card" aria-label="Bảng món Việt tham chiếu"><p class="sec-title" style="font-size:0.98rem">Bảng món Việt · trên 100 g</p>' +
    '<table class="foods"><thead><tr><th scope="col">Món</th><th scope="col" class="n">kcal</th><th scope="col" class="n">Đạm, g</th></tr></thead>' +
    '<tbody>' + foods + '</tbody></table>' +
    '<p class="card__sub">Bảng đầy đủ trong ứng dụng có gần 90 món; món lạ có thể chụp ảnh để AI ước lượng rồi sửa lại trước khi lưu.</p></section>' +
    '<a class="btn-ghost" href="#home">Quay lại Hôm nay</a>';
}

/* ---------------- screens: profile ---------------- */
function restChipsHtml() {
  return [60, 90, 120, 180].map(function (s) {
    return '<button type="button" class="chip" data-rest="' + s + '" aria-pressed="' + (state.restSeconds === s) + '" aria-label="Đặt thời gian nghỉ mặc định ' + s + ' giây">' + s + 's</button>';
  }).join('');
}
function switchRow(key, title, sub) {
  return '<div class="switch-row"><span class="t"><b>' + title + '</b><span>' + sub + '</span></span>' +
    '<button type="button" class="switch" role="switch" data-pref="' + key + '" aria-checked="' + state.toggles[key] + '" aria-label="' + title + '"></button></div>';
}
function screenProfile() {
  return '<section class="card" aria-label="Hồ sơ vận động viên"><div class="athlete">' +
    '<span class="avatar" aria-hidden="true">TH</span><div>' +
    '<p class="athlete__name">Trần Huy Hoàng</p>' +
    '<p style="margin:0.25rem 0"><span class="chip chip--lime">PRO HYPERTROPHY</span></p>' +
    '<p class="card__sub">@ironlog.lift • Chu kỳ Hypertrophy</p></div></div></section>' +
    '<div class="tiles">' +
    '<div class="tile"><span>Cân nặng</span><b>70,5 kg</b><i>Lean Bulk</i></div>' +
    '<div class="tile"><span>Body fat</span><b>14,2%</b><i>Lean Zone</i></div>' +
    '<div class="tile"><span>Khối nạc (LBM)</span><b>60,5 kg</b><i>Đang tăng đều</i></div></div>' +
    '<section class="card" aria-label="Cấu hình buổi tập"><p class="sec-title" style="font-size:0.95rem">CẤU HÌNH BUỔI TẬP (WORKOUT ENGINE)</p>' +
    '<div class="cfg-row"><div class="goal-row"><b>Thời gian nghỉ mặc định</b><strong class="num">' + state.restSeconds + ' giây</strong></div>' +
    '<div class="chip-row" role="group" aria-label="Chọn thời gian nghỉ mặc định">' + restChipsHtml() + '</div>' +
    '<p class="card__sub">Đồng hồ đếm ngược tự động chạy sau mỗi hiệp (kể cả khi tắt màn hình).</p></div>' +
    '<div class="cfg-row"><div class="goal-row"><b>Bước nhảy tạ nhỏ nhất</b><strong class="num">2,5 kg</strong></div>' +
    '<p class="card__sub">Micro-plates, Chuẩn Gym VN</p></div>' +
    switchRow('sound', 'Âm thanh chuông báo', 'Đếm ngược 3-2-1 chuẩn bị hiệp tiếp theo') +
    switchRow('haptic', 'Rung phản hồi (Haptic)', 'Rung mạnh khi hoàn thành hiệp và hết giờ nghỉ') +
    '</section>' +
    '<section class="card" aria-label="Trạng thái bản demo"><p class="sec-title" style="font-size:0.98rem">Bản demo trên web</p>' +
    '<div class="kv"><span>Hiệp Bench Press đã ghi (phiên này)</span><strong class="num">' + state.logged.length + ' hiệp</strong></div>' +
    '<div class="kv"><span>Volume buổi đang tập</span><strong class="num">' + fmt(totalVolume()) + ' kg</strong></div>' +
    '<div class="kv"><span>Buổi Push Day A đã lưu</span><strong>' + (state.sessionSaved ? 'Rồi' : 'Chưa') + '</strong></div>' +
    '<div class="kv"><span>Dinh dưỡng hôm nay</span><strong class="num">' + fmt(state.kcal) + ' kcal · ' + state.protein + ' g đạm</strong></div>' +
    '<div class="kv"><span>Thời gian nghỉ mặc định</span><strong class="num">' + state.restSeconds + ' giây</strong></div>' +
    '<button type="button" class="btn-lime" data-action="reset" style="margin-top:0.7rem" aria-label="Đặt lại toàn bộ dữ liệu demo">Reset demo</button>' +
    '<p class="caption">Reset đưa mọi hiệp, đồng hồ nghỉ, bữa ăn, thời gian nghỉ và tiến bộ về đúng trạng thái ban đầu.</p></section>' +
    '<a class="btn-ghost" href="index.html">← Quay lại trang giới thiệu</a>';
}

/* ---------------- render ---------------- */
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
  document.getElementById('appbar-title').textContent = TITLES[name];
  document.getElementById('appbar-back').hidden = name !== 'nutrition';
  var logo = document.querySelector('.appbar__logo');
  if (logo) logo.style.display = name === 'nutrition' ? 'none' : '';
  var tabs = document.querySelectorAll('#tabbar [data-tab]');
  tabs.forEach(function (a) {
    if (a.getAttribute('data-tab') === TAB_FOR[name]) a.classList.add('is-on');
    else a.classList.remove('is-on');
  });
  renderRest();
  root.scrollTop = 0;
}

/* ---------------- events ---------------- */
function updateDraftBits() {
  var root = document.getElementById('screen');
  var btn = root.querySelector('[data-log-btn]');
  if (btn) btn.textContent = draftLabel();
  var vol = root.querySelector('[data-live-volume]');
  if (vol) vol.textContent = fmt(totalVolume()) + ' kg';
}

document.getElementById('screen').addEventListener('input', function (ev) {
  var el = ev.target;
  if (!el.classList || !el.classList.contains('set-input')) return;
  var field = el.getAttribute('data-field');
  var v = field === 'kg' ? parseFloat(el.value) : parseInt(el.value, 10);
  state.draft[field] = isNaN(v) ? 0 : Math.max(0, v);
  updateDraftBits();
});

document.addEventListener('click', function (ev) {
  var stepBtn = ev.target.closest('[data-step]');
  if (stepBtn) {
    var field = stepBtn.getAttribute('data-step');
    var delta = parseFloat(stepBtn.getAttribute('data-delta'));
    var next = state.draft[field] + delta;
    state.draft[field] = Math.max(0, Math.round(next * 10) / 10);
    render();
    return;
  }
  var rirBtn = ev.target.closest('[data-rir]');
  if (rirBtn) { state.rir = Number(rirBtn.getAttribute('data-rir')); render(); return; }
  var weekBtn = ev.target.closest('[data-week]');
  if (weekBtn) { state.weekView = weekBtn.getAttribute('data-week'); render(); return; }
  var restBtn = ev.target.closest('[data-rest]');
  if (restBtn) { state.restSeconds = Number(restBtn.getAttribute('data-rest')); render(); return; }
  var prefBtn = ev.target.closest('[data-pref]');
  if (prefBtn) {
    var key = prefBtn.getAttribute('data-pref');
    state.toggles[key] = !state.toggles[key];
    render();
    return;
  }
  var act = ev.target.closest('[data-action]');
  if (!act) return;
  var action = act.getAttribute('data-action');
  if (action === 'log-set') {
    var d = state.draft;
    if (d.kg <= 0 || d.reps <= 0) {
      state.workoutError = 'Hiệp cần số rep và kg lớn hơn 0 trước khi ghi.';
      render();
      return;
    }
    if (d.kg > 200) {
      state.workoutError = 'Mức tạ trên 200 kg — kiểm tra lại số đã nhập.';
      render();
      return;
    }
    state.logged.push({ kg: d.kg, reps: d.reps, rir: state.rir });
    state.workoutError = '';
    startRest();
    render();
  } else if (action === 'review') {
    if (state.logged.length === 0) {
      state.workoutError = 'Chưa ghi hiệp nào. Ghi ít nhất một hiệp để tiếp tục.';
      render();
      return;
    }
    state.workoutError = '';
    location.hash = '#review';
  } else if (action === 'confirm') {
    if (!state.sessionSaved) {
      state.sessionSaved = true;
      state.savedVolume = totalVolume();
      state.savedSets = state.logged.length;
      state.savedBest = bestSet();
      state.savedSuggestion = computeSuggestion();
    }
    state.timer.visible = false;
    state.timer.running = false;
    location.hash = '#done';
  } else if (action === 'pause-timer') {
    if (state.timer.remaining > 0) {
      state.timer.running = !state.timer.running;
      state.timer.finished = false;
      ensureTicker();
    }
    updateTimerDom();
  } else if (action === 'add30') {
    if (state.timer.finished) { state.timer.total = 0; state.timer.finished = false; }
    state.timer.remaining += 30;
    state.timer.total += 30;
    state.timer.running = true;
    ensureTicker();
    renderRest();
  } else if (action === 'minus15') {
    state.timer.remaining = Math.max(0, state.timer.remaining - 15);
    if (state.timer.remaining === 0) { state.timer.running = false; state.timer.finished = true; }
    updateTimerDom();
  } else if (action === 'skip-timer') {
    state.timer.visible = false;
    state.timer.running = false;
    state.timer.finished = false;
    renderRest();
  } else if (action === 'add-meal') {
    state.mealsAdded += 1;
    state.meals.push({ name: 'Bữa mẫu (nhập tay)', meta: 'Cơm + thịt · ' + MEAL_KCAL + ' kcal · Đạm ' + MEAL_PROTEIN + ' g' });
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

/* status-bar clock (real local time, Android style) */
function tickClock() {
  var el = document.getElementById('sb-time');
  if (!el) return;
  var d = new Date();
  el.textContent = d.getHours() + ':' + (d.getMinutes() < 10 ? '0' : '') + d.getMinutes();
}
tickClock();
setInterval(tickClock, 20000);
render();
