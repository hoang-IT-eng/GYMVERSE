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

   THE FLEX: every bar part (shaft segments, sleeves, plates, collars)
   samples one bend curve per frame — y position + tangent rotation — so
   the whole bar arcs as one piece of steel. Static deflection steps up
   with each unit claimed in the forge chapter (20 → 10 → 5 → collars =
   95 kg) on an underdamped spring, so a plate "slam" visibly bounces the
   bar; the closing rep adds whip (dip deepens the sag, the press drive
   kicks an oscillation that decays through the plates' rattle). A small
   chalk-dust point burst puffs off the grip at each slam and at the
   press. Bend is exaggerated ~3× past a real 95 kg flex so the arc
   reads in profile; the forge camera holds a near-side profile angle
   while the load peaks, and the group lifts as it bends so the ends
   stay clear of the floor — centre up, ends sagging: the deadlift arc.
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
    shaftParts: [],       // [{obj, x}] every rigid shaft piece, flexed per frame
    keyLight: null, fillLight: null, groundMat: null, pool: null,
    mood: 0, poolScale: 1, // profile-hold light mood + contact-pool spread, damped
    bend: 0, bendV: 0,    // static deflection spring (world units at the bar ends)
    whip: 0, whipV: 0,    // dynamic rep oscillation, same units
    prevHighlight: -1, pressGo: false,
    chalk: null,          // {points, pos, col, vel, life, cursor} dust pool
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

  function dotTexture(THREE) {
    var c = document.createElement("canvas");
    c.width = c.height = 64;
    var x = c.getContext("2d");
    var g = x.createRadialGradient(32, 32, 2, 32, 32, 30);
    g.addColorStop(0, "rgba(255,255,255,0.9)");
    g.addColorStop(0.4, "rgba(255,255,255,0.38)");
    g.addColorStop(1, "rgba(255,255,255,0)");
    x.fillStyle = g;
    x.fillRect(0, 0, 64, 64);
    return new THREE.CanvasTexture(c);
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
      color: 0x9aa1a6, metalness: 1.0, roughness: 0.5,
      bumpMap: knurl, bumpScale: 0.55, envMapIntensity: 0.95
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
    var shaftParts = [];

    function seg(r, len, mat, cx) {
      var m = new THREE.Mesh(new THREE.CylinderGeometry(r, r, len, 28), mat);
      m.rotation.z = Math.PI / 2;
      m.position.x = cx;
      m.castShadow = true;
      bar.add(m);
      shaftParts.push({ obj: m, x: cx });
      return m;
    }
    /* long spans are built from short sub-segments (slight length
       overlap) so the flex pass can arc the shaft as a polyline instead
       of a few stiff chords — the joints hide under plates and flanges */
    function segRun(r, len, mat, cx) {
      var n = Math.max(1, Math.ceil(len / 0.55));
      var sub = len / n;
      for (var i = 0; i < n; i++) {
        var m = mat;
        if (mat === knurlMat) {
          /* the bump texture maps 0..1 across each mesh, so a short
             sub-segment squeezes the whole crosshatch into a few
             centimetres and crushes it to black — the centre knurl
             read as a gap in the bar. Scale the repeat with the
             segment length so the diamonds keep one size end to end. */
          var kt = knurl.clone();
          kt.repeat.set(14 * sub / 1.14, 1);
          kt.needsUpdate = true;
          m = knurlMat.clone();
          m.bumpMap = kt;
        }
        seg(r, sub + 0.035, m, cx - len / 2 + sub * (i + 0.5));
      }
    }
    /* shaft: knurled grips + centre knurl, smooth between, sleeves outboard.
       Radii keep real proportions against the 450 mm plates (1 unit ≈
       150 mm): 29 mm shaft, 50 mm sleeves. */
    segRun(0.078, 0.62, knurlMat, 0);              // centre knurl
    seg(0.078, 0.58, shaftMat, 0.60);
    seg(0.078, 0.58, shaftMat, -0.60);             // (was missing: a 0.58 hole in the left shaft)
    segRun(0.078, 1.14, knurlMat, 1.46);           // right grip knurl
    segRun(0.078, 1.14, knurlMat, -1.46);          // left grip knurl
    seg(0.078, 0.34, shaftMat, 2.20);
    seg(0.078, 0.34, shaftMat, -2.20);
    seg(0.108, 0.14, steelDark, 2.42);             // flange / collar stop
    seg(0.108, 0.14, steelDark, -2.42);
    segRun(0.138, 1.30, steel, 3.06);              // sleeve R (2.41 → 3.71)
    segRun(0.138, 1.30, steel, -3.06);             // sleeve L
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

    return { group: bar, units: units, shaftParts: shaftParts };
  }

  /* ---------------- the bend ----------------
     Deflection profile of a held bar, per unit of bend (bend = the
     deflection at the bar ends, in world units): the hands support the
     bar around |x| = BEND_G, so between them the shaft only sags a
     whisper at centre and leaves each hand with zero slope; outboard,
     the plate load pulls the ends down on a cantilever curve whose
     slope keeps growing to the sleeve tip. Past the end cap the shape
     continues on a gentle tangent sag so exploded plates still ride
     the arc. Even in x — bendShape works on |x| and returns
     [y, dShape/d|x|]; bendAt folds the sign back in for the slope. */
  var BEND_MAX = 0.62;
  var BEND_G = 1.05, BEND_END = 3.78;
  var BEND_STEPS = [0, 0.45, 0.70, 0.90, 1.0];  // by claimed unit + 1

  function bendShape(ax) {
    if (ax <= BEND_G) {
      var u = (Math.PI / 2) * (ax / BEND_G);
      var cu = Math.cos(u);
      return [-0.10 * cu * cu, 0.10 * (Math.PI / (2 * BEND_G)) * Math.sin(2 * u)];
    }
    var t = (ax - BEND_G) / (BEND_END - BEND_G);
    var tc = Math.min(t, 1);
    var y = -(0.55 * tc * tc + 0.45 * tc * tc * tc);
    var m = -(1.1 * tc + 1.35 * tc * tc) / (BEND_END - BEND_G);
    if (t > 1) {
      var over = (ax - BEND_END) / BEND_END;
      y -= 0.18 * over;
      m -= 0.18 / BEND_END;
    }
    return [y, m];
  }

  function bendAt(x, bend) {
    var s = bendShape(Math.abs(x));
    return [bend * s[0], Math.atan(bend * s[1] * (x < 0 ? -1 : 1))];
  }

  /* every bar part samples the curve: shaft pieces at their fixed x,
     plates and collars at wherever the assemble/explode offsets have
     currently put them — position from the curve, rotation from its
     tangent, so plates stay seated on the sleeves as the bar arcs */
  function applyFlex(bendTotal) {
    var i, p, r;
    for (i = 0; i < state.shaftParts.length; i++) {
      p = state.shaftParts[i];
      r = bendAt(p.x, bendTotal);
      p.obj.position.y = r[0];
      p.obj.rotation.z = Math.PI / 2 + r[1];
    }
    state.units.forEach(function (unit) {
      unit.plates.forEach(function (pl) {
        var rr = bendAt(pl.group.position.x, bendTotal);
        pl.group.position.y = rr[0];
        pl.group.rotation.z = rr[1];
      });
    });
  }

  /* ---------------- chalk dust ----------------
     One small Points pool, reused for every burst: chalk puffs off the
     grip when a plate slams home in the forge chapter and again at the
     press in the closing. Soft additive motes, gravity + drag, gone in
     under a second. An accent effect, not the scene's motif. */
  var CHALK_N = 200;

  function buildChalk(THREE, parent) {
    var geo = new THREE.BufferGeometry();
    var pos = new Float32Array(CHALK_N * 3);
    var col = new Float32Array(CHALK_N * 3);
    var i;
    for (i = 0; i < CHALK_N; i++) pos[i * 3 + 1] = -999;
    geo.setAttribute("position", new THREE.BufferAttribute(pos, 3));
    geo.setAttribute("color", new THREE.BufferAttribute(col, 3));
    var mat = new THREE.PointsMaterial({
      size: 0.17, map: dotTexture(THREE), transparent: true,
      depthWrite: false, blending: THREE.AdditiveBlending,
      vertexColors: true, opacity: 0.9
    });
    var points = new THREE.Points(geo, mat);
    points.frustumCulled = false;
    parent.add(points);
    return {
      points: points, pos: pos, col: col,
      vel: new Float32Array(CHALK_N * 3),
      life: new Float32Array(CHALK_N), cursor: 0
    };
  }

  function spawnBurst(count, strength) {
    var ch = state.chalk;
    if (!ch || !state.barbell) return;
    var bx = state.barbell.position.x;
    var by = state.barbell.position.y + 0.06;
    for (var k = 0; k < count; k++) {
      var i = ch.cursor;
      ch.cursor = (ch.cursor + 1) % CHALK_N;
      var i3 = i * 3;
      ch.pos[i3] = bx + (Math.random() - 0.5) * 0.5;
      ch.pos[i3 + 1] = by + (Math.random() - 0.5) * 0.22;
      ch.pos[i3 + 2] = (Math.random() - 0.5) * 0.3;
      var a = Math.random() * Math.PI * 2;
      var sp = (0.35 + Math.random() * 0.9) * strength;
      ch.vel[i3] = Math.cos(a) * sp * 0.9;
      ch.vel[i3 + 1] = (0.35 + Math.random() * 0.85) * strength;
      ch.vel[i3 + 2] = Math.sin(a) * sp * 0.55;
      ch.life[i] = 0.55 + Math.random() * 0.45;
    }
  }

  function updateChalk(dt) {
    var ch = state.chalk;
    if (!ch) return;
    var drag = Math.exp(-2.1 * dt);
    var dirty = false;
    for (var i = 0; i < CHALK_N; i++) {
      if (ch.life[i] <= 0) continue;
      dirty = true;
      ch.life[i] -= dt;
      var i3 = i * 3;
      if (ch.life[i] <= 0) {
        ch.pos[i3 + 1] = -999;
        ch.col[i3] = ch.col[i3 + 1] = ch.col[i3 + 2] = 0;
        continue;
      }
      ch.vel[i3] *= drag;
      ch.vel[i3 + 2] *= drag;
      ch.vel[i3 + 1] = ch.vel[i3 + 1] * drag - 1.5 * dt;
      ch.pos[i3] += ch.vel[i3] * dt;
      ch.pos[i3 + 1] += ch.vel[i3 + 1] * dt;
      ch.pos[i3 + 2] += ch.vel[i3 + 2] * dt;
      var f = Math.min(1, ch.life[i] / 0.55) * 0.85;
      ch.col[i3] = f;
      ch.col[i3 + 1] = f * 0.97;
      ch.col[i3 + 2] = f * 0.9;
    }
    if (dirty) {
      ch.points.geometry.attributes.position.needsUpdate = true;
      ch.points.geometry.attributes.color.needsUpdate = true;
    }
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
    state.keyLight = key;
    var fill = new THREE.DirectionalLight(0x9fb4c4, 0.35);
    fill.position.set(-6, 3, -5);
    scene.add(fill);
    state.fillLight = fill;

    /* ground: shadow catcher + a soft painted contact pool */
    var ground = new THREE.Mesh(
      new THREE.PlaneGeometry(46, 46),
      new THREE.ShadowMaterial({ opacity: 0.42 })
    );
    ground.rotation.x = -Math.PI / 2;
    ground.position.y = state.groundY;
    ground.receiveShadow = true;
    scene.add(ground);
    state.groundMat = ground.material;
    var pool = new THREE.Mesh(
      new THREE.PlaneGeometry(15.5, 4.6),
      new THREE.MeshBasicMaterial({
        map: shadowDiscTexture(THREE), transparent: true, depthWrite: false
      })
    );
    pool.rotation.x = -Math.PI / 2;
    pool.position.y = state.groundY + 0.002;
    scene.add(pool);
    state.pool = pool;

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
    state.shaftParts = built.shaftParts;
    state.chalk = buildChalk(THREE, rig);
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
  function getBend() { return state.bend + state.whip; }
  function getProgress() { return state.progress.forge; }

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

  /* forge turntable path: the rotation eases INTO a near-side profile
     (rotY ≈ 0.1) and holds it while the load — and the bend — peaks,
     because a bending bar only reads from the side; then it swings on
     around for the exploded finale. The hold is deliberately long:
     most of the chapter's scroll dwell sits on the bent profile. */
  var FORGE_KEYS = [[0, -0.55], [0.28, 0.84], [0.56, 0.10], [0.78, 0.16], [1, 2.45]];
  var FORGE_KEYS_P = [[0, -0.55], [0.60, 0.05], [1, 1.9]];
  function keyLerp(keys, p) {
    if (p <= keys[0][0]) return keys[0][1];
    for (var i = 1; i < keys.length; i++) {
      if (p <= keys[i][0]) {
        var t = (p - keys[i - 1][0]) / (keys[i][0] - keys[i - 1][0]);
        t = t * t * (3 - 2 * t);
        return keys[i - 1][1] + (keys[i][1] - keys[i - 1][1]) * t;
      }
    }
    return keys[keys.length - 1][1];
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
      if (rep.t >= 0.12) {
        rep.phase = "press"; rep.t = 0; rep.v = 0;
        state.pressGo = true;   // the frame loop turns this into whip + chalk
      }
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

    /* --- explode from forge progress: the plates stay seated while
       the chapter loads and the bar bends; coming apart is the finale --- */
    var forgeP = state.progress.forge;
    var explodeTarget = mode === "forge" ? smoothstep(0.52, 0.98, forgeP) : 0;
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

    /* --- the flex: static bend steps up per claimed unit (a claim in
       the forge chapter slams: spring kick + a puff of chalk), springs
       to full in the closing, and straightens back out in the hero.
       The rep adds whip on top — the dip deepens the sag, the press
       drive kicks a decaying oscillation the plates rattle along with. --- */
    var bendTarget = 0;
    if (mode === "forge") bendTarget = BEND_STEPS[clamp(state.highlight + 1, 0, 4)] * BEND_MAX;
    else if (mode === "closing") bendTarget = BEND_MAX;
    if (mode === "forge" && state.highlight > state.prevHighlight && state.prevHighlight >= 0) {
      state.bendV += 1.5;
      spawnBurst(90, 0.8);
    }
    state.prevHighlight = state.highlight;

    var loadFrac = clamp(state.bend / BEND_MAX, 0, 1);
    if (state.pressGo) {
      state.pressGo = false;
      state.whipV += 5.2 * BEND_MAX * Math.max(loadFrac, 0.12);
      spawnBurst(130, 1.0);
    }
    var whipTarget = 0;
    if (state.rep.phase === "dip") {
      whipTarget = 0.30 * BEND_MAX * loadFrac * clamp(-state.rep.y / 0.52, 0, 1);
    }
    /* substep the two springs: at the frame loop's dt clamp (0.05) a
       single explicit step eats the whip's overshoot; two half-steps
       keep the oscillation honest on slow renderers too */
    var springSteps = dt > 0.024 ? 2 : 1;
    var sh = dt / springSteps;
    for (var si = 0; si < springSteps; si++) {
      state.bendV += ((bendTarget - state.bend) * 48 - state.bendV * 8.5) * sh;
      state.bend += state.bendV * sh;
      state.whipV += ((whipTarget - state.whip) * 120 - state.whipV * 3.0) * sh;
      state.whip += state.whipV * sh;
    }
    if (state.bend < -0.2) { state.bend = -0.2; state.bendV = 0; }
    if (Math.abs(state.whip) > 0.02) {
      state.rep.rattle = Math.max(state.rep.rattle, Math.min(0.7, Math.abs(state.whip) * 1.15));
    }
    applyFlex(state.bend + state.whip);
    updateChalk(dt);

    /* --- mood: while the profile holds, the warm key leans in and the
       cool fill falls away, so the bent bar gets the contrast of the
       photography; the painted contact pool also spreads under the
       bigger forge/closing framings so the shadow anchors the bar.
       Pure light and shadow — the Gunmetal Chalk grade is untouched. --- */
    var moodT = 0, poolT = 1;
    if (mode === "forge") {
      moodT = smoothstep(0.5, 0.64, forgeP) * (1 - smoothstep(0.8, 0.92, forgeP));
      poolT = (state.camera.aspect || 1) < 0.8 ? 1.1 : 1.28;
    } else if (mode === "closing") {
      poolT = 1.12;
    }
    state.mood = damp(state.mood, moodT, 4, dt);
    state.poolScale = damp(state.poolScale, poolT, 4, dt);
    if (state.keyLight) state.keyLight.intensity = 2.6 + 0.55 * state.mood;
    if (state.fillLight) state.fillLight.intensity = 0.35 - 0.13 * state.mood;
    if (state.groundMat) state.groundMat.opacity = 0.42 + 0.05 * state.mood;
    if (state.pool) state.pool.scale.set(state.poolScale, state.poolScale, 1);

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
      if (portraitF) {
        /* portrait: fit less of the bar's width so it stays legible,
           and turn less far — end-on, a bar is just a disc */
        tgt = {
          grpX: 0, grpY: 0, camX: 0, camY: 0.05,
          dist: fitDistance(9.9 + explode * 2.6),
          lookX: 0, lookY: 0.3 + state.bend * 0.7,
          rotY: keyLerp(FORGE_KEYS_P, forgeP)
        };
      } else {
        /* desktop: the bar owns the frame. The camera sits low and
           looks UP at it (heroic angle), and the look height tracks
           the bend lift so the loaded bar rides on a lower-third line
           with its contact shadow anchored beneath it, instead of
           floating at centre in a tall empty stage. The distance fit
           is tuned on the rendered frames: as wide as the profile
           hold can take without the fanned plates kissing the stage
           edges, widening gently for the exploded finale. */
        var lookF = 0.45 + state.bend * 0.9;
        tgt = {
          grpX: 0, grpY: 0, camX: 0, camY: lookF - 1.35,
          dist: fitDistance(10.3 + explode * 3.4),
          lookX: 0, lookY: lookF,
          rotY: keyLerp(FORGE_KEYS, forgeP)
        };
      }
    } else { /* closing */
      state.spin += dt * 0.26;
      tgt = { grpX: 0.9, grpY: -0.55, camX: 0.3, camY: 0.5, dist: fitDistance((state.camera.aspect || 1) < 0.8 ? 9.5 : 11.0), lookX: 0.6, lookY: 0.2, rotY: state.spin };
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
    /* as the bar bends it is also lifted — the lifter holds the centre
       up while the ends sag, so the plates stay clear of the floor and
       the full centre-to-end differential reads as the arc */
    state.barbell.position.y = pose.grpY + state.bend * 0.8;
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
    getBend: getBend,
    getProgress: getProgress,
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
