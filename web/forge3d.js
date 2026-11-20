/* ==========================================================================
   GymForge — "THE BARBELL" product-turntable engine for IronLog (GYMVERSE)

   One WebGL context, one canvas, re-parented across hosts by the
   controller in landing.js: hero (plates assemble on, rest + sway +
   pointer parallax) → forge (scroll-scrubbed turntable + exploded view,
   one plate claimed per chapter beat) → closing (one slow press, then a
   slow rotation behind the CTA).

   The barbell is built entirely from primitives with procedural canvas
   textures (knurling bump, plate faces) — no external assets. Palette is
   THIS page's system ("Gunmetal Chalk"): ground #10171d, steel #18222a,
   chalk #ece7dd, haze #97a3ab, and plate-yellow #e9c33f as the only
   accent — the same yellow the page saves for the one number that
   matters. Plates are the black plates of the photography; one gold ring
   per face. No violet anywhere, scene lighting included. All motion is
   eased in the render loop from plain state (progress / pointer / rep
   timeline) — no per-frame DOM.
   ========================================================================== */
(function (global) {
  "use strict";

  var PLATE_BODY = 0x161c22;   /* the black plates of the photos */
  var GOLD = 0xe9c33f;         /* --plate: the one accent */

  var state = {
    ready: false, dead: false, running: false, active: false,
    mode: "veil",
    canvas: null, renderer: null, scene: null, camera: null,
    rig: null, barbell: null, groundY: -1.585,
    units: [],            // [{plates:[{group,home,side}], mats:[...] }] claim units 20/10/5/collar
    clock: null, raf: 0, time: 0,
    progress: { hero: 0, forge: 0 },
    assemble: 0, assembleStart: -1,
    explode: 0,
    pointer: { x: 0, y: 0, tx: 0, ty: 0 },
    pose: { grpX: 0, grpY: 0, camX: 0, camY: 0.55, dist: 11, lookX: 0, lookY: 0, rotY: -0.35 },
    spin: 0,
    highlight: -1, highlightLevel: [0, 0, 0, 0],
    rep: { phase: "idle", t: 0, y: 0, v: 0, rattle: 0 },
    closingArmed: true,
    opts: {},
    ro: null, firstFrame: false
  };

  /* ---------------- helpers ---------------- */
  function clamp(v, a, b) { return Math.max(a, Math.min(b, v)); }
  function damp(cur, target, k, dt) { return cur + (target - cur) * (1 - Math.exp(-dt * k)); }
  function easeOutCubic(x) { return 1 - Math.pow(1 - x, 3); }
  function easeInOut(x) { return x < 0.5 ? 2 * x * x : 1 - Math.pow(-2 * x + 2, 2) / 2; }
  function smoothstep(a, b, x) { var t = clamp((x - a) / (b - a), 0, 1); return t * t * (3 - 2 * t); }

  /* ---------------- procedural textures ---------------- */
  function knurlTexture(THREE) {
    var c = document.createElement("canvas");
    c.width = c.height = 256;
    var x = c.getContext("2d");
    x.fillStyle = "#7f7f7f";
    x.fillRect(0, 0, 256, 256);
    x.lineWidth = 7;
    for (var i = -256; i < 512; i += 22) {
      x.strokeStyle = "#2e2e2e";
      x.beginPath(); x.moveTo(i, 0); x.lineTo(i + 256, 256); x.stroke();
      x.beginPath(); x.moveTo(i + 256, 0); x.lineTo(i, 256); x.stroke();
      x.strokeStyle = "#c9c9c9";
      x.lineWidth = 2.5;
      x.beginPath(); x.moveTo(i + 6, 0); x.lineTo(i + 262, 256); x.stroke();
      x.beginPath(); x.moveTo(i + 262, 0); x.lineTo(i + 6, 256); x.stroke();
      x.lineWidth = 7;
    }
    var tex = new THREE.CanvasTexture(c);
    tex.wrapS = tex.wrapT = THREE.RepeatWrapping;
    tex.repeat.set(14, 1);
    return tex;
  }

  function plateFaceTexture(THREE, label, rot180) {
    var S = 512;
    var c = document.createElement("canvas");
    c.width = c.height = S;
    var x = c.getContext("2d");
    /* Cap-UV mapping (measured head-on in a test scene): the -X cap reads
       the canvas as-is; the +X cap reads it rotated 180°. The inner (+X)
       texture is therefore drawn pre-rotated so it reads upright. */
    if (rot180) { x.translate(S, S); x.rotate(Math.PI); }
    /* black painted-steel face, graded like the photography: a warm
       sheen from top-left, edges falling into the ground tone */
    x.fillStyle = "#161c22";
    x.fillRect(0, 0, S, S);
    var grad = x.createRadialGradient(S * 0.38, S * 0.34, 40, S / 2, S / 2, S * 0.52);
    grad.addColorStop(0, "rgba(255,240,210,0.10)");
    grad.addColorStop(0.55, "rgba(255,255,255,0.02)");
    grad.addColorStop(1, "rgba(0,0,0,0.38)");
    x.fillStyle = grad;
    x.fillRect(0, 0, S, S);
    /* machined grooves */
    x.strokeStyle = "rgba(0,0,0,0.34)";
    x.lineWidth = 22;
    x.beginPath(); x.arc(S / 2, S / 2, 238, 0, Math.PI * 2); x.stroke();
    x.strokeStyle = "rgba(236,231,221,0.07)";
    x.lineWidth = 3;
    [168, 138].forEach(function (r) {
      x.beginPath(); x.arc(S / 2, S / 2, r, 0, Math.PI * 2); x.stroke();
    });
    /* the one gold ring — the page's plate-yellow, near the rim */
    x.strokeStyle = "#e9c33f";
    x.lineWidth = 9;
    x.beginPath(); x.arc(S / 2, S / 2, 208, 0, Math.PI * 2); x.stroke();
    /* stamped numeral — drawn twice for an embossed read */
    x.textAlign = "center";
    x.textBaseline = "middle";
    try { x.letterSpacing = "2px"; } catch (e) { /* older canvas */ }
    try { x.fontStretch = "62%"; } catch (e) { /* canvas width axis where supported */ }
    x.font = "820 200px Archivo, \"Helvetica Neue\", Arial, sans-serif";
    x.fillStyle = "rgba(0,0,0,0.5)";
    x.fillText(label, S / 2 + 3, S / 2 - 16 + 3);
    x.fillStyle = "#ece7dd";
    x.fillText(label, S / 2, S / 2 - 16);
    x.font = "700 58px Archivo, \"Helvetica Neue\", Arial, sans-serif";
    x.fillStyle = "rgba(236,231,221,0.88)";
    x.fillText("KG", S / 2, S / 2 + 96);
    x.font = "700 30px Archivo, \"Helvetica Neue\", Arial, sans-serif";
    x.fillStyle = "rgba(151,163,171,0.6)";
    x.fillText("I R O N L O G", S / 2, 92);
    var tex = new THREE.CanvasTexture(c);
    tex.colorSpace = THREE.SRGBColorSpace;
    tex.anisotropy = 4;
    return tex;
  }

  function shadowDiscTexture(THREE) {
    var c = document.createElement("canvas");
    c.width = 256; c.height = 128;
    var x = c.getContext("2d");
    var g = x.createRadialGradient(128, 64, 6, 128, 64, 126);
    g.addColorStop(0, "rgba(0,0,0,0.52)");
    g.addColorStop(0.6, "rgba(0,0,0,0.22)");
    g.addColorStop(1, "rgba(0,0,0,0)");
    x.fillStyle = g;
    x.save();
    x.translate(128, 64); x.scale(1, 0.5); x.translate(-128, -64);
    x.fillRect(0, -64, 256, 256);
    x.restore();
    return new THREE.CanvasTexture(c);
  }

  /* A tiny procedural "studio" for image-based light: a dark dome the
     colour of the room, one warm window card (the light in his photos
     falls from above), a dim cool bounce and one gold strip, PMREM-
     filtered. No HDRIs. */
  function buildEnvironment(THREE, renderer) {
    var scene = new THREE.Scene();
    var c = document.createElement("canvas");
    c.width = 64; c.height = 256;
    var x = c.getContext("2d");
    var g = x.createLinearGradient(0, 0, 0, 256);
    g.addColorStop(0, "#46525c");
    g.addColorStop(0.45, "#232d35");
    g.addColorStop(0.72, "#141b21");
    g.addColorStop(1, "#090c0f");
    x.fillStyle = g;
    x.fillRect(0, 0, 64, 256);
    var domeTex = new THREE.CanvasTexture(c);
    domeTex.colorSpace = THREE.SRGBColorSpace;
    var dome = new THREE.Mesh(
      new THREE.SphereGeometry(50, 16, 12),
      new THREE.MeshBasicMaterial({ map: domeTex, side: THREE.BackSide })
    );
    scene.add(dome);
    function card(w, h, rgb, px, py, pz) {
      var m = new THREE.Mesh(
        new THREE.PlaneGeometry(w, h),
        new THREE.MeshBasicMaterial({ side: THREE.DoubleSide })
      );
      m.material.color.setRGB(rgb[0], rgb[1], rgb[2]);
      m.position.set(px, py, pz);
      m.lookAt(0, 0, 0);
      scene.add(m);
    }
    card(20, 10, [6.5, 5.7, 4.2], 14, 12, 8);    // warm key window
    card(14, 7, [0.55, 0.7, 0.85], -16, 6, -10); // cool bounce
    card(12, 2.2, [2.4, 1.85, 0.45], -6, 3, -16); // gold strip, far behind
    var pmrem = new THREE.PMREMGenerator(renderer);
    var env = pmrem.fromScene(scene, 0.05).texture;
    pmrem.dispose();
    return env;
  }

  /* ---------------- the barbell ---------------- */
  function buildBarbell(THREE) {
    var steel = new THREE.MeshStandardMaterial({
      color: 0x9aa1a6, metalness: 1.0, roughness: 0.38, envMapIntensity: 0.95
    });
    var steelDark = new THREE.MeshStandardMaterial({
      color: 0x596066, metalness: 1.0, roughness: 0.32, envMapIntensity: 1.0
    });
    var knurl = knurlTexture(THREE);
    var knurlMat = new THREE.MeshStandardMaterial({
      color: 0x878e94, metalness: 1.0, roughness: 0.5,
      bumpMap: knurl, bumpScale: 0.9, envMapIntensity: 0.8
    });
    /* the long smooth shaft spans: a mid steel that still catches the
       warm window card — darker chrome disappears into the ground */
    var shaftMat = new THREE.MeshStandardMaterial({
      color: 0x8b9298, metalness: 1.0, roughness: 0.44, envMapIntensity: 0.75
    });
    var hubMat = new THREE.MeshStandardMaterial({
      color: 0x232c34, metalness: 0.6, roughness: 0.4, envMapIntensity: 0.7
    });

    var bar = new THREE.Group();

    function seg(r, len, mat, cx) {
      var m = new THREE.Mesh(new THREE.CylinderGeometry(r, r, len, 28), mat);
      m.rotation.z = Math.PI / 2;
      m.position.x = cx;
      m.castShadow = true;
      bar.add(m);
      return m;
    }
    /* shaft: knurled grips + centre knurl, smooth between, sleeves outboard.
       Radii keep real proportions against the 450 mm plates (1 unit ≈
       150 mm): 29 mm shaft, 50 mm sleeves. */
    seg(0.078, 0.62, knurlMat, 0);                 // centre knurl
    seg(0.078, 0.58, shaftMat, 0.60);
    seg(0.078, 1.14, knurlMat, 1.46);              // right grip knurl
    seg(0.078, 1.14, knurlMat, -1.46);             // left grip knurl
    seg(0.078, 0.34, shaftMat, 2.20);
    seg(0.078, 0.34, shaftMat, -2.20);
    seg(0.108, 0.14, steelDark, 2.42);             // flange / collar stop
    seg(0.108, 0.14, steelDark, -2.42);
    seg(0.138, 1.30, steel, 3.06);                 // sleeve R (2.41 → 3.71)
    seg(0.138, 1.30, steel, -3.06);                // sleeve L
    seg(0.152, 0.07, steelDark, 3.74);             // end cap R
    seg(0.152, 0.07, steelDark, -3.74);            // end cap L
    bar.traverse(function (o) { if (o.isMesh) o.castShadow = true; });

    /* plates — claim units shared across both sides */
    var specs = [
      { label: "20", r: 1.50, t: 0.34, explode: 0.55 },
      { label: "10", r: 1.16, t: 0.27, explode: 1.20 },
      { label: "5",  r: 0.90, t: 0.21, explode: 1.78 }
    ];
    var units = [];

    function makePlate(spec, side) {
      var g = new THREE.Group();
      /* Cap-texture orientation, measured in test scenes + screenshots:
         the -X cap reads the canvas as-is, the +X cap reads it rotated
         180°, and the right side's pi group-flip inverts both — so the
         pre-rotation is an XOR of cap and side. */
      var faceOut = plateFaceTexture(THREE, spec.label, side > 0);
      var faceIn = plateFaceTexture(THREE, spec.label, side < 0);
      var sideMat = new THREE.MeshStandardMaterial({
        color: PLATE_BODY, metalness: 0.4, roughness: 0.46, envMapIntensity: 0.65,
        emissive: GOLD, emissiveIntensity: 0
      });
      var outMat = new THREE.MeshStandardMaterial({
        map: faceOut, metalness: 0.4, roughness: 0.44, envMapIntensity: 0.7,
        emissive: GOLD, emissiveIntensity: 0, emissiveMap: faceOut
      });
      var inMat = new THREE.MeshStandardMaterial({
        map: faceIn, metalness: 0.4, roughness: 0.48, envMapIntensity: 0.6,
        emissive: GOLD, emissiveIntensity: 0, emissiveMap: faceIn
      });
      /* cylinder axis Y → rotate to X. Cap groups after rotateZ(π/2):
         index 1 faces −X, index 2 faces +X. Outer face is +X on the
         right stack, −X on the left stack. */
      var geo = new THREE.CylinderGeometry(spec.r, spec.r, spec.t, 72);
      geo.rotateZ(Math.PI / 2);
      var mats = side > 0 ? [sideMat, inMat, outMat] : [sideMat, outMat, inMat];
      var disc = new THREE.Mesh(geo, mats);
      disc.castShadow = true;
      g.add(disc);
      /* hub */
      var hubGeo = new THREE.CylinderGeometry(spec.r * 0.30, spec.r * 0.30, spec.t + 0.09, 40);
      hubGeo.rotateZ(Math.PI / 2);
      var hub = new THREE.Mesh(hubGeo, hubMat);
      hub.castShadow = true;
      g.add(hub);
      /* bevel rim — a torus hugging the outer edge catches the key light */
      var rimMat = new THREE.MeshStandardMaterial({
        color: 0x707880, metalness: 0.95, roughness: 0.3, envMapIntensity: 1.0,
        emissive: GOLD, emissiveIntensity: 0
      });
      var rimGeo = new THREE.TorusGeometry(spec.r - 0.015, 0.030, 12, 80);
      rimGeo.rotateY(Math.PI / 2);
      var rim = new THREE.Mesh(rimGeo, rimMat);
      rim.position.x = side * (spec.t / 2 - 0.006);
      rim.castShadow = true;
      g.add(rim);
      return { group: g, mats: [sideMat, outMat, inMat, rimMat] };
    }

    [1, -1].forEach(function (side) {
      var cursor = 2.52;
      specs.forEach(function (spec, i) {
        var center = cursor + spec.t / 2;
        var p = makePlate(spec, side);
        p.group.position.x = side * center;
        bar.add(p.group);
        if (!units[i]) units[i] = { spec: spec, plates: [], mats: [] };
        units[i].plates.push({ group: p.group, home: center, side: side, phase: Math.random() * 6.28 });
        p.mats.forEach(function (m) { units[i].mats.push(m); });
        cursor = center + spec.t / 2 + 0.025;
      });
      /* collar — dark steel clamp with a lever tab */
      var cg = new THREE.Group();
      var cGeo = new THREE.CylinderGeometry(0.30, 0.30, 0.24, 40);
      cGeo.rotateZ(Math.PI / 2);
      var collarMat = new THREE.MeshStandardMaterial({
        color: 0x7d858d, metalness: 1.0, roughness: 0.32, envMapIntensity: 1.0,
        emissive: GOLD, emissiveIntensity: 0
      });
      var collar = new THREE.Mesh(cGeo, collarMat);
      collar.castShadow = true;
      cg.add(collar);
      var lever = new THREE.Mesh(new THREE.BoxGeometry(0.10, 0.34, 0.16), collarMat);
      lever.position.set(0, 0.30, 0);
      lever.castShadow = true;
      cg.add(lever);
      var cCenter = cursor + 0.025 + 0.12;
      cg.position.x = side * cCenter;
      bar.add(cg);
      if (!units[3]) units[3] = { spec: { label: "collar", explode: 2.18 }, plates: [], mats: [] };
      units[3].plates.push({ group: cg, home: cCenter, side: side, phase: Math.random() * 6.28 });
      units[3].mats.push(collarMat);
    });

    return { group: bar, units: units };
  }

  /* ---------------- engine ---------------- */
  function init(canvas, opts) {
    if (state.ready) return true;
    var THREE = global.THREE;
    if (!THREE || !canvas) return false;
    state.opts = opts || {};
    state.canvas = canvas;

    var renderer;
    try {
      renderer = new THREE.WebGLRenderer({
        canvas: canvas, alpha: true, antialias: true, powerPreference: "high-performance"
      });
    } catch (e) { return false; }
    if (!renderer || !renderer.getContext()) return false;

    /* software-renderer gate (QA force bypasses) */
    if (!state.opts.force) {
      try {
        var gl = renderer.getContext();
        var dbg = gl.getExtension("WEBGL_debug_renderer_info");
        var rstr = dbg ? String(gl.getParameter(dbg.UNMASKED_RENDERER_WEBGL)) : "";
        if (/swiftshader|llvmpipe|software|angle \(google/i.test(rstr)) { renderer.dispose(); return false; }
      } catch (e) { /* if we cannot tell, proceed */ }
    }

    var coarse = global.matchMedia && global.matchMedia("(pointer: coarse)").matches;
    renderer.setPixelRatio(Math.min(global.devicePixelRatio || 1, coarse ? 1.25 : 1.5));
    renderer.shadowMap.enabled = true;
    renderer.shadowMap.type = THREE.PCFShadowMap;
    renderer.toneMapping = THREE.ACESFilmicToneMapping;
    renderer.toneMappingExposure = 1.05;
    renderer.outputColorSpace = THREE.SRGBColorSpace;

    var scene = new THREE.Scene();
    scene.environment = buildEnvironment(THREE, renderer);

    var camera = new THREE.PerspectiveCamera(40, 1, 0.1, 100);
    camera.position.set(0, 0.55, 11);

    /* room light — one warm key from above with shadows (the light in
       his photos), a whisper of cool fill, a dim blue-grey hemi base */
    scene.add(new THREE.HemisphereLight(0x8fa3b0, 0x0c0e10, 0.5));
    var key = new THREE.DirectionalLight(0xffe9bb, 2.6);
    key.position.set(5, 8, 4);
    key.castShadow = true;
    key.shadow.mapSize.set(coarse ? 1024 : 2048, coarse ? 1024 : 2048);
    key.shadow.camera.left = -7.5; key.shadow.camera.right = 7.5;
    key.shadow.camera.top = 6; key.shadow.camera.bottom = -4;
    key.shadow.camera.near = 1; key.shadow.camera.far = 30;
    key.shadow.bias = -0.0004;
    key.shadow.normalBias = 0.02;
    scene.add(key);
    var fill = new THREE.DirectionalLight(0x9fb4c4, 0.35);
    fill.position.set(-6, 3, -5);
    scene.add(fill);

    /* ground: shadow catcher + a soft painted contact pool */
    var ground = new THREE.Mesh(
      new THREE.PlaneGeometry(46, 46),
      new THREE.ShadowMaterial({ opacity: 0.42 })
    );
    ground.rotation.x = -Math.PI / 2;
    ground.position.y = state.groundY;
    ground.receiveShadow = true;
    scene.add(ground);
    var pool = new THREE.Mesh(
      new THREE.PlaneGeometry(15.5, 4.6),
      new THREE.MeshBasicMaterial({
        map: shadowDiscTexture(THREE), transparent: true, depthWrite: false
      })
    );
    pool.rotation.x = -Math.PI / 2;
    pool.position.y = state.groundY + 0.002;
    scene.add(pool);

    var built = buildBarbell(THREE);
    var rig = new THREE.Group();       // rep / sway motion
    rig.add(built.group);
    scene.add(rig);

    state.renderer = renderer;
    state.scene = scene;
    state.camera = camera;
    state.rig = rig;
    state.barbell = built.group;
    state.units = built.units;
    /* NOTE: no THREE.Clock/Timer — in this vendored three (r187) Clock is a
       stub returning 0 and Timer needs update() calls. The frame loop runs
       on performance.now() deltas instead. */
    state.lastT = 0;
    state.ready = true;
    /* the assembly plays wherever the canvas first lands (the hero —
       this page has no loader veil): stamp the wall-clock start now so
       the plates slide on as the hero settles */
    if (state.assembleStart < 0) state.assembleStart = performance.now() / 1000;

    canvas.addEventListener("webglcontextlost", function (ev) {
      ev.preventDefault();
      state.dead = true;
      stop();
      if (state.opts.onDead) state.opts.onDead();
    });

    document.addEventListener("visibilitychange", function () {
      if (document.hidden) stop();
      else if (state.active) start();
    });

    resize();
    return true;
  }

  function hostSize() {
    var el = state.canvas && state.canvas.parentElement;
    if (!el) return { w: 0, h: 0 };
    return { w: el.clientWidth || 0, h: el.clientHeight || 0 };
  }

  function resize() {
    if (!state.ready) return;
    var s = hostSize();
    if (!s.w || !s.h) return;
    state.renderer.setSize(s.w, s.h, false);
    state.camera.aspect = s.w / s.h;
    state.camera.updateProjectionMatrix();
  }

  function observeHost() {
    if (state.ro) { state.ro.disconnect(); state.ro = null; }
    if (!("ResizeObserver" in global) || !state.canvas.parentElement) return;
    state.ro = new global.ResizeObserver(function () { resize(); });
    state.ro.observe(state.canvas.parentElement);
  }

  function setMode(mode) {
    if (!state.ready || state.dead) return;
    state.mode = mode;
    if (mode === "veil" && state.assembleStart < 0) {
      /* wall-clock stamp: the assembly timeline must complete in real
         time even when frames are slow (software renderers in QA) */
      state.assembleStart = performance.now() / 1000;
    }
    if (mode === "closing" && state.closingArmed) {
      state.closingArmed = false;
      global.setTimeout(function () { doRep(); }, 420);
    }
    if (mode !== "closing") state.closingArmed = true;
    resize();
  }

  function getMode() { return state.mode; }
  function isReady() { return state.ready && !state.dead; }

  function setProgress(which, p) {
    state.progress[which] = clamp(p, 0, 1);
  }

  function setPointer(nx, ny) {
    state.pointer.tx = clamp(nx, -1, 1);
    state.pointer.ty = clamp(ny, -1, 1);
  }

  function doRep() {
    if (!state.ready || state.dead) return;
    if (state.rep.phase !== "idle") return;
    state.rep.phase = "dip";
    state.rep.t = 0;
    if (state.opts.onRep) state.opts.onRep();
  }

  /* active highlight unit from forge progress */
  var PLATE_THRESHOLDS = [0.10, 0.36, 0.60, 0.82];
  function activeUnit(p) {
    var idx = -1;
    for (var i = 0; i < PLATE_THRESHOLDS.length; i++) {
      if (p >= PLATE_THRESHOLDS[i]) idx = i;
    }
    return idx;
  }

  function fitDistance(needW) {
    var aspect = state.camera.aspect || 1;
    var half = Math.tan((state.camera.fov * Math.PI / 180) / 2);
    var d = (needW * 0.5) / (half * aspect);
    return clamp(d, 6.8, 36);
  }

  function updateRep(dt) {
    var rep = state.rep;
    if (rep.phase === "dip") {
      rep.t += dt;
      var k = clamp(rep.t / 0.5, 0, 1);
      rep.y = -0.52 * easeInOut(k);
      if (k >= 1) { rep.phase = "hold"; rep.t = 0; }
    } else if (rep.phase === "hold") {
      rep.t += dt;
      if (rep.t >= 0.12) { rep.phase = "press"; rep.t = 0; rep.v = 0; }
    } else if (rep.phase === "press") {
      rep.t += dt;
      /* spring drive: stiff spring, light damping → a small overshoot */
      rep.v += ((0 - rep.y) * 150 - rep.v * 10.5) * dt;
      rep.y += rep.v * dt;
      rep.rattle = Math.min(1, Math.abs(rep.v) * 0.35 + 0.35);
      if (rep.t > 0.35 && Math.abs(rep.y) < 0.004 && Math.abs(rep.v) < 0.05) {
        rep.y = 0; rep.v = 0; rep.phase = "settle"; rep.t = 0;
      }
      if (rep.t > 2.2) { rep.y = 0; rep.v = 0; rep.phase = "settle"; rep.t = 0; }
    } else if (rep.phase === "settle") {
      rep.t += dt;
      rep.rattle *= Math.exp(-dt * 3.4);
      if (rep.t > 0.8) { rep.phase = "idle"; rep.rattle = 0; }
    }
    return rep.y;
  }

  function frame() {
    if (!state.running) return;
    state.raf = requestAnimationFrame(frame);
    if (document.hidden) return;
    var nowS = performance.now() / 1000;
    var dt = state.lastT ? Math.min(Math.max(nowS - state.lastT, 0), 0.05) : 0.016;
    state.lastT = nowS;
    state.time += dt;
    var t = state.time;
    var mode = state.mode;
    var pose = state.pose;

    /* --- assemble (veil): plates slide on, staggered per unit --- */
    if (state.assembleStart >= 0 && state.assemble < 1) {
      state.assemble = clamp((performance.now() / 1000 - state.assembleStart) / 1.9, 0, 1);
    } else if (mode !== "veil") {
      state.assemble = damp(state.assemble, 1, 8, dt);
    }

    /* --- explode from forge progress --- */
    var forgeP = state.progress.forge;
    var explodeTarget = mode === "forge" ? smoothstep(0.06, 0.88, forgeP) : 0;
    state.explode = damp(state.explode, explodeTarget, 6, dt);
    var explode = state.explode;

    /* --- highlight --- */
    var targetUnit = mode === "forge" ? activeUnit(forgeP) : -1;
    if (targetUnit !== state.highlight) {
      state.highlight = targetUnit;
      if (state.opts.onPlateChange) state.opts.onPlateChange(targetUnit);
    }
    state.units.forEach(function (unit, i) {
      var goal = i === state.highlight ? 0.35 : 0;
      state.highlightLevel[i] = damp(state.highlightLevel[i], goal, 8, dt);
      var lv = state.highlightLevel[i];
      unit.mats.forEach(function (m) { m.emissiveIntensity = lv; });
    });

    /* --- plate positions: home + explode + assemble offsets --- */
    state.units.forEach(function (unit, i) {
      var aLocal = easeOutCubic(clamp(state.assemble * 1.45 - i * 0.14, 0, 1));
      var assembleOff = (1 - aLocal) * 3.1;
      var eLocal = easeOutCubic(clamp(explode * 1.3 - i * 0.09, 0, 1));
      var spread = unit.spec.explode * eLocal;
      unit.plates.forEach(function (p) {
        var rattleX = state.rep.rattle * Math.sin(t * 39 + p.phase * 3.1) * 0.012;
        p.group.position.x = p.side * (p.home + spread + assembleOff) + rattleX;
        p.group.rotation.x = state.rep.rattle * Math.sin(t * 46 + p.phase) * 0.02;
      });
    });

    /* --- rep + sway on the rig --- */
    var repY = updateRep(dt);
    var sway = (mode === "hero" || mode === "veil") ? 1 : 0.35;
    state.rig.position.y = repY + Math.sin(t * 0.85) * 0.035 * sway;
    state.rig.rotation.z = Math.sin(t * 0.55) * 0.008 * sway;
    state.rig.rotation.x = state.rep.rattle * Math.sin(t * 52) * 0.006;

    /* --- pose targets per mode --- */
    var tgt;
    if (mode === "veil") {
      tgt = { grpX: 0, grpY: 0, camX: 0, camY: 0.55, dist: fitDistance(9.8), lookX: 0, lookY: 0, rotY: -0.38 + state.assemble * 0.14 };
    } else if (mode === "hero") {
      var hp = state.progress.hero;
      /* portrait: pull the bar further back and lower, so it rests
         under the stacked copy instead of crossing it */
      if ((state.camera.aspect || 1) < 0.8) {
        tgt = {
          grpX: 0.85, grpY: -1.35, camX: 0.2, camY: 0.34,
          dist: fitDistance(19.5), lookX: 0.4, lookY: 0.3,
          rotY: -0.55 + Math.sin(t * 0.4) * 0.05
        };
      } else {
        tgt = {
          grpX: 2.35, grpY: -1.12, camX: 0.3, camY: 0.34 - hp * 0.08,
          dist: fitDistance(17.5 - hp * 0.6), lookX: 0.6, lookY: 0.3,
          rotY: -0.58 + Math.sin(t * 0.4) * 0.05 - hp * 0.3
        };
      }
    } else if (mode === "forge") {
      var portraitF = (state.camera.aspect || 1) < 0.8;
      tgt = {
        grpX: 0, grpY: 0, camX: 0, camY: 0.45,
        /* portrait: fit less of the bar's width so it stays legible,
           and turn less far — end-on, a bar is just a disc */
        dist: portraitF
          ? fitDistance(9.5 + explode * 2.5)
          : fitDistance(9.6 + explode * 6.0),
        lookX: 0, lookY: 0,
        rotY: -0.55 + forgeP * (portraitF ? 2.2 : 2.9)
      };
    } else { /* closing */
      state.spin += dt * 0.26;
      tgt = { grpX: 0.9, grpY: -0.55, camX: 0.3, camY: 0.5, dist: fitDistance((state.camera.aspect || 1) < 0.8 ? 10.5 : 12.6), lookX: 0.6, lookY: 0.2, rotY: state.spin };
    }
    var K = 5; /* heavy-cinematic damping, this page's tone */
    pose.grpX = damp(pose.grpX, tgt.grpX, K, dt);
    pose.grpY = damp(pose.grpY, tgt.grpY, K, dt);
    pose.camX = damp(pose.camX, tgt.camX, K, dt);
    pose.camY = damp(pose.camY, tgt.camY, K, dt);
    pose.dist = damp(pose.dist, tgt.dist, K, dt);
    pose.lookX = damp(pose.lookX, tgt.lookX, K, dt);
    pose.lookY = damp(pose.lookY, tgt.lookY, K, dt);
    /* rotY damps toward target except in closing, where spin is absolute */
    if (mode === "closing") pose.rotY = tgt.rotY;
    else pose.rotY = damp(pose.rotY, tgt.rotY, 5, dt);

    state.barbell.position.x = pose.grpX;
    state.barbell.position.y = pose.grpY;
    state.barbell.rotation.y = pose.rotY;

    /* --- pointer parallax (clamped, eased) --- */
    var ptr = state.pointer;
    ptr.x = damp(ptr.x, ptr.tx, 6, dt);
    ptr.y = damp(ptr.y, ptr.ty, 6, dt);
    state.camera.position.set(
      pose.camX + ptr.x * 0.55,
      pose.camY - ptr.y * 0.32,
      pose.dist
    );
    state.camera.lookAt(pose.lookX + ptr.x * 0.3, pose.lookY - 0.05, 0);

    state.renderer.render(state.scene, state.camera);

    if (!state.firstFrame) {
      state.firstFrame = true;
      state.canvas.classList.add("is-live");
      if (state.opts.onFirstFrame) state.opts.onFirstFrame();
    }
  }

  function start() {
    state.active = true;
    if (!state.ready || state.dead || state.running || document.hidden) return;
    state.running = true;
    state.lastT = 0;
    frame();
  }

  function stop() {
    state.running = false;
    if (state.raf) cancelAnimationFrame(state.raf);
  }

  function setActive(on) {
    if (on) start();
    else { state.active = false; stop(); }
  }

  function dispose() {
    stop();
    if (state.ro) state.ro.disconnect();
    if (!state.ready) return;
    state.scene.traverse(function (obj) {
      if (obj.geometry) obj.geometry.dispose();
      if (obj.material) {
        (Array.isArray(obj.material) ? obj.material : [obj.material]).forEach(function (m) {
          if (m.map) m.map.dispose();
          if (m.bumpMap) m.bumpMap.dispose();
          m.dispose();
        });
      }
    });
    if (state.scene.environment) state.scene.environment.dispose();
    state.renderer.dispose();
    state.ready = false;
  }

  global.GymForge = {
    init: init,
    setMode: setMode,
    getMode: getMode,
    isReady: isReady,
    setProgress: setProgress,
    setPointer: setPointer,
    doRep: doRep,
    start: start,
    stop: stop,
    setActive: setActive,
    resize: resize,
    observeHost: observeHost,
    dispose: dispose
  };
})(typeof window !== "undefined" ? window : globalThis);
